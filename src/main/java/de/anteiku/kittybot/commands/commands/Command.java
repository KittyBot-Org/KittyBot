package de.anteiku.kittybot.commands.commands;

import de.anteiku.kittybot.KittyBot;
import de.anteiku.kittybot.commands.ACommand;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class Command extends ACommand{
	
	public static String COMMAND = "";
	public static String USAGE = "";
	public static String DESCRIPTION = "";
	protected static String[] ALIAS = {};
	
	public Command(KittyBot main){
		super(main, COMMAND, USAGE, DESCRIPTION, ALIAS);
		this.main = main;
	}
	
	@Override
	public void run(String[] args, GuildMessageReceivedEvent event){
		sendAnswer(event, "this is my command template uwu");
	}
	

}
