package de.anteiku.kittybot.commands;

import de.anteiku.kittybot.KittyBot;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class KurapikaCommand extends ACommand{
	
	public static String COMMAND = "kurapika";
	public static String USAGE = "kurapika";
	public static String DESCRIPTION = "Sends a random Kurapika image";
	protected static String[] ALIAS = {"kura"};
	
	public KurapikaCommand(KittyBot main){
		super(main, COMMAND, USAGE, DESCRIPTION, ALIAS);
	}
	
	@Override
	public void run(String[] args, GuildMessageReceivedEvent event){
		sendLocalImage(event.getMessage(), "kurapika");
	}
	
}
