package de.anteiku.kittybot.commands;

import de.anteiku.kittybot.KittyBot;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class PokeCommand extends ACommand{
	
	public static String COMMAND = "poke";
	public static String USAGE = "poke <@user>";
	public static String DESCRIPTION = "Pokes a user";
	protected static String[] ALIAS = {"stups"};
	
	public PokeCommand(KittyBot main){
		super(main, COMMAND, USAGE, DESCRIPTION, ALIAS);
	}
	
	@Override
	public void run(String[] args, GuildMessageReceivedEvent event){
		if(args.length == 0){
			sendUsage(event.getMessage());
			return;
		}
		sendReactionImage(event, "poke", "pokes");
	}
	
}
