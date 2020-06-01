package de.anteiku.kittybot.commands.commands;

import de.anteiku.kittybot.KittyBot;
import de.anteiku.kittybot.commands.ACommand;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class SpankCommand extends ACommand{

	public static String COMMAND = "spank";
	public static String USAGE = "spank <@user, ...>";
	public static String DESCRIPTION = "Spanks a user";
	protected static String[] ALIAS = {};

	public SpankCommand(KittyBot main){
		super(main, COMMAND, USAGE, DESCRIPTION, ALIAS);
	}

	@Override
	public void run(String[] args, GuildMessageReceivedEvent event){
		if(!event.getChannel().isNSFW()){
			sendError(event, "Sorry but this command can only be used in nsfw channels");
			return;
		}
		if(args.length == 0){
			sendUsage(event);
			return;
		}
		sendReactionImage(event, "spank", "spanks");
	}

}
