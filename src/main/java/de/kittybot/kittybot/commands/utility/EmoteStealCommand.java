package de.kittybot.kittybot.commands.utility;

import de.kittybot.kittybot.command.Args;
import de.kittybot.kittybot.command.Category;
import de.kittybot.kittybot.command.Command;
import de.kittybot.kittybot.command.ctx.CommandContext;
import de.kittybot.kittybot.utils.Utils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Icon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class EmoteStealCommand extends Command{

	private static final Logger LOG = LoggerFactory.getLogger(EmoteStealCommand.class);
	private static final int MAX_EMOTE_SIZE = 256000;

	public EmoteStealCommand(){
		super("steal", "Steals some emotes", Category.UTILITIES);
		setUsage("<Emote, Emote, ...> or <url> <name> or <id> <name> (<animated true | false>)");
		addAliases("grab", "klau", "st");
	}

	@Override
	public void run(Args args, CommandContext ctx){
		if(!ctx.getMember().hasPermission(Permission.MANAGE_EMOTES)){
			ctx.sendError("Sorry you don't have the permission to manage emotes :(");
			return;
		}
		if(!ctx.getMessage().getAttachments().isEmpty()){
			var attachment = ctx.getMessage().getAttachments().get(0); //Users can't add multiple attachments in one message
			var extension = attachment.getFileExtension();
			if(extension != null && (extension.equalsIgnoreCase("png") || extension.equalsIgnoreCase("jpg") || extension.equalsIgnoreCase("gif") || extension.equalsIgnoreCase("webp"))){
				attachment.retrieveInputStream().thenAccept(inputStream -> createEmote(ctx, args.isEmpty() ? extractName(attachment.getFileName()) : args.get(0), inputStream));
			}
			else{
				ctx.sendError("The image provided is not a valid image file");
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
		if(args.isEmpty()){
			ctx.sendUsage(this);
			return;
		}
		if(Utils.isSnowflake(args.get(0))){
			if(args.size() == 1){
				ctx.sendUsage("steal <id> <name> (<animated true | false>)");
				return;
			}
			var animated = args.size() == 2 || args.get(2).equalsIgnoreCase("false") ? "png" : "gif";
			createEmote(ctx, args.get(1), "https://cdn.discordapp.com/emojis/" + args.get(0) + "." + animated);
			return;
		}
		String name;
		var url = args.get(0);
		if(args.size() >= 2){
			name = args.get(1);
		}
		else{
			name = extractName(url);
		}
		createEmote(ctx, name, url);
	}

	private void createEmote(CommandContext ctx, String name, InputStream inputStream){
		try{
			if(inputStream.available() > MAX_EMOTE_SIZE){
				ctx.sendError("The image provided is bigger than 256kb");
				return;
			}
			ctx.getGuild()
					.createEmote(name, Icon.from(inputStream))
					.queue(success -> ctx.sendSuccess("Emote stolen"), failure -> ctx.sendError("Error creating emote: " + failure.getMessage()));
		}
		catch(IOException e){
			LOG.error("Error with stream", e);
			ctx.sendError("Error creating emote please try again");
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
			ctx.sendError("Please provide a valid url or emote id");
		}
		catch(IOException e){
			LOG.error("Error with stream", e);
			ctx.sendError("Error creating emote please try again");
		}
	}

}
