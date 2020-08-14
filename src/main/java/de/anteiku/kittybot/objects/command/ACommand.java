package de.anteiku.kittybot.objects.command;

import com.google.gson.JsonParser;
import de.anteiku.kittybot.KittyBot;
import de.anteiku.kittybot.database.Database;
import de.anteiku.kittybot.objects.Cache;
import de.anteiku.kittybot.objects.Emotes;
import de.anteiku.kittybot.objects.ReactiveMessage;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
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

	public final String command;
	public final String usage;
	public final String description;
	public final String[] alias;

	public ACommand(String command, String usage, String description, String[] alias){
		this.command = command;
		this.usage = usage;
		this.description = description;
		this.alias = alias;
	}

	public abstract void run(CommandContext ctx);

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
			Cache.removeReactiveMessage(event.getGuild(), event.getMessageId());
		}
	}

	public void queue(MessageAction messageAction, CommandContext ctx){
		if(messageAction != null){
			messageAction.queue(success -> Cache.addCommandResponse(ctx.getMessage(), success), failure -> sendError(ctx, "There was an error processing your command!\nError: " + failure.getLocalizedMessage()));
		}
	}

	public void sendPrivateMessage(CommandContext ctx, EmbedBuilder eb){
		privateMessage(ctx, eb).queue(null,
				failure -> sendError(ctx, "There was an error processing your command!\nError: " + failure.getLocalizedMessage())
		);
	}

	public RestAction<Message> privateMessage(CommandContext ctx, EmbedBuilder eb){
		return ctx.getUser().openPrivateChannel().flatMap(
				privateChannel -> privateChannel.sendMessage(eb.setTimestamp(Instant.now()).build())
		);
	}

	public void sendNoPermission(CommandContext ctx){
		queue(noPermission(ctx), ctx);
	}

	public MessageAction noPermission(CommandContext ctx){
		return error(ctx, "Sorry you don't have the permission to use this command :(");
	}

	public void sendError(CommandContext ctx, String error){
		queue(error(ctx, error), ctx);
	}

	public MessageAction error(CommandContext ctx, String error){
		addStatus(ctx.getMessage(), Status.ERROR);
		return ctx.getChannel().sendMessage(new EmbedBuilder()
				.setColor(Color.RED)
				.addField("Error:", error, true)
				.setFooter(ctx.getMember().getEffectiveName(), ctx.getUser().getEffectiveAvatarUrl())
				.setTimestamp(Instant.now())
				.build()
		);
	}

	public void sendAnswer(CommandContext ctx, String answer){
		queue(answer(ctx, answer), ctx);
	}

	public void sendAnswer(CommandContext ctx, EmbedBuilder embed){
		queue(answer(ctx, embed), ctx);
	}

	public MessageAction answer(CommandContext ctx, String answer){
		return answer(ctx, new EmbedBuilder().setDescription(answer));
	}

	public MessageAction answer(CommandContext ctx, byte[] file, String fileName, EmbedBuilder embed){
		// add attachment://[the file name with extension] in embed
		return answer(ctx, embed).addFile(file, fileName);
	}

	public MessageAction answer(CommandContext ctx, EmbedBuilder answer){
		addStatus(ctx.getMessage(), Status.OK);
		if(ctx.getGuild().getSelfMember().hasPermission(ctx.getChannel(), Permission.MESSAGE_WRITE)){
			return ctx.getChannel().sendMessage(answer
					.setColor(Color.GREEN)
					.setFooter(ctx.getMember().getEffectiveName(), ctx.getUser().getEffectiveAvatarUrl())
					.setTimestamp(Instant.now())
					.build()
			);
		}
		return null;
	}

	public void addStatus(Message message, Status status){
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

	public MessageAction answer(CommandContext ctx, InputStream file, String fileName, EmbedBuilder embed){
		// add attachment://[the file name with extension] in embed
		return answer(ctx, embed).addFile(file, fileName);
	}

	public void sendUsage(CommandContext ctx){
		queue(usage(ctx, usage), ctx);
	}

	public void sendUsage(CommandContext ctx, String usage){
		queue(usage(ctx, usage), ctx);
	}

	public MessageAction usage(CommandContext ctx, String usage){
		addStatus(ctx.getMessage(), Status.QUESTION);
		return answer(ctx, new EmbedBuilder()
				.setColor(Color.ORANGE)
				.addField("Command usage:", "`" + Database.getCommandPrefix(ctx.getGuild().getId()) + usage + "`", true)
				.setFooter(ctx.getMember().getEffectiveName(), ctx.getUser().getEffectiveAvatarUrl())
				.setTimestamp(Instant.now())
		);
	}

	public void sendReactionImage(CommandContext ctx, String type, String text){
		queue(reactionImage(ctx, type, text), ctx);
	}

	public MessageAction reactionImage(CommandContext ctx, String type, String text){
		List<User> users = ctx.getMessage().getMentionedUsers();
		StringBuilder message = new StringBuilder();
		if(users.isEmpty()){
			return error(ctx, "Please mention a user");
		}
		else if(users.contains(ctx.getUser()) && users.size() == 1){
			message.append("You can't ").append(type).append(" yourself so I ").append(type).append(" you ").append(ctx.getUser().getAsMention()).append("!");
		}
		else{
			message.append(ctx.getUser().getAsMention()).append(" ").append(text).append(" ");

			for(User user : users){
				if(user.getId().equals(ctx.getUser().getId())){
					continue;
				}
				message.append(user.getAsMention()).append(", ");
			}
			if(message.lastIndexOf(",") != -1){
				message.deleteCharAt(message.lastIndexOf(","));
			}
		}
		String url = getNeko(type);
		if(url == null){
			return error(ctx, "Unknown error occurred while getting image for `" + type + "`");
		}
		return answer(ctx, new EmbedBuilder().setDescription(message).setImage(url));
	}

	public String getNeko(String type){
		try{
			Request request = new Request.Builder().url("https://nekos.life/api/v2/img/" + type).build();
			return JsonParser.parseString(KittyBot.httpClient.newCall(request).execute().body().string()).getAsJsonObject().get("url").getAsString();
		}
		catch(IOException e){
			LOG.error("Error while retrieving Neko", e);
		}
		return null;
	}

	public MessageAction image(CommandContext ctx, String url){
		return answer(ctx, new EmbedBuilder().setImage(url).setColor(Color.GREEN));
	}

	public enum Status{
		OK,
		ERROR,
		QUESTION
	}

}
