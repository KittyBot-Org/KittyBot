package de.kittybot.kittybot.utils;

import de.kittybot.kittybot.slashcommands.interaction.GuildInteraction;
import de.kittybot.kittybot.slashcommands.interaction.response.InteractionResponse;
import net.dv8tion.jda.api.entities.Icon;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class EmoteHelper{

	private static final int MAX_EMOTE_SIZE = 256000;

	private EmoteHelper(){}

	public static void createEmote(GuildInteraction ia, String name, long emoteId, boolean animated){
		createEmote(ia, name, "https://cdn.discordapp.com/emojis/" + emoteId + "." + (animated ? "gif" : "png"));
	}

	public static void createEmote(GuildInteraction ia, String name, String url){
		try{
			createEmote(ia, name, new URL(url).openStream());
		}
		catch(MalformedURLException e){
			ia.error("Please provide a valid url or emote id");
		}
		catch(IOException e){
			ia.error("Error creating emote please try again\nError: " + e.getMessage());
		}
	}

	public static void createEmote(GuildInteraction ia, String name, InputStream inputStream){
		ia.reply(new InteractionResponse.Builder().ephemeral().setContent("processing...").build());
		try{
			if(inputStream.available() > MAX_EMOTE_SIZE){
				ia.followup("The image provided is bigger than 256kb");
				return;
			}
			ia.getGuild().createEmote(name, Icon.from(inputStream)).queue(success -> ia.followup("Stole emote: " + success.getAsMention()), failure -> ia.followup("Error creating emote: " + failure.getMessage()));
		}
		catch(IOException e){
			ia.followup("Error creating emote please try again\nError: " + e.getMessage());
		}
	}

}
