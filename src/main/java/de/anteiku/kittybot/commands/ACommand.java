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
		noPermission(event).queue();
	}
	
	protected MessageAction noPermission(GuildMessageReceivedEvent event){
		return error(event, "Sorry you don't have the permission to use this command :(");
	}
	
	/* Send Answer */
	protected void sendAnswer(GuildMessageReceivedEvent event, String answer){
		answer(event, answer).queue();
	}
	
	protected MessageAction answer(GuildMessageReceivedEvent event, String answer){
		return answer(event, new EmbedBuilder()
			.setDescription(answer)
		);
	}
	
	protected MessageAction answer(GuildMessageReceivedEvent event, EmbedBuilder answer){
		addStatus(event.getMessage(), Status.OK);
		return event.getChannel().sendMessage(answer
			.setColor(Color.GREEN)
			.setFooter(event.getMember().getEffectiveName(), event.getAuthor().getEffectiveAvatarUrl())
			.setTimestamp(Instant.now())
			.build()
		);
	}

	protected MessageAction answer(GuildMessageReceivedEvent event, byte[] file, String fileName, EmbedBuilder embed) {
		// add attachment://[the file name with extension] in embed
		return answer(event, embed).addFile(file, fileName);
	}

	protected MessageAction answer(GuildMessageReceivedEvent event, InputStream file, String fileName, EmbedBuilder embed){
		// add attachment://[the file name with extension] in embed
		return answer(event, embed).addFile(file, fileName);
	}
	
	/* Send Error */
	protected void sendError(GuildMessageReceivedEvent event, String error){
		error(event, error).queue();
	}
	
	protected MessageAction error(GuildMessageReceivedEvent event, String error){
		addStatus(event.getMessage(), Status.ERROR);
		return event.getChannel().sendMessage(new EmbedBuilder()
			.setColor(Color.RED)
			.addField("Error:", error, true)
			.setFooter(event.getMember().getEffectiveName(), event.getAuthor().getEffectiveAvatarUrl())
			.setTimestamp(Instant.now())
			.build()
		);
	}
	
	/* Send Usage */
	protected void sendUsage(GuildMessageReceivedEvent event, String usage){
		usage(event, usage).queue();
	}
	
	protected void sendUsage(GuildMessageReceivedEvent event){
		usage(event, usage).queue();
	}
	
	protected MessageAction usage(GuildMessageReceivedEvent event, String usage){
		addStatus(event.getMessage(), Status.QUESTION);
		return event.getChannel().sendMessage(new EmbedBuilder()
			.setColor(Color.ORANGE)
			.addField("Command usage:", "`" + main.database.getCommandPrefix(event.getGuild().getId()) + usage + "`", true)
			.setFooter(event.getMember().getEffectiveName(), event.getAuthor().getEffectiveAvatarUrl())
			.setTimestamp(Instant.now())
			.build()
		);
	}
	
	protected void sendReactionImage(GuildMessageReceivedEvent event, String type, String text){
		reactionImage(event, type, text).queue();
	}
	
	protected MessageAction reactionImage(GuildMessageReceivedEvent event, String type, String text){
		List<User> users = event.getMessage().getMentionedUsers();
		StringBuilder message = new StringBuilder();
		if(users.isEmpty()){
			return error(event, "Please mention a user");
		}
		else if(users.contains(event.getAuthor()) && users.size() == 1){
			message.append("You can't ")
				.append(type)
				.append(" yourself so I ")
				.append(type)
				.append(" you ")
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
			return error(event, "Unknown error occurred while getting image for `" + type + "`");
		}
		return answer(event, new EmbedBuilder().setDescription(message).setImage(url));
	}
	
	protected MessageAction image(GuildMessageReceivedEvent event, String url){
		return answer(event, new EmbedBuilder().setImage(url).setColor(Color.GREEN));
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
	
	protected MessageAction localImage(GuildMessageReceivedEvent event, String image){
		try{
			Request request = new Request.Builder().url("http://anteiku.de:9000/" + image).build();
			String url = main.httpClient.newCall(request).execute().body().string();
			return image(event, url);
		}
		catch(IOException e){
			LOG.error("Error while sending local image", e);
		}
		return null;
	}
	
}
