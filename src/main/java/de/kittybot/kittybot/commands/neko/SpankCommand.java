package de.kittybot.kittybot.commands.neko;

import de.kittybot.kittybot.objects.command.ACommand;
import de.kittybot.kittybot.objects.command.Category;
import de.kittybot.kittybot.objects.command.CommandContext;

public class SpankCommand extends ACommand{

	public static final String COMMAND = "spank";
	public static final String USAGE = "spank <@user, ...>";
	public static final String DESCRIPTION = "Spanks a user";
	protected static final String[] ALIASES = {};
	protected static final Category CATEGORY = Category.NEKO;

	public SpankCommand(){
		super(COMMAND, USAGE, DESCRIPTION, ALIASES, CATEGORY);
	}

	@Override
	public void run(CommandContext ctx){
		if(!ctx.getChannel().isNSFW()){
			sendError(ctx, "Sorry but this command can only be used in nsfw channels");
			return;
		}
		if(ctx.getArgs().length == 0){
			this.sendUsage(ctx);
			return;
		}
		this.sendReactionImage(ctx, "spank", "spanks");
	}

}
