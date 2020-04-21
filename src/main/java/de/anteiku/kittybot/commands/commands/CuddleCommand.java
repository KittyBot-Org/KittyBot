package de.anteiku.kittybot.commands.commands;

import de.anteiku.kittybot.KittyBot;
import de.anteiku.kittybot.commands.ACommand;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class CuddleCommand extends ACommand{
	
	public static String COMMAND = "cuddle";
	public static String USAGE = "cuddle <@user, ...>";
	public static String DESCRIPTION = "Cuddles a user";
	protected static String[] ALIAS = {"knuddel"};
	
	public CuddleCommand(KittyBot main){
		super(main, COMMAND, USAGE, DESCRIPTION, ALIAS);
	}
	
	@Override
	public void run(String[] args, GuildMessageReceivedEvent event){
		if(args.length == 0){
			sendUsage(event);
			return;
		}
		sendReactionImage(event, "cuddle", "cuddles").queue();
	}
	
}
