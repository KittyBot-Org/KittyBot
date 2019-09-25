package de.anteiku.kittybot.commands;

import de.anteiku.kittybot.KittyBot;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

public class KissCommand extends Command{

	public static String COMMAND = "kiss";
	public static String USAGE = "kiss <@user>";
	public static String DESCRIPTION = "Sends a kiss to a user";
	public static String[] ALIAS = {"küss"};

	public KissCommand(KittyBot main){
		super(main, COMMAND, USAGE, DESCRIPTION, ALIAS);
	}

	@Override
	public void run(String[] args, GuildMessageReceivedEvent event){
		if(args.length != 1){
			sendUsage(event.getMessage());
			return;
		}
		sendReactionImage(event, "kiss", "kisses");
	}

}
