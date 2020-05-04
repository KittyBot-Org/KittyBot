package de.anteiku.kittybot.commands.commands;

import de.anteiku.kittybot.KittyBot;
import de.anteiku.kittybot.commands.ACommand;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class StopCommand extends ACommand{
	
	public static String COMMAND = "stop";
	public static String USAGE = "stop";
	public static String DESCRIPTION = "Stops me from playing stuff";
	protected static String[] ALIAS = {"s", "quit", "stopp"};
	
	public StopCommand(KittyBot main){
		super(main, COMMAND, USAGE, DESCRIPTION, ALIAS);
		this.main = main;
	}
	
	@Override
	public void run(String[] args, GuildMessageReceivedEvent event){
		main.lavalink.getLink(event.getGuild()).destroy();
		sendAnswer(event, "Successfully disconnected");
	}
	

}
