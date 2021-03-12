package de.kittybot.kittybot.utils;

import de.kittybot.kittybot.slashcommands.GuildCommandContext;
import net.dv8tion.jda.api.entities.Icon;

import java.awt.Color;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class EmoteHelper{

	private static final int MAX_EMOTE_SIZE = 256000;

	private EmoteHelper(){}

	public static void createEmote(GuildCommandContext ctx, String name, long emoteId, boolean animated){
		createEmote(ctx, name, "https://cdn.discordapp.com/emojis/" + emoteId + "." + (animated ? "gif" : "png"));
	}

	public static void createEmote(GuildCommandContext ctx, String name, String url){
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

	public static void createEmote(GuildCommandContext ctx, String name, InputStream inputStream){
		ctx.replyEphemeral("processing...");
		try{
			if(inputStream.available() > MAX_EMOTE_SIZE){
				ctx.getThread().sendMessage("").addEmbeds(ctx.getEmbed().setColor(Color.RED).setDescription("The image provided is bigger than 256kb").build()).queue();
				return;
			}
			ctx.getGuild().createEmote(name, Icon.from(inputStream)).queue(success -> ctx.getThread().sendMessage("").addEmbeds(ctx.getEmbed().setColor(Color.RED).setDescription("Stole emote: " + success.getAsMention()).build()).queue(), failure -> ctx.getThread().sendMessage("").addEmbeds(ctx.getEmbed().setColor(Color.RED).setDescription("Error creating emote: " + failure.getMessage()).build()).queue());
		}
		catch(IOException e){
			ctx.getThread().sendMessage("").addEmbeds(ctx.getEmbed().setColor(Color.RED).setDescription("Error creating emote please try again\nError: " + e.getMessage()).build()).queue();
		}
	}

}
