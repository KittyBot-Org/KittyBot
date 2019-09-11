package de.anteiku.kittybot.commands;

import de.anteiku.kittybot.KittyBot;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

public class LoginCommand extends Command{
	
	public static final String COMMAND = "login";
	public static final String USAGE = "login";
	public static final String DESCRIPTION = "Used to get the login link for the [webinterface](http://anteiku.de/login)";
	public static final String[] ALIAS = {"webinterface"};
	
	public LoginCommand(KittyBot main){
		super(main, COMMAND, USAGE, DESCRIPTION, ALIAS);
	}
	
	@Override
	public void run(String[] args, GuildMessageReceivedEvent event){
		sendAnswer(event.getChannel(), "Click [here](http://anteiku.de/login) to login with discord and manage your guilds!\nDo not forget that you need administrator permissions to manage them!");
	}
	
}
