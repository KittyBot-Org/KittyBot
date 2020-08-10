package de.anteiku.kittybot.commands.commands;

import de.anteiku.kittybot.commands.ACommand;
import de.anteiku.kittybot.commands.CommandContext;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Icon;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class EmoteStealCommand extends ACommand{

	public static final String COMMAND = "steal";
	public static final String USAGE = "steal <Emote, Emote, ...> or <url> <name>";
	public static final String DESCRIPTION = "Steals some emotes";
	protected static final String[] ALIAS = {"grab", "klau"};
	protected static final int MAX_EMOTE_SIZE = 256000;

	public EmoteStealCommand(){
		super(COMMAND, USAGE, DESCRIPTION, ALIAS);
	}

	@Override
	public void run(CommandContext ctx){
		if(!ctx.getMember().hasPermission(Permission.MANAGE_EMOTES)){
			sendError(ctx, "Sorry you don't have the permission to manage emotes :(");
			return;
		}
		if(!ctx.getMessage().getAttachments().isEmpty() && ctx.getArgs().length > 0){
			var attachment = ctx.getMessage().getAttachments().get(0); //Users can't add multiple attachments in one message
			var extension = attachment.getFileExtension();
			if(extension != null && (extension.equalsIgnoreCase("png") || extension.equalsIgnoreCase("jpg") || extension.equalsIgnoreCase("gif") || extension.equalsIgnoreCase("webp"))){
				attachment.retrieveInputStream().thenAccept(inputStream -> createEmote(ctx, ctx.getArgs()[0], inputStream));
			}
			else{
				sendError(ctx, "The image provided is not a valid image file");
			}
			return;
		}
		List<Emote> emotes = ctx.getMessage().getEmotes();
		List<Emote> guildEmotes = ctx.getGuild().getEmotes();
		if(!emotes.isEmpty()){
			for(Emote emote : emotes){
				if(!guildEmotes.contains(emote)){
					createEmote(ctx, emote.getName(), emote.getImageUrl());
				}
			}
		}
		else if(ctx.getArgs().length >= 2){
			createEmote(ctx, ctx.getArgs()[1], ctx.getArgs()[0]);
		}
		else{
			sendUsage(ctx);
		}
	}

	private void createEmote(CommandContext ctx, String name, InputStream inputStream){
		try{
			if(inputStream.available() > MAX_EMOTE_SIZE){
				sendError(ctx, "The image provided is bigger than 256kb");
				return;
			}
			ctx.getGuild().createEmote(name, Icon.from(inputStream)).queue(
					success -> sendAnswer(ctx, "Emote stolen"),
					failure -> sendError(ctx, "Error creating emote: " + failure.getMessage()));
		}
		catch(IOException e){
			LOG.error("Error with stream", e);
			sendError(ctx, "Error creating emote please try again");
		}
	}

	private void createEmote(CommandContext ctx, String name, String url){
		try{
			createEmote(ctx, name, new URL(url).openStream());
		}
		catch(MalformedURLException e){
			sendError(ctx, "Please provide a valid url");
		}
		catch(IOException e){
			LOG.error("Error with stream", e);
			sendError(ctx, "Error creating emote please try again");
		}
	}

}
