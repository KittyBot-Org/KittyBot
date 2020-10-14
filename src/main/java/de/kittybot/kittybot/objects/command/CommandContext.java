package de.kittybot.kittybot.objects.command;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.apache.commons.collections4.Bag;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandContext{

	private final GuildMessageReceivedEvent event;
	private final String command;
	private final String[] args;

	public CommandContext(GuildMessageReceivedEvent event, String command, String message){
		this.event = event;
		this.command = command;
		this.args = this.getCommandArguments(message);
	}

	private String[] getCommandArguments(String message){
		String[] args = message.split(" ");
		return Arrays.copyOfRange(args, 1, args.length);
	}

	public JDA getJDA(){
		return this.event.getJDA();
	}

	public TextChannel getChannel(){
		return this.event.getChannel();
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

	public Bag<User> getMentionedUsersBag(){
		var users = this.getMessage().getMentionedUsersBag();
		var selfUser = this.getSelfUser();

		if(this.isMentionCommand()){
			var occurrences = users.getCount(selfUser);
			users.remove(selfUser, occurrences == 1 ? 1 : occurrences - 1);
		}
		return users;
	}

	public Member getMember(){
		return this.event.getMember();
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

}
