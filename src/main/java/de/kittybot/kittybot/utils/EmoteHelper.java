package de.kittybot.kittybot.utils;

import de.kittybot.kittybot.command.context.CommandContext;
import de.kittybot.kittybot.command.response.Response;
import net.dv8tion.jda.api.entities.Icon;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class EmoteHelper{

	private static final int MAX_EMOTE_SIZE = 256000;

	private EmoteHelper(){}

	public static void createEmote(CommandContext ctx, String name, InputStream inputStream){
		ctx.reply(new Response.Builder().ephemeral().setContent("processing...").build());
		try{
			if(inputStream.available() > MAX_EMOTE_SIZE){
				ctx.followup("The image provided is bigger than 256kb");
				return;
			}
			ctx.getGuild().createEmote(name, Icon.from(inputStream)).queue(success -> ctx.followup("Stole emote: " + success.getAsMention()), failure -> ctx.followup("Error creating emote: " + failure.getMessage()));
		}
		catch(IOException e){
			ctx.followup("Error creating emote please try again\nError: " + e.getMessage());
		}
	}

	public static void createEmote(CommandContext ctx, String name, long emoteId, boolean animated){
		createEmote(ctx, name, "https://cdn.discordapp.com/emojis/" + emoteId + "." + (animated ? "gif" : "png"));
	}

	public static void createEmote(CommandContext ctx, String name, String url){
		try{
			createEmote(ctx, name, new URL(url).openStream());
		}
		catch(MalformedURLException e){
			ctx.error("Please provide a valid url or emote id");
		}
		catch(IOException e){
			ctx.error("Error creating emote please try again\nError: " + e.getMessage());
		}
	}
}
