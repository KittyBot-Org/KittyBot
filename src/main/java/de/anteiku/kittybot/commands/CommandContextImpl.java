package de.anteiku.kittybot.commands;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.apache.commons.collections4.Bag;

import java.util.List;

public class CommandContextImpl implements CommandContext{

	private final GuildMessageReceivedEvent event;
	private final String command;
	private final String[] args;

	public CommandContextImpl(GuildMessageReceivedEvent event, String command, String[] args){
		this.event = event;
		this.command = command;
		this.args = args;
	}

	public JDA getJDA(){
		return event.getJDA();
	}

	public Guild getGuild(){
		return this.event.getGuild();
	}

	public TextChannel getChannel(){
		return this.event.getChannel();
	}

	public Message getMessage(){
		return this.event.getMessage();
	}

	public String getCommand(){
		return this.command;
	}

	public String[] getArgs(){
		return this.args;
	}

	public User getSelfUser(){
		return this.event.getJDA().getSelfUser();
	}

	public User getUser(){
		return this.event.getAuthor();
	}

	public List<User> getMentionedUsers(){
		return this.event.getMessage().getMentionedUsers();
	}

	public Bag<User> getMentionedUsersBag(){
		return this.event.getMessage().getMentionedUsersBag();
	}

	public Member getSelfMember(){
		return getGuild().getSelfMember();
	}

	public Member getMember(){
		return this.event.getMember();
	}

	public List<TextChannel> getMentionedChannels(){
		return this.event.getMessage().getMentionedChannels();
	}

	public Bag<TextChannel> getMentionedChannelsBag(){
		return this.event.getMessage().getMentionedChannelsBag();
	}

	public List<Role> getMentionedRoles(){
		return this.event.getMessage().getMentionedRoles();
	}

	public Bag<Role> getMentionedRolesBag(){
		return this.event.getMessage().getMentionedRolesBag();
	}

	public GuildMessageReceivedEvent getEvent(){
		return this.event;
	}

}
