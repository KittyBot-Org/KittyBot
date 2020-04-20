package de.anteiku.kittybot.commands;

import com.google.gson.JsonParser;
import de.anteiku.kittybot.KittyBot;
import de.anteiku.kittybot.utils.Emotes;
import de.anteiku.kittybot.objects.ReactiveMessage;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import okhttp3.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.TimeUnit;

public abstract class ACommand{
	
	protected static final Logger LOG = LoggerFactory.getLogger(ACommand.class);

	protected KittyBot main;
	protected String command;
	protected String usage;
	protected String description;
	protected String[] alias;
	
	protected enum Status{
		OK,
		ERROR,
		QUESTION
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
	protected RestAction<Message> sendPrivate(Message message, EmbedBuilder eb){
		return message.getAuthor().openPrivateChannel().flatMap(privateChannel -> privateChannel.sendMessage(eb.setTimestamp(Instant.now()).build()));
	}
	
	protected RestAction<Message> sendPrivate(Message message, String msg){
		return sendPrivate(message, new EmbedBuilder().setDescription(msg));
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
	
	protected MessageAction sendAnswer(TextChannel channel, EmbedBuilder answer){
		return channel.sendMessage(answer.setTimestamp(Instant.now()).build());
	}

	protected MessageAction sendAnswer(GuildMessageReceivedEvent event, byte[] file, String fileName, EmbedBuilder embed) {
		// add attachment://[the file name with extension] in embed
		return sendAnswer(event, embed).addFile(file, fileName);
	}

	protected MessageAction sendAnswer(GuildMessageReceivedEvent event, InputStream file, String fileName, EmbedBuilder embed){
		// add attachment://[the file name with extension] in embed
		return sendAnswer(event, embed).addFile(file, fileName);
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
		return sendAnswer(channel, new EmbedBuilder()
			.setColor(Color.GREEN)
			.setDescription(answer)
		);
	}
	
	protected MessageAction sendAnswer(Message message, EmbedBuilder answer){
		addStatus(message, Status.OK);
		return sendAnswer(message.getTextChannel(), answer);
	}
	
	protected MessageAction sendAnswer(GuildMessageReceivedEvent event, EmbedBuilder answer){
		return sendAnswer(event.getMessage(), answer.setAuthor(event.getAuthor().getName(), event.getMessage().getJumpUrl(), event.getAuthor().getAvatarUrl()));
	}
	
	protected MessageAction sendAnswer(TextChannel channel, String answer, String title){
		return sendAnswer(channel, new EmbedBuilder()
			.setColor(Color.GREEN)
			.setTitle(title)
			.setDescription(answer)
		);
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
		return channel.sendMessage(new EmbedBuilder()
			.setColor(Color.RED)
			.addField("Error:", error, true)
			.build()
		);
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
		return channel.sendMessage(new EmbedBuilder()
			.setColor(Color.ORANGE)
			.addField("Command usage:", "`" + main.database.getCommandPrefix(channel.getGuild().getId()) + usage + "`", true)
			.setTimestamp(Instant.now())
			.build()
		);
	}
	
	protected MessageAction sendReactionImage(GuildMessageReceivedEvent event, String type, String text){
		List<User> users = event.getMessage().getMentionedUsers();
		StringBuilder message = new StringBuilder();
		if(users.isEmpty()){
			return sendError(event.getMessage(), "Please mention a user");
		}
		else if(users.contains(event.getAuthor()) && users.size() == 1){
			message.append("You can't ")
				.append(type)
				.append(" yourself so I ")
				.append(type)
				.append("you ")
				.append(event.getAuthor().getAsMention())
				.append("!");
		}
		else{
			message.append(event.getAuthor().getAsMention())
				.append(" ")
				.append(text)
				.append(" ");

			for(User user : users){
				if(user.getId().equals(event.getAuthor().getId())) continue;
				message.append(user.getAsMention()).append(", ");
			}
			if(message.lastIndexOf(",") != - 1){
				message.deleteCharAt(message.lastIndexOf(","));
			}
		}
		String url = getNeko(type);
		if(url == null){
			return sendError(event.getMessage(), "Unknown error occurred while getting image for `" + type + "`");
		}
		return sendAnswer(event.getMessage(), new EmbedBuilder().setDescription(message).setImage(url));
	}
	
	protected MessageAction sendImage(TextChannel channel, String url){
		return sendAnswer(channel, new EmbedBuilder().setImage(url).setColor(Color.GREEN));
	}
	
	protected MessageAction sendImage(Message message, String url){
		return sendAnswer(message, new EmbedBuilder().setImage(url).setColor(Color.GREEN));
	}
	
	protected String getNeko(String type){
		try{
			Request request = new Request.Builder().url("https://nekos.life/api/v2/img/" + type).build();
			return JsonParser.parseString(main.httpClient.newCall(request).execute().body().string()).getAsJsonObject().get("url").getAsString();
		}
		catch(IOException e){
			LOG.error("Error while retrieving Neko", e);
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
			LOG.error("Error while sending local image", e);
		}
		return null;
	}
	
}
