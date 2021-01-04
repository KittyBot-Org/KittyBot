package de.kittybot.kittybot.command;

import de.kittybot.kittybot.main.KittyBot;
import de.kittybot.kittybot.managers.*;
import de.kittybot.kittybot.utils.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import org.apache.commons.collections4.Bag;

import java.awt.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class CommandContext{

	private final GuildMessageReceivedEvent event;
	private final KittyBot main;
	private final String command;
	private final String fullPath;
	private final Args args;
	private final String rawMessage;

	public CommandContext(GuildMessageReceivedEvent event, KittyBot main, String fullPath, Args args, String rawMessage){
		this.event = event;
		this.main = main;
		this.command = args.get(0);
		this.fullPath = fullPath;
		this.args = args.subArgs();
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

	public StreamAnnouncementManager getStreamAnnouncementManager(){
		return this.main.getStreamAnnouncementManager();
	}

	public NotificationManager getNotificationManager(){
		return this.main.getNotificationManager();
	}

	public ReactiveMessageManager getReactiveMessageManager(){
		return this.main.getReactiveMessageManager();
	}

	public DashboardSessionManager getDashboardSessionManager(){
		return this.main.getDashboardSessionManager();
	}

	public TagManager getTagManager(){
		return this.main.getTagManager();
	}

	public InviteManager getInviteManager(){
		return this.main.getInviteManager();
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

	public MusicManager getMusicManager(){
		return this.main.getMusicManager();
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

	public Args getArgs(){
		return this.args;
	}

	public String getRawMessage(){
		return this.rawMessage;
	}

	public String getRawMessage(int toArg){
		var msg = this.rawMessage;
		var i = 0;
		for(var arg : this.args.getList()){
			if(i > args.size() || i > toArg - 1){
				i++;
				continue;
			}
			msg = msg.replaceFirst(arg, "").trim();
			i++;
		}
		return msg;
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

	public long getMessageId(){
		return this.event.getMessage().getIdLong();
	}

	public User getSelfUser(){
		return this.event.getJDA().getSelfUser();
	}

	public boolean isMentionCommand(){
		var content = this.getMessage().getContentRaw();
		var botId = this.getSelfUser().getId();
		return content.startsWith("<@" + botId + ">") || content.startsWith("<@!" + botId + ">");
	}

	public void collectMentionedUsers(Consumer<Set<User>> success, Consumer<Throwable> error){
		var mentionedUsers = new HashSet<>(getMentionedUsers());
		var actions = this.getArgs().stream()
				.filter(Utils::isSnowflake)
				.map(s -> this.event.getJDA().retrieveUserById(s))
				.collect(Collectors.toList());
		if(!actions.isEmpty()){
			RestAction.allOf(
					actions
			).queue(users -> {
				mentionedUsers.addAll(users);
				success.accept(mentionedUsers);
			}, error);
			return;
		}
		success.accept(mentionedUsers);
	}

	public void collectMentionedMembers(Consumer<Set<Member>> success, Consumer<Throwable> error){
		var mentionedMembers = new HashSet<>(getMentionedMembers());
		var actions = this.getArgs().stream()
				.filter(Utils::isSnowflake)
				.map(s -> this.event.getGuild().retrieveMemberById(s))
				.collect(Collectors.toList());
		if(!actions.isEmpty()){
			RestAction.allOf(
					actions
			).queue(members -> {
				mentionedMembers.addAll(members);
				success.accept(mentionedMembers);
			}, error);
		}
		success.accept(mentionedMembers);
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

	public long getGuildId(){
		return this.event.getGuild().getIdLong();
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

	public long getChannelId(){
		return this.event.getChannel().getIdLong();
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

	public void sendBlankSuccess(String msg){
		addStatus(getMessage(), Status.OK);
		queue(getChannel().sendMessage(msg));
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
