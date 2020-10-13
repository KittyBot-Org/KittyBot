package de.kittybot.kittybot.objects.command;

import de.kittybot.kittybot.KittyBot;
import de.kittybot.kittybot.cache.CommandResponseCache;
import de.kittybot.kittybot.cache.GuildSettingsCache;
import de.kittybot.kittybot.cache.ReactiveMessageCache;
import de.kittybot.kittybot.database.Database;
import de.kittybot.kittybot.objects.Emojis;
import de.kittybot.kittybot.objects.ReactiveMessage;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import net.dv8tion.jda.api.utils.data.DataObject;
import okhttp3.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

public abstract class ACommand{

	protected static final Logger LOG = LoggerFactory.getLogger(ACommand.class);

	private final String command;
	protected final String usage;
	protected final String description;
	protected final String[] aliases;
	protected final Category category;

	protected ACommand(String command, String usage, String description, String[] aliases, Category category){
		this.command = command;
		this.usage = usage;
		this.description = description;
		this.aliases = aliases;
		this.category = category;
	}

	protected static void queue(MessageAction messageAction, CommandContext ctx){
		if(messageAction != null){
			messageAction.queue(success -> CommandResponseCache.addCommandResponse(ctx.getMessage(), success), failure -> sendError(ctx, "There was an error processing your command!\nError: " + failure
					.getLocalizedMessage()));
		}
	}

	public static void sendError(CommandContext ctx, String error){
		queue(error(ctx, error), ctx);
	}

	protected static MessageAction error(CommandContext ctx, String error){
		addStatus(ctx.getMessage(), Status.ERROR);
		return ctx.getChannel()
				.sendMessage(new EmbedBuilder().setColor(Color.RED)
						.addField("Error:", error, true)
						.setFooter(ctx.getMember().getEffectiveName(), ctx.getUser().getEffectiveAvatarUrl())
						.setTimestamp(Instant.now())
						.build());
	}

	protected abstract void run(CommandContext ctx);

	protected boolean checkCmd(String cmd){
		if(cmd.equalsIgnoreCase(this.command)){
			return true;
		}
		for(var a : this.aliases){
			if(a.equalsIgnoreCase(cmd)){
				return true;
			}
		}
		return false;
	}

	protected String[] getAliases(){
		return this.aliases;
	}

	public String getCommand(){
		return this.command;
	}

	public String getDescription(){
		return this.description;
	}

	protected String getUsage(){
		return this.usage;
	}

	public Category getCategory(){
		return this.category;
	}

	public void reactionAdd(ReactiveMessage reactiveMessage, GuildMessageReactionAddEvent event){
		if(event.getReactionEmote().getName().equals(Emojis.WASTEBASKET) && (event.getUserId().equals(reactiveMessage.userId) || event.getMember()
				.hasPermission(Permission.MESSAGE_MANAGE))){
			event.getChannel().deleteMessageById(event.getMessageId()).queue();
			event.getChannel().deleteMessageById(reactiveMessage.commandId).queue();
			ReactiveMessageCache.removeReactiveMessage(event.getGuild(), event.getMessageId());
		}
	}

	protected void sendPrivateMessage(CommandContext ctx, EmbedBuilder eb){
		privateMessage(ctx, eb).queue(null, failure -> sendError(ctx, "There was an error processing your command!\nError: " + failure.getLocalizedMessage()));
	}

	protected static RestAction<Message> privateMessage(CommandContext ctx, EmbedBuilder eb){
		return ctx.getUser().openPrivateChannel().flatMap(privateChannel -> privateChannel.sendMessage(eb.setTimestamp(Instant.now()).build()));
	}

	protected static void sendNoPermission(CommandContext ctx){
		queue(noPermission(ctx), ctx);
	}

	protected static MessageAction noPermission(CommandContext ctx){
		return error(ctx, "Sorry you don't have the permission to use this command :(");
	}

	public void sendAnswer(CommandContext ctx, String answer){
		queue(answer(ctx, answer), ctx);
	}

