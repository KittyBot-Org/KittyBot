package de.anteiku.kittybot.commands;

import com.google.gson.JsonIOException;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import de.anteiku.kittybot.KittyBot;
import de.anteiku.kittybot.utils.Emotes;
import de.anteiku.kittybot.utils.Logger;
import de.anteiku.kittybot.utils.ReactiveMessage;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import okhttp3.Request;

import java.awt.*;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

public abstract class ACommand{
	
	protected KittyBot main;
	protected String command;
	protected String usage;
	protected String description;
	protected String[] alias;
	
	protected enum Status{
		OK, ERROR, QUESTION
	}
	
	protected ACommand(KittyBot main, String command, String usage, String description, String[] alias){
		this.main = main;
		this.command = command;
		this.usage = usage;
		this.description = description;
		this.alias = alias;
	}
	
	public abstract void run(String[] args, GuildMessageReceivedEvent event);
	
	public boolean checkCmd(String cmd){
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
	
	public void reactionAdd(ReactiveMessage reactiveMessage, GuildMessageReactionAddEvent event){
		if(event.getReactionEmote().getName().equals(Emotes.WASTEBASKET.get()) && (event.getUserId().equals(reactiveMessage.userId) || event.getMember().hasPermission(Permission.MESSAGE_MANAGE))){
			event.getChannel().deleteMessageById(event.getMessageId()).queue();
			event.getChannel().deleteMessageById(reactiveMessage.commandId).queue();
			main.commandManager.removeReactiveMessage(event.getGuild(), event.getMessageId());
		}
		else if(event.getReactionEmote().getName().equals(Emotes.QUESTION.get())){
			event.getReaction().removeReaction(event.getUser()).queue();
			sendUsage(event.getChannel());
		}
	}
	
	protected void addStatus(Message message, Status status){
		Emotes emote;
		switch(status){
			case OK:
				emote = Emotes.CHECK;
				break;
			case ERROR:
				emote = Emotes.X;
				break;
			case QUESTION:
			default:
				emote = Emotes.QUESTION;
				break;
		}
		message.addReaction(emote.get()).queue(
			success -> message.getTextChannel().removeReactionById(message.getId(), emote.get()).queueAfter(5, TimeUnit.SECONDS)
		);
	}
	
	/* Send Private Message*/
	protected RestAction<Message> sendPrivate(Message message, MessageEmbed eb){
		return message.getAuthor().openPrivateChannel().flatMap(privateChannel -> privateChannel.sendMessage(eb));
	}
	
	protected RestAction<Message> sendPrivate(Message message, String msg){
		return sendPrivate(message, new EmbedBuilder().setDescription(msg).build());
	}
	
	/* Send No permission Message*/
	protected void sendNoPermission(GuildMessageReceivedEvent event){
		sendNoPermission2(event).queue();
	}
	
	protected MessageAction sendNoPermission2(GuildMessageReceivedEvent event){
		return sendError(event.getMessage(), "Sorry you have no permission to run this command :(");
	}
	
	/* Send Answer */
	protected void sendAnswer(GuildMessageReceivedEvent event, String answer){
		sendAnswer(event.getMessage(), answer).queue();
	}
	
	protected MessageAction sendAnswer(TextChannel channel, MessageEmbed answer){
		return channel.sendMessage(answer);
	}
	
	protected MessageAction sendAnswer(Message message, String answer, String title){
		addStatus(message, Status.OK);
		return sendAnswer(message.getTextChannel(), answer, title);
	}
	
	protected MessageAction sendAnswer(Message message, String answer){
		addStatus(message, Status.OK);
		return sendAnswer(message.getTextChannel(), answer);
	}
	
	protected MessageAction sendAnswer(TextChannel channel, String answer){
		EmbedBuilder eb = new EmbedBuilder();
		eb.setColor(Color.GREEN);
		eb.setDescription(answer);
		return sendAnswer(channel, eb.build());
	}
	
	protected MessageAction sendAnswer(Message message, MessageEmbed answer){
		addStatus(message, Status.OK);
		return sendAnswer(message.getTextChannel(), answer);
	}
	
	protected MessageAction sendAnswer(GuildMessageReceivedEvent event, MessageEmbed answer){
		return sendAnswer(event.getMessage(), answer);
	}
	
	protected MessageAction sendAnswer(TextChannel channel, String answer, String title){
		EmbedBuilder eb = new EmbedBuilder();
		eb.setColor(Color.GREEN);
		eb.setTitle(title);
		eb.setDescription(answer);
		return sendAnswer(channel, eb.build());
	}
	
	/* Send Error */
	protected void sendError(GuildMessageReceivedEvent event, String error){
		sendError(event.getMessage(), error).queue();
	}
	
	protected MessageAction sendError(Message message, String error){
		addStatus(message, Status.ERROR);
		return sendError(message.getTextChannel(), error);
	}
	
	protected MessageAction sendError(TextChannel channel, String error){
		EmbedBuilder eb = new EmbedBuilder();
		eb.setColor(Color.RED);
		eb.addField("Error:", error, true);
		
		return channel.sendMessage(eb.build());
	}
	
	/* Send Usage */
	protected void sendUsage(GuildMessageReceivedEvent event, String usage){
		sendUsage(event.getMessage(), usage).queue();
	}
	
	protected void sendUsage(GuildMessageReceivedEvent event){
		sendUsage(event.getMessage(), usage).queue();
	}
	
	protected MessageAction sendUsage(Message message, String usage){
		addStatus(message, Status.QUESTION);
		return sendUsage(message.getTextChannel(), usage);
	}
	
	protected MessageAction sendUsage(Message message){
		return sendUsage(message.getTextChannel(), usage);
	}
	
	protected MessageAction sendUsage(TextChannel channel){
		return sendUsage(channel, usage);
	}
	
	protected MessageAction sendUsage(TextChannel channel, String usage){
		EmbedBuilder eb = new EmbedBuilder();
		eb.setColor(Color.ORANGE);
		eb.addField("Command usage:", "`" + main.database.getCommandPrefix(channel.getGuild().getId()) + usage + "`", true);
		
		return channel.sendMessage(eb.build());
	}
	
	protected MessageAction sendReactionImage(GuildMessageReceivedEvent event, String type, String text){
		List<User> users = event.getMessage().getMentionedUsers();
		if(users.isEmpty() || users.contains(event.getAuthor())){
			return sendError(event.getMessage(), "You need to mention a User(or not yourself :p)");
		}
		else{
			StringBuilder mentioned = new StringBuilder();
			for(User user : users){
				mentioned.append(user.getAsMention()).append(", ");
			}
			if(mentioned.lastIndexOf(",") != - 1){
				mentioned.deleteCharAt(mentioned.lastIndexOf(","));
			}
			String url = getNeko(type);
			if(url == null){
				return sendError(event.getMessage(), "Unknown error occurred while getting image for `" + type + "`");
			}
			return sendAnswer(event.getMessage(), new EmbedBuilder().setDescription(event.getAuthor().getAsMention() + " " + text + " " + mentioned).setImage(url).build());
		}
	}
	
	protected MessageAction sendImage(TextChannel channel, String url){
		return sendAnswer(channel, new EmbedBuilder().setImage(url).setColor(Color.GREEN).build());
	}
	
	protected MessageAction sendImage(Message message, String url){
		return sendAnswer(message, new EmbedBuilder().setImage(url).setColor(Color.GREEN).build());
	}
	
	protected String getNeko(String type){
		try{
			Request request = new Request.Builder().url("https://nekos.life/api/v2/img/" + type).build();
			return JsonParser.parseString(main.httpClient.newCall(request).execute().body().string()).getAsJsonObject().get("url").getAsString();
		}
		catch(IOException e){
			Logger.error(e);
		}
		return null;
	}
	
	protected MessageAction sendUnsplashImage(Message message, String search){
		try{
			Request request = new Request.Builder().url("https://unsplash.com/photos/random/?client_id=" + main.UNSPLASH_CLIENT_ID + "&query=" + search).build();
			String url = JsonParser.parseString(main.httpClient.newCall(request).execute().body().string()).getAsJsonObject().get("urls").getAsJsonObject().get("regular").getAsString();
			return sendImage(message.getTextChannel(), url);
		}
		catch(JsonIOException | JsonSyntaxException | IOException e){
			Logger.error(e);
		}
		return null;
	}
	
	protected MessageAction sendLocalImage(Message message, String image){
		try{
			Request request = new Request.Builder().url("http://anteiku.de:9000/" + image).build();
			String url = main.httpClient.newCall(request).execute().body().string();
			return sendImage(message.getTextChannel(), url);
		}
		catch(IOException e){
			Logger.error(e);
		}
		return null;
	}
	
}
