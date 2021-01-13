package de.kittybot.kittybot.commands.botowner;

import de.kittybot.kittybot.command.Args;
import de.kittybot.kittybot.command.Category;
import de.kittybot.kittybot.command.Command;
import de.kittybot.kittybot.command.context.CommandContext;

@SuppressWarnings("unused")
public class TestCommand extends Command{

	public TestCommand(){
		super("test", "Only for testing weird stuff", Category.BOT_OWNER);
		setBotOwnerOnly();
	}

	@Override
	public void run(Args args, CommandContext ctx){
		ctx.sendSuccess("Test command working!");
	}

}