	protected static void sendAnswer(CommandContext ctx, EmbedBuilder embed){
		queue(answer(ctx, embed), ctx);
	}

	protected static MessageAction answer(CommandContext ctx, String answer){
		return answer(ctx, new EmbedBuilder().setDescription(answer));
	}

	protected static MessageAction answer(CommandContext ctx, byte[] file, String fileName, EmbedBuilder embed){
		// add attachment://[the file name with extension] in embed
		return answer(ctx, embed).addFile(file, fileName);
	}

	protected static MessageAction answer(CommandContext ctx, EmbedBuilder answer){
		addStatus(ctx.getMessage(), Status.OK);
		if(ctx.getGuild().getSelfMember().hasPermission(ctx.getChannel(), Permission.MESSAGE_WRITE)){
			return ctx.getChannel()
					.sendMessage(answer.setColor(Color.GREEN)
							.setFooter(ctx.getMember().getEffectiveName(), ctx.getUser().getEffectiveAvatarUrl())
							.setTimestamp(Instant.now())
							.build());
		}
		return null;
	}

	protected static void addStatus(Message message, Status status){
		String emote = switch(status){
			case OK -> Emojis.CHECK;
			case ERROR -> Emojis.X;
			default -> Emojis.QUESTION;
		};
		message.addReaction(emote).queue(success -> message.getTextChannel().removeReactionById(message.getId(), emote).queueAfter(5, TimeUnit.SECONDS));
	}

	protected static MessageAction answer(CommandContext ctx, InputStream file, String fileName, EmbedBuilder embed){
		// add attachment://[the file name with extension] in embed
		return answer(ctx, embed).addFile(file, fileName);
	}

	protected void sendUsage(CommandContext ctx){
		queue(usage(ctx, this.usage), ctx);
	}

	protected static void sendUsage(CommandContext ctx, String usage){
		queue(usage(ctx, usage), ctx);
	}

	protected static MessageAction usage(CommandContext ctx, String usage){
		addStatus(ctx.getMessage(), Status.QUESTION);
		return ctx.getChannel()
				.sendMessage(new EmbedBuilder().setColor(Color.ORANGE)
						.addField("Command usage:", "`" + GuildSettingsCache.getCommandPrefix(ctx.getGuild().getId()) + usage + "`", true)
						.setFooter(ctx.getMember().getEffectiveName(), ctx.getUser().getEffectiveAvatarUrl())
						.setTimestamp(Instant.now())
						.build());
	}

	protected void sendReactionImage(CommandContext ctx, String type, String text){
		queue(this.reactionImage(ctx, type, text), ctx);
	}

	protected MessageAction reactionImage(CommandContext ctx, String type, String text){
		var users = ctx.getMentionedUsers();

		var message = new StringBuilder();
		if(users.isEmpty()){
			return error(ctx, "Please mention a user");
		}
		else if(users.contains(ctx.getUser()) && users.size() == 1){
			message.append("You are not allowed to ")
					.append(type)
					.append(" yourself so I ")
					.append(type)
					.append(" you ")
					.append(ctx.getUser().getAsMention())
					.append("!");
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
		var url = getNeko(type);
		if(url == null){
			return error(ctx, "Unknown error occurred while getting image for `" + type + "`");
		}
		return answer(ctx, new EmbedBuilder().setDescription(message).setImage(url));
	}

	protected static String getNeko(String type){
		try{
			var request = new Request.Builder().url("https://nekos.life/api/v2/img/" + type).build();
			return DataObject.fromJson(KittyBot.getHttpClient().newCall(request).execute().body().string()).getString("url");
		}
		catch(IOException e){
			LOG.error("Error while retrieving Neko", e);
		}
		return null;
	}

	protected static MessageAction image(CommandContext ctx, String url){
		return answer(ctx, new EmbedBuilder().setImage(url).setColor(Color.GREEN));
	}

	protected enum Status{
		OK,
		ERROR,
		QUESTION
	}

}
