package de.anteiku.kittybot.commands;

import com.google.gson.JsonParser;
import de.anteiku.kittybot.API;
import de.anteiku.kittybot.Emotes;
import de.anteiku.kittybot.KittyBot;
import de.anteiku.kittybot.Logger;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import java.awt.*;
import java.io.IOException;
import java.util.Objects;

public abstract class ImageCommand extends Command{

	public static String COMMAND = "cat";
	public static String USAGE = "cat";
	public static String DESCRIPTION = "Sends a random cat";
	public static String[] ALIAS = {"kitty", "katze"};

	public ImageCommand(KittyBot main){
		super(main, COMMAND, USAGE, DESCRIPTION, ALIAS);
		this.main = main;
	}
	
	protected Message sendGif(Message message, String type){
		try{
			Request request = new Request.Builder().url("https://nekos.life/api/v2/img/" + type).build();
			JsonParser jp = new JsonParser();
			String url = jp.parse(main.httpClient.newCall(request).execute().body().string()).getAsJsonObject().get("url").getAsString();
			EmbedBuilder eb = new EmbedBuilder();
			eb.setImage(url);
			return sendAnswer(message, eb.build());
		}
		catch(IOException e){
			Logger.error(e);
		}
		return null;
	}

}
