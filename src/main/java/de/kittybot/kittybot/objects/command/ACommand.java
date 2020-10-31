package de.kittybot.kittybot.objects.command;

import de.kittybot.kittybot.cache.CommandResponseCache;
import de.kittybot.kittybot.cache.GuildSettingsCache;
import de.kittybot.kittybot.cache.ReactiveMessageCache;
import de.kittybot.kittybot.objects.Emojis;
import de.kittybot.kittybot.objects.data.ReactiveMessage;
import de.kittybot.kittybot.objects.requests.Requester;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

public abstract class ACommand{

	protected static final Logger LOG = LoggerFactory.getLogger(ACommand.class);

	protected final String command;
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

	protected abstract void run(CommandContext ctx);

	public void reactionAdd(ReactiveMessage reactiveMessage, GuildMessageReactionAddEvent event){
		if(event.getReactionEmote().getName().equals(Emojis.WASTEBASKET) && (event.getUserId().equals(reactiveMessage.userId) || event.getMember()
				.hasPermission(Permission.MESSAGE_MANAGE))){
			event.getChannel().deleteMessageById(event.getMessageId()).queue();
			event.getChannel().deleteMessageById(reactiveMessage.commandId).queue();
			ReactiveMessageCache.removeReactiveMessage(event.getGuild(), event.getMessageId());
		}
	}

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

	public String[] getAliases(){
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

	public static void addStatus(Message message, Status status){
		String emote;
		switch(status){
			case OK:
				emote = Emojis.CHECK;
				break;
			case ERROR:
				emote = Emojis.X;
				break;
			default:
				emote = Emojis.QUESTION;
				break;
		}
		message.addReaction(emote).queue(success -> message.getTextChannel().removeReactionById(message.getId(), emote).queueAfter(5, TimeUnit.SECONDS));
	}

	public static void queue(MessageAction messageAction, CommandContext ctx){
		if(messageAction != null){
			messageAction.queue(success -> CommandResponseCache.addCommandResponse(ctx.getMessage(), success), null);
		}
	}

	public static void sendNoPerms(CommandContext ctx){
		queue(error(ctx, "You don't have the permission to use this command"), ctx);
	}

	public static void sendError(CommandContext ctx, String error){
		queue(error(ctx, error), ctx);
	}

	public static MessageAction error(CommandContext ctx, String error){
		addStatus(ctx.getMessage(), Status.ERROR);
		return answer(ctx, new EmbedBuilder().setColor(Color.RED).setDescription(error));
	}

	public static void sendSuccess(CommandContext ctx, String answer){
		queue(success(ctx, answer), ctx);
	}

	public static void sendSuccess(CommandContext ctx, EmbedBuilder embed){
		queue(success(ctx, embed), ctx);
	}

	public static MessageAction success(CommandContext ctx, String answer){
		return success(ctx, new EmbedBuilder().setDescription(answer));
	}

	public static MessageAction success(CommandContext ctx, EmbedBuilder answer){
		addStatus(ctx.getMessage(), Status.OK);
		return answer(ctx, answer.setColor(Color.GREEN));
	}

	public static void sendAnswer(CommandContext ctx, String answer){
		queue(answer(ctx, answer), ctx);
	}

	public static void sendAnswer(CommandContext ctx, EmbedBuilder answer){
		queue(answer(ctx, answer), ctx);
	}

	public static void sendAnswer(TextChannel channel, Member member, EmbedBuilder answer){
		var an = answer(channel, member, answer);
		if(an != null){
			an.queue();
		}
	}

	public static MessageAction answer(CommandContext ctx, String answer){
		return answer(ctx, new EmbedBuilder().setDescription(answer));
	}

	public void sendUsage(CommandContext ctx){
		queue(usage(ctx, this.usage), ctx);
	}

	public static void sendUsage(CommandContext ctx, String usage){
		queue(usage(ctx, usage), ctx);
	}

	public static MessageAction usage(CommandContext ctx, String usage){
		addStatus(ctx.getMessage(), Status.QUESTION);
		return answer(ctx, new EmbedBuilder().setColor(Color.ORANGE).addField("Command usage:", "`" + GuildSettingsCache.getCommandPrefix(ctx.getGuild().getId()) + usage + "`", true));
	}

	public static MessageAction answer(CommandContext ctx, EmbedBuilder answer){
		return answer(ctx.getChannel(), ctx.getMember(), answer);
	}

	public static MessageAction answer(TextChannel channel, Member member, EmbedBuilder answer){
		if(!channel.canTalk()){
			return null;
		}
		return channel.sendMessage(answer.setFooter(member.getEffectiveName(), member.getUser().getEffectiveAvatarUrl())
			.setTimestamp(Instant.now())
			.build()
		);
	}

	protected void sendReactionImage(CommandContext ctx, String type, String text){
		queue(reactionImage(ctx, type, text), ctx);
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

			for(var user : users){
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
		return success(ctx, new EmbedBuilder().setDescription(message).setImage(url));
	}

	protected String getNeko(String type){
		return Requester.getNeko(type);
	}

	protected MessageAction image(CommandContext ctx, String url){
		return answer(ctx, new EmbedBuilder().setImage(url).setColor(Color.GREEN));
	}

	protected enum Status{
		OK,
		ERROR,
		QUESTION
	}

}
