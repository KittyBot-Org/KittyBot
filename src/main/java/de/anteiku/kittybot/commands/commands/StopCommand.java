package de.anteiku.kittybot.commands.commands;

import de.anteiku.kittybot.KittyBot;
import de.anteiku.kittybot.commands.ACommand;
import de.anteiku.kittybot.commands.CommandContext;

public class StopCommand extends ACommand{

	public static final String COMMAND = "stop";
	public static final String USAGE = "stop";
	public static final String DESCRIPTION = "Stops me from playing stuff";
	protected static final String[] ALIAS = {"s", "quit", "stopp", "stfu"};

	public StopCommand(){
		super(COMMAND, USAGE, DESCRIPTION, ALIAS);
	}

	@Override
	public void run(CommandContext ctx){
		KittyBot.lavalink.getLink(ctx.getGuild()).destroy();
		sendAnswer(ctx, "Successfully disconnected");
	}

}
