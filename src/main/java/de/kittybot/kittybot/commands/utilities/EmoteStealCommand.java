package de.kittybot.kittybot.commands.utilities;

import de.kittybot.kittybot.objects.command.ACommand;
import de.kittybot.kittybot.objects.command.Category;
import de.kittybot.kittybot.objects.command.CommandContext;
import de.kittybot.kittybot.utils.Utils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Icon;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class EmoteStealCommand extends ACommand{

	public static final String COMMAND = "steal";
	public static final String USAGE = "steal <Emote, Emote, ...> or <url> <name> or <id> <name> (<animated true | false>)";
	public static final String DESCRIPTION = "Steals some emotes";
	protected static final String[] ALIASES = {"grab", "klau", "st"};
	protected static final Category CATEGORY = Category.UTILITIES;
	protected static final int MAX_EMOTE_SIZE = 256000;

	public EmoteStealCommand(){
		super(COMMAND, USAGE, DESCRIPTION, ALIASES, CATEGORY);
	}

	@Override
	public void run(CommandContext ctx){
		var args = ctx.getArgs();
		if(!ctx.getMember().hasPermission(Permission.MANAGE_EMOTES)){
			sendError(ctx, "Sorry you don't have the permission to manage emotes :(");
			return;
		}
		if(!ctx.getMessage().getAttachments().isEmpty()){
			var attachment = ctx.getMessage().getAttachments().get(0); //Users can't add multiple attachments in one message
			var extension = attachment.getFileExtension();
			if(extension != null && (extension.equalsIgnoreCase("png") || extension.equalsIgnoreCase("jpg") || extension.equalsIgnoreCase("gif") || extension.equalsIgnoreCase("webp"))){
				attachment.retrieveInputStream().thenAccept(inputStream -> createEmote(ctx, args.length > 0 ? args[0] : extractName(attachment.getFileName()), inputStream));
			}
			else{
				sendError(ctx, "The image provided is not a valid image file");
			}
			return;
		}
		var emotes = ctx.getMessage().getEmotes();
		var guildEmotes = ctx.getGuild().getEmotes();
		if(!emotes.isEmpty()){
			emotes.forEach(emote -> {
				if(!guildEmotes.contains(emote)){
					createEmote(ctx, emote.getName(), emote.getImageUrl());
				}
			});
			return;
		}
		if(args.length == 0){
			sendUsage(ctx);
			return;
		}
		if(Utils.isSnowflake(args[0])){
			if(args.length == 1){
				sendUsage(ctx, "steal <id> <name> (<animated true | false>)");
				return;
			}
			var animated = args.length == 2 || args[2].equalsIgnoreCase("false") ? "png" : "gif";
			createEmote(ctx, args[1], "https://cdn.discordapp.com/emojis/" + args[0] + "." + animated);
			return;
		}
		String name;
		var url = args[0];
		if(args.length >= 2){
			name = args[1];
		}
		else{
			name = extractName(url);
		}
		createEmote(ctx, name, url);
	}

	private void createEmote(CommandContext ctx, String name, InputStream inputStream){
		try{
			if(inputStream.available() > MAX_EMOTE_SIZE){
				sendError(ctx, "The image provided is bigger than 256kb");
				return;
			}
			ctx.getGuild()
					.createEmote(name, Icon.from(inputStream))
					.queue(success -> sendSuccess(ctx, "Emote stolen"), failure -> sendError(ctx, "Error creating emote: " + failure.getMessage()));
		}
		catch(IOException e){
			LOG.error("Error with stream", e);
			sendError(ctx, "Error creating emote please try again");
		}
	}

	private String extractName(String url){
		var start = url.lastIndexOf("/");
		var name = url.substring(start == -1 ? 0 : start + 1);
		var end = name.lastIndexOf(".");
		return name.substring(0, end == -1 ? name.length() - 1 : end);
	}

	private void createEmote(CommandContext ctx, String name, String url){
		try{
			createEmote(ctx, name, new URL(url).openStream());
		}
		catch(MalformedURLException e){
			sendError(ctx, "Please provide a valid url or emote id");
		}
		catch(IOException e){
			LOG.error("Error with stream", e);
			sendError(ctx, "Error creating emote please try again");
		}
	}

}
