package de.anteiku.kittybot.commands.commands;

import de.anteiku.kittybot.KittyBot;
import de.anteiku.kittybot.commands.ACommand;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Icon;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

public class EmoteStealCommand extends ACommand{

	public static String COMMAND = "steal";
	public static String USAGE = "steal <Emote, Emote, ...> or <url> <name>";
	public static String DESCRIPTION = "Steals some emotes";
	protected static String[] ALIAS = {"grab", "klau"};
	protected static int MAX_EMOTE_SIZE = 256000;

	public EmoteStealCommand(KittyBot main){
		super(main, COMMAND, USAGE, DESCRIPTION, ALIAS);
		this.main = main;
	}

	@Override
	public void run(String[] args, GuildMessageReceivedEvent event){
		if(!event.getMember().hasPermission(Permission.MANAGE_EMOTES)){
			sendError(event, "Sorry you don't have the permission to manage emotes :(");
			return;
		}
		if(!event.getMessage().getAttachments().isEmpty() && args.length > 0){
			var attachment = event.getMessage().getAttachments().get(0); //Users can't add multiple attachments in one message
			var extension = attachment.getFileExtension();
			if(extension != null && (extension.equalsIgnoreCase("png") || extension.equalsIgnoreCase("jpg") ||extension.equalsIgnoreCase("gif") ||extension.equalsIgnoreCase("webp"))){
				attachment.retrieveInputStream().thenAccept(inputStream -> createEmote(event, args[0],inputStream));
			}
			else{
				sendError(event, "The image provided is not a valid image file");
			}
			return;
		}
		List<Emote> emotes = event.getMessage().getEmotes();
		List<Emote> guildEmotes = event.getGuild().getEmotes();
		if(!emotes.isEmpty()){
			for(Emote emote : emotes){
				if(!guildEmotes.contains(emote)){
					createEmote(event, emote.getName(), emote.getImageUrl());
				}
			}
		}
		else if(args.length >= 2){
			createEmote(event, args[1], args[0]);
		}
		else{
			sendUsage(event);
		}
	}

	private void createEmote(GuildMessageReceivedEvent event, String name, String url){
		try{
			createEmote(event, name, new URL(url).openStream());
		}
		catch(MalformedURLException e){
			sendError(event, "Please provide a valid url");
		}
		catch(IOException e){
			LOG.error("Error with stream", e);
			sendError(event, "Error creating emote please try again");
		}
	}

	private void createEmote(GuildMessageReceivedEvent event, String name, InputStream inputStream){
		try{
			if(inputStream.available() > MAX_EMOTE_SIZE){
				sendError(event, "The image provided is bigger than 256kb");
				return;
			}
			event.getGuild().createEmote(name, Icon.from(inputStream)).queue(
				success -> sendAnswer(event, "Emote stolen"),
				failure -> sendError(event, "Error creating emote: " + failure.getMessage()));
		}
		catch(IOException e){
			LOG.error("Error with stream", e);
			sendError(event, "Error creating emote please try again");
		}
	}

}
