package de.anteiku.kittybot.commands;

import com.google.gson.JsonIOException;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import de.anteiku.kittybot.Emotes;
import de.anteiku.kittybot.KittyBot;
import de.anteiku.kittybot.Logger;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.PrivateChannel;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionAddEvent;
import okhttp3.Request;

import java.awt.*;
import java.io.IOException;

public abstract class Command{
	
	protected KittyBot main;
	protected String command;
	protected String usage;
	protected String description;
	protected String[] alias;
	
	protected Command(KittyBot main, String command, String usage, String description, String[] alias){
		this.main = main;
		this.command = command;
		this.usage = usage;
		this.description = description;
		this.alias = alias;
	}
	
	abstract void run(String[] args, GuildMessageReceivedEvent event);
	
	boolean checkCmd(String cmd){
		if(cmd.equalsIgnoreCase(command)){
			return true;
		}
		for(String a : alias){
			if(a.equalsIgnoreCase(cmd)){
				return true;
			}
		}
		return false;
	}
	
	public String[] getAlias(){
		return alias;
	}
	
	public String getCommand(){
		return command;
	}
	
	public String getDescription(){
		return description;
	}
	
	public String getUsage(){
		return usage;
	}
	
	public void reactionAdd(Message command, GuildMessageReactionAddEvent event){
		if(event.getReactionEmote().getName().equals(Emotes.WASTEBASKET.get())){
			event.getChannel().deleteMessageById(event.getMessageId()).queue();
			command.delete().queue();
		}
		else if(event.getReactionEmote().getName().equals(Emotes.QUESTION.get())){
			event.getReaction().removeReaction(event.getUser()).queue();
			sendUsage(event.getChannel());
		}
	}
	
	protected Message sendPrivate(PrivateChannel channel, String message){
		EmbedBuilder eb = new EmbedBuilder();
		eb.setColor(Color.GREEN);
		eb.setDescription(message);
		
		return channel.sendMessage(eb.build()).complete();
	}
	
	protected Message sendPrivate(Message message, MessageEmbed eb){
		return message.getAuthor().openPrivateChannel().complete().sendMessage(eb).complete();
	}
	
	protected Message sendPrivate(Message message, String msg){
		return message.getAuthor().openPrivateChannel().complete().sendMessage(new EmbedBuilder().setDescription(msg).build()).complete();
	}
	
	protected Message sendAnswer(Message message, String answer){
		message.addReaction(Emotes.CHECK.get()).queue();
		return sendAnswer(message.getTextChannel(), answer);
	}
	
	protected Message sendAnswer(Message message, MessageEmbed answer){
		message.addReaction(Emotes.CHECK.get()).queue();
		return sendAnswer(message.getTextChannel(), answer);
	}
	
	protected Message sendAnswer(TextChannel channel, String answer){
		EmbedBuilder eb = new EmbedBuilder();
		eb.setColor(Color.GREEN);
		eb.setDescription(answer);
		
		return channel.sendMessage(eb.build()).complete();
	}
	
	protected Message sendAnswer(TextChannel channel, MessageEmbed answer){
		return channel.sendMessage(answer).complete();
	}
	
	protected Message sendError(Message message, String error){
		message.addReaction(Emotes.X.get()).queue();
		return sendError(message, error);
	}
	
	protected Message sendError(TextChannel channel, String error){
		EmbedBuilder eb = new EmbedBuilder();
		eb.setColor(Color.RED);
		eb.addField("Error:", "`" + error + "`", true);
		
		return channel.sendMessage(eb.build()).complete();
	}
	
	protected Message sendUsage(Message message){
		return sendUsage(message.getTextChannel(), usage);
	}
	
	protected Message sendUsage(Message message, String usage){
		message.addReaction(Emotes.QUESTION.get()).queue();
		return sendUsage(message.getTextChannel(), usage);
	}
	
	protected Message sendUsage(TextChannel channel){
		return sendUsage(channel, usage);
	}
	
	protected Message sendUsage(TextChannel channel, String usage){
		EmbedBuilder eb = new EmbedBuilder();
		eb.setColor(Color.ORANGE);
		eb.addField("Command usage:", "`" + main.database.getCommandPrefix(channel.getGuild().getId()) + usage + "`", true);
		
		return channel.sendMessage(eb.build()).complete();
	}
	
	protected Message sendImage(TextChannel channel, String url){
		EmbedBuilder eb = new EmbedBuilder();
		eb.setImage(url);
		return sendAnswer(channel, eb.build());
	}
	
	protected String getNeko(String type){
		try{
			Request request = new Request.Builder().url("https://nekos.life/api/v2/img/" + type).build();
			JsonParser jp = new JsonParser();
			return jp.parse(main.httpClient.newCall(request).execute().body().string()).getAsJsonObject().get("url").getAsString();
		}
		catch(IOException e){
			Logger.error(e);
		}
		return null;
	}
	
	protected Message sendNeko(Message message, String type){
		return sendImage(message.getTextChannel(), getNeko(type));
	}
	
	protected Message sendUnsplashImage(Message message, String search){
		try{
			Request request = new Request.Builder().url("https://unsplash.com/photos/random/?client_id=" + main.unsplashClientId + "&query=" + search).build();
			JsonParser jp = new JsonParser();
			String url = jp.parse(main.httpClient.newCall(request).execute().body().string()).getAsJsonObject().get("urls").getAsJsonObject().get("regular").getAsString();
			return sendImage(message.getTextChannel(), url);
		}
		catch(JsonIOException | JsonSyntaxException | IOException e){
			Logger.error(e);
		}
		return null;
	}
	
	protected Message sendLocalImage(Message message, String image){
		try{
			Request request = new Request.Builder().url("http://anteiku.de:9000/" + image).build();
			JsonParser jp = new JsonParser();
			String url = jp.parse(main.httpClient.newCall(request).execute().body().string()).getAsJsonObject().get("urls").getAsJsonObject().get("regular").getAsString();
			return sendImage(message.getTextChannel(), url);
		}
		catch(IOException e){
			Logger.error(e);
		}
		return null;
	}
	
}
