package de.kittybot.kittybot.objects.command;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.apache.commons.collections4.Bag;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class CommandContext{

	private final GuildMessageReceivedEvent event;
	private final String command;
	private final String[] args;

	public CommandContext(GuildMessageReceivedEvent event, String command, String message){
		this.event = event;
		this.command = command;
		this.args = getCommandArguments(message);
	}

	private String[] getCommandArguments(String message){
		String[] args = message.split(" ");
		return Arrays.copyOfRange(args, 1, args.length);
	}

	public JDA getJDA(){
		return event.getJDA();
	}

	public TextChannel getChannel(){
		return getEvent().getChannel();
	}

	public GuildMessageReceivedEvent getEvent(){
		return this.event;
	}

	public String getCommand(){
		return this.command;
	}

	public String[] getArgs(){
		return this.args;
	}

	public User getUser(){
		return this.event.getAuthor();
	}

	public List<User> getMentionedUsers(){
		var users = new LinkedList<>(getMessage().getMentionedUsers());
		var selfUser = getSelfUser();

		if(isMentionCommand()){
			if(getMessage().getMentionedUsersBag().getCount(selfUser) == 1){
				users.remove(selfUser);
			}
		}
		return users;
	}

	public Message getMessage(){
		return getEvent().getMessage();
	}

	public User getSelfUser(){
		return getEvent().getJDA().getSelfUser();
	}

	private boolean isMentionCommand(){
		var content = getMessage().getContentRaw();
		var botId = getSelfUser().getId();
		return content.startsWith("<@" + botId + ">") || content.startsWith("<@!" + botId + ">");
	}

	public List<Member> getMentionedMembers(){
		var members = new LinkedList<>(getMessage().getMentionedMembers());
		var selfMember = getSelfMember();

		if(isMentionCommand()){
			if(getMessage().getMentionedUsersBag().getCount(selfMember) == 1){
				members.remove(selfMember);
			}
		}
		return members;
	}

	public Member getSelfMember(){
		return getGuild().getSelfMember();
	}

	public Guild getGuild(){
		return getEvent().getGuild();
	}

	public Bag<User> getMentionedUsersBag(){
		var users = getMessage().getMentionedUsersBag();
		var selfUser = getSelfUser();

		if(isMentionCommand()){
			var occurrences = users.getCount(selfUser);
			users.remove(selfUser, occurrences == 1 ? 1 : occurrences - 1);
		}
		return users;
	}

	public Member getMember(){
		return getEvent().getMember();
	}

	public List<TextChannel> getMentionedChannels(){
		return getMessage().getMentionedChannels();
	}

	public Bag<TextChannel> getMentionedChannelsBag(){
		return getMessage().getMentionedChannelsBag();
	}

	public List<Role> getMentionedRoles(){
		return getMessage().getMentionedRoles();
	}

	public Bag<Role> getMentionedRolesBag(){
		return getMessage().getMentionedRolesBag();
	}

}
