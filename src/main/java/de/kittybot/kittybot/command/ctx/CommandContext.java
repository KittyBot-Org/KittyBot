package de.kittybot.kittybot.command.ctx;

import de.kittybot.kittybot.command.Command;
import de.kittybot.kittybot.main.KittyBot;
import de.kittybot.kittybot.main.Main;
import de.kittybot.kittybot.managers.*;
import de.kittybot.kittybot.utils.Config;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import org.apache.commons.collections4.Bag;

import java.awt.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class CommandContext{

	private final GuildMessageReceivedEvent event;
	private final KittyBot main;
	private final String command;
	private final String fullPath;
	private final List<String> args;
	private final String rawMessage;

	public CommandContext(GuildMessageReceivedEvent event, KittyBot main, String fullPath, List<String> args, String rawMessage){
		this.event = event;
		this.main = main;
		this.command = args.get(0);
		this.fullPath = fullPath;
		this.args = args.subList(1, args.size());
		this.rawMessage = rawMessage.replaceFirst(args.get(0), "").trim();
	}

	public CommandContext getChildContext(String fullPath){
		return new CommandContext(this.event, this.main, fullPath, this.args, rawMessage);
	}

	public KittyBot getMain(){
		return this.main;
	}

	public CommandManager getCommandManager(){
		return this.main.getCommandManager();
	}

	public GuildSettingsManager getGuildSettingsManager(){
		return this.main.getGuildSettingsManager();
	}

	public ReactiveMessageManager getReactiveMessageManager(){
		return this.main.getReactiveMessageManager();
	}

	public DashboardSessionManager getDashboardSessionManager(){
		return this.main.getDashboardSessionManager();
	}

	public CommandResponseManager getCommandResponseManager(){
		return this.main.getCommandResponseManager();
	}

	public MessageManager getMessageManager(){
		return this.main.getMessageManager();
	}

	public RequestManager getRequestManager(){
		return this.main.getRequestManager();
	}

	public Config getConfig(){
		return this.main.getConfig();
	}

	public JDA getJDA(){
		return this.event.getJDA();
	}

	public GuildMessageReceivedEvent getEvent(){
		return this.event;
	}

	public String getCommand(){
		return this.command;
	}

	public String getFullPath(){
		return this.fullPath;
	}

	public List<String> getArgs(){
		return this.args;
	}

	public String getRawMessage(){
		return this.rawMessage;
	}

	public User getUser(){
		return this.event.getAuthor();
	}

	public List<User> getMentionedUsers(){
		var users = new ArrayList<>(this.getMessage().getMentionedUsers());
		var selfUser = this.getSelfUser();

		if(this.isMentionCommand()){
			if(this.getMessage().getMentionedUsersBag().getCount(selfUser) == 1){
				users.remove(selfUser);
			}
		}
		return users;
	}

	public Message getMessage(){
		return this.event.getMessage();
	}

	public User getSelfUser(){
		return this.event.getJDA().getSelfUser();
	}

	private boolean isMentionCommand(){
		var content = this.getMessage().getContentRaw();
		var botId = this.getSelfUser().getId();
		return content.startsWith("<@" + botId + ">") || content.startsWith("<@!" + botId + ">");
	}

	public Bag<User> getMentionedUsersBag(){
		var users = this.getMessage().getMentionedUsersBag();
		var selfUser = this.getSelfUser();

		if(this.isMentionCommand()){
			var occurrences = users.getCount(selfUser);
			users.remove(selfUser, occurrences == 1 ? 1 : occurrences - 1);
		}
		return users;
	}

	public List<Member> getMentionedMembers(){
		var members = new ArrayList<>(this.getMessage().getMentionedMembers());
		var selfMember = this.getSelfMember();

		if(this.isMentionCommand()){
			if(this.getMessage().getMentionedUsersBag().getCount(selfMember) == 1){
				members.remove(selfMember);
			}
		}
		return members;
	}

	public Member getSelfMember(){
		return this.getGuild().getSelfMember();
	}

	public Guild getGuild(){
		return this.event.getGuild();
	}

	public List<TextChannel> getMentionedChannels(){
		return this.getMessage().getMentionedChannels();
	}

	public Bag<TextChannel> getMentionedChannelsBag(){
		return this.getMessage().getMentionedChannelsBag();
	}

	public List<Role> getMentionedRoles(){
		return this.getMessage().getMentionedRoles();
	}

	public Bag<Role> getMentionedRolesBag(){
		return this.getMessage().getMentionedRolesBag();
	}

	public void sendNoPermissions(Set<Permission> permissions){
		sendNoPermissions("You are missing the following permissions: " + permissions.stream().map(Permission::getName).collect(Collectors.joining(", ")));
	}

	public void sendNoPermissions(String message){
		addStatus(getMessage(), Status.NO_ENTRY);
		queue(answer(new EmbedBuilder().setColor(Color.RED).setDescription(message)));
	}

	public void addStatus(Message message, Status status){
		message.addReaction(status.getEmoji()).queue(success -> message.removeReaction(status.getEmoji()).queueAfter(5, TimeUnit.SECONDS));
	}

	public void queue(MessageAction messageAction){
		if(messageAction != null){
			messageAction.queue(message -> this.main.getCommandResponseManager().add(getMessage().getIdLong(), message.getIdLong()), null);
		}
	}

	public MessageAction answer(EmbedBuilder answer){
		return answer(getChannel(), getMember(), answer);
	}

	public MessageAction answer(TextChannel channel, Member member, EmbedBuilder answer){
		if(!channel.canTalk()){
			return null;
		}
		return channel.sendMessage(answer.setFooter(member.getEffectiveName(), member.getUser().getEffectiveAvatarUrl())
				.setTimestamp(Instant.now())
				.build()
		);
	}

	public TextChannel getChannel(){
		return this.event.getChannel();
	}

	public Member getMember(){
		return this.event.getMember();
	}

	public void sendNoAdminPermissions(){
		sendNoPermissions("You don't have the permission to use admin commands");
	}

	public void sendError(String error){
		queue(error(error));
	}

	public MessageAction error(String error){
		addStatus(getMessage(), Status.ERROR);
		return answer(new EmbedBuilder().setColor(Color.RED).setDescription(error));
	}

	public void sendSuccess(String answer){
		queue(success(answer));
	}

	public MessageAction success(String answer){
		return success(new EmbedBuilder().setDescription(answer));
	}

	public MessageAction success(EmbedBuilder answer){
		addStatus(getMessage(), Status.OK);
		return answer(answer.setColor(Color.GREEN));
	}

	public void sendSuccess(EmbedBuilder embed){
		queue(success(embed));
	}

	public void sendAnswer(String answer){
		queue(answer(answer));
	}

	public MessageAction answer(String answer){
		return answer(new EmbedBuilder().setDescription(answer));
	}

	public void sendAnswer(EmbedBuilder answer){
		queue(answer(answer));
	}

	public void sendAnswer(TextChannel channel, Member member, EmbedBuilder answer){
		var an = answer(channel, member, answer);
		if(an != null){
			an.queue();
		}
	}

	public void sendUsage(Command cmd){
		queue(usage(cmd.getUsage()));
	}

	public MessageAction usage(String usage){
		addStatus(getMessage(), Status.QUESTION);
		return answer(new EmbedBuilder().setColor(Color.ORANGE).addField("Command usage:", "`." + usage + "`", true));
	}

	public void sendUsage(String usage){
		queue(usage(usage));
	}

	protected enum Status{
		OK("\u2705"),
		ERROR("\u274C"),
		NO_ENTRY("\ud83d\udeab"),
		QUESTION("\u2753");

		private final String emoji;

		Status(String emoji){
			this.emoji = emoji;
		}

		private String getEmoji(){
			return this.emoji;
		}
	}

}
