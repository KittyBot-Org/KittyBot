package de.anteiku.kittybot.commands.commands;

import de.anteiku.kittybot.KittyBot;
import de.anteiku.kittybot.commands.ACommand;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class TickleCommand extends ACommand{
	
	public static String COMMAND = "tickle";
	public static String USAGE = "tickle <@user, ...>";
	public static String DESCRIPTION = "Tickles a user";
	protected static String[] ALIAS = {"kitzel"};
	
	public TickleCommand(KittyBot main){
		super(main, COMMAND, USAGE, DESCRIPTION, ALIAS);
	}
	
	@Override
	public void run(String[] args, GuildMessageReceivedEvent event){
		if(args.length == 0){
			sendUsage(event);
			return;
		}
		sendReactionImage(event, "tickle", "tickles");
	}
	
}
