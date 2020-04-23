package de.anteiku.kittybot.commands.commands;

import de.anteiku.kittybot.KittyBot;
import de.anteiku.kittybot.commands.ACommand;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class BakaCommand extends ACommand{

	public static String COMMAND = "baka";
	public static String USAGE = "baka <@user, ...>";
	public static String DESCRIPTION = "Says baka to a user";
	protected static String[] ALIAS = {"dummy", "dummi"};

	public BakaCommand(KittyBot main){
		super(main, COMMAND, USAGE, DESCRIPTION, ALIAS);
	}
	
	@Override
	public void run(String[] args, GuildMessageReceivedEvent event){
		if(args.length == 0){
			sendUsage(event);
			return;
		}
		sendReactionImage(event, "baka", "said baka to");
	}
	
}
