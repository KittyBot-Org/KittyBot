package de.anteiku.kittybot.objects.command;

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
		return getEvent().getGuild();
	}

	public TextChannel getChannel(){
		return getEvent().getChannel();
	}

	public Message getMessage(){
		return getEvent().getMessage();
	}

	public String getCommand(){
		return this.command;
	}

	public String[] getArgs(){
		return this.args;
	}

	public User getSelfUser(){
		return getEvent().getJDA().getSelfUser();
	}

	public User getUser(){
		return this.event.getAuthor();
	}

	public List<User> getMentionedUsers(){
		return getMessage().getMentionedUsers();
	}

	public Bag<User> getMentionedUsersBag(){
		return getMessage().getMentionedUsersBag();
	}

	public Member getSelfMember(){
		return getGuild().getSelfMember();
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

	public GuildMessageReceivedEvent getEvent(){
		return this.event;
	}

}
