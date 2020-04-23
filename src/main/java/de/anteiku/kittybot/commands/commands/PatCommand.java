package de.anteiku.kittybot.commands.commands;

import de.anteiku.kittybot.KittyBot;
import de.anteiku.kittybot.commands.ACommand;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class PatCommand extends ACommand{

	public static String COMMAND = "pat";
	public static String USAGE = "pat <@user, ...>";
	public static String DESCRIPTION = "Pats to a user";
	protected static String[] ALIAS = {"tätschel"};

	public PatCommand(KittyBot main){
		super(main, COMMAND, USAGE, DESCRIPTION, ALIAS);
	}

	@Override
	public void run(String[] args, GuildMessageReceivedEvent event){
		if(args.length == 0){
			sendUsage(event);
			return;
		}
		sendReactionImage(event, "pat", "pats");
	}

}
