package de.anteiku.kittybot.commands.commands;

import de.anteiku.kittybot.KittyBot;
import de.anteiku.kittybot.commands.ACommand;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class HugCommand extends ACommand{

	public static String COMMAND = "hug";
	public static String USAGE = "hug <@user, ...>";
	public static String DESCRIPTION = "Sends a hug to a user";
	protected static String[] ALIAS = {"umarme"};

	public HugCommand(KittyBot main){
		super(main, COMMAND, USAGE, DESCRIPTION, ALIAS);
	}

	@Override
	public void run(String[] args, GuildMessageReceivedEvent event){
		if(args.length == 0){
			sendUsage(event);
			return;
		}
		sendReactionImage(event, "hug", "hugs");
	}

}
