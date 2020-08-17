package de.anteiku.kittybot.commands.neko;

import de.anteiku.kittybot.objects.command.ACommand;
import de.anteiku.kittybot.objects.command.Category;
import de.anteiku.kittybot.objects.command.CommandContext;

public class CuddleCommand extends ACommand{

	public static final String COMMAND = "cuddle";
	public static final String USAGE = "cuddle <@user, ...>";
	public static final String DESCRIPTION = "Cuddles a user";
	protected static final String[] ALIAS = {"knuddel"};
	protected static final Category CATEGORY = Category.NEKO;

	public CuddleCommand(){
		super(COMMAND, USAGE, DESCRIPTION, ALIAS, CATEGORY);
	}

	@Override
	public void run(CommandContext ctx){
		if(ctx.getArgs().length == 0){
			sendUsage(ctx);
			return;
		}
		sendReactionImage(ctx, "cuddle", "cuddles");
	}

}
