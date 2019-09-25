package de.anteiku.kittybot.commands;

import de.anteiku.kittybot.KittyBot;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

public class PatCommand extends Command{

	public static String COMMAND = "pat";
	public static String USAGE = "pat <@user>";
	public static String DESCRIPTION = "Sends a pat to a user";
	public static String[] ALIAS = {"tätschel"};

	public PatCommand(KittyBot main){
		super(main, COMMAND, USAGE, DESCRIPTION, ALIAS);
	}

	@Override
	public void run(String[] args, GuildMessageReceivedEvent event){
		if(args.length != 1){
			sendUsage(event.getMessage());
			return;
		}
		sendReactionImage(event, "pat", "pats");
	}

}
