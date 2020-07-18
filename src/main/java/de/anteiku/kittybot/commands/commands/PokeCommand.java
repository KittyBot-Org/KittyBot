package de.anteiku.kittybot.commands.commands;

import de.anteiku.kittybot.KittyBot;
import de.anteiku.kittybot.commands.ACommand;
import de.anteiku.kittybot.commands.CommandContext;

public class PokeCommand extends ACommand{

	public static String COMMAND = "poke";
	public static String USAGE = "poke <@user, ...>";
	public static String DESCRIPTION = "Pokes a user";
	protected static String[] ALIAS = {"stups"};

	public PokeCommand(KittyBot main){
		super(main, COMMAND, USAGE, DESCRIPTION, ALIAS);
	}

	@Override
	public void run(CommandContext ctx){
		if(ctx.getArgs().length == 0){
			sendUsage(ctx);
			return;
		}
		sendReactionImage(ctx, "poke", "pokes");
	}

}
