package de.kittybot.kittybot.main.commands.botowner;

import de.kittybot.kittybot.command.old.Args;
import de.kittybot.kittybot.command.Category;
import de.kittybot.kittybot.command.old.Command;
import de.kittybot.kittybot.command.old.CommandContext;

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
