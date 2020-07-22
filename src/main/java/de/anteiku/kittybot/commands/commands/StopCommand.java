package de.anteiku.kittybot.commands.commands;

import de.anteiku.kittybot.KittyBot;
import de.anteiku.kittybot.commands.ACommand;
import de.anteiku.kittybot.commands.CommandContext;

public class StopCommand extends ACommand{

	public static String COMMAND = "stop";
	public static String USAGE = "stop";
	public static String DESCRIPTION = "Stops me from playing stuff";
	protected static String[] ALIAS = {"s", "quit", "stopp", "stfu"};

	public StopCommand(KittyBot main){
		super(main, COMMAND, USAGE, DESCRIPTION, ALIAS);
		this.main = main;
	}

	@Override
	public void run(CommandContext ctx){
		KittyBot.lavalink.getLink(ctx.getGuild()).destroy();
		sendAnswer(ctx, "Successfully disconnected");
	}

}
