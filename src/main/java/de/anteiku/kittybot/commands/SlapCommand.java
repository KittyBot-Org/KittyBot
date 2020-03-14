package de.anteiku.kittybot.commands;

import de.anteiku.kittybot.KittyBot;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

public class SlapCommand extends ACommand{
	
	public static String COMMAND = "slap";
	public static String USAGE = "slap <@user>";
	public static String DESCRIPTION = "Slaps a user";
	protected static String[] ALIAS = {"schlag"};
	
	public SlapCommand(KittyBot main){
		super(main, COMMAND, USAGE, DESCRIPTION, ALIAS);
	}
	
	@Override
	public void run(String[] args, GuildMessageReceivedEvent event){
		if(args.length == 0){
			sendUsage(event.getMessage());
			return;
		}
		sendReactionImage(event, "slap", "slaps");
	}
	
}
