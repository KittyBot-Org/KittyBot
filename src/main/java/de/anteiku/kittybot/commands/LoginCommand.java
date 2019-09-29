package de.anteiku.kittybot.commands;

import de.anteiku.kittybot.KittyBot;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

public class LoginCommand extends ACommand{
	
	public static final String COMMAND = "login";
	public static final String USAGE = "login";
	public static final String DESCRIPTION = "Used to get the login link for the [webinterface](http://anteiku.de/login)";
	protected static final String[] ALIAS = {"webinterface"};
	
	public LoginCommand(KittyBot main){
		super(main, COMMAND, USAGE, DESCRIPTION, ALIAS);
	}
	
	@Override
	public void run(String[] args, GuildMessageReceivedEvent event){
		if(event.getMember().hasPermission(Permission.ADMINISTRATOR) || event.getMember().isOwner()){
			sendAnswer(event.getMessage(), "Click [here](" + main.host + "/guild/" + event.getGuild().getId() + ") to login with discord and manage your guilds.");
		}
		else{
			sendAnswer(event.getMessage(), "Click [here](" + main.host + "/login) to login with discord and manage your guilds.\nYou need at least administrator permissions to manage guilds.");
		}
	}
	
}
