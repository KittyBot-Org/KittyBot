package de.kittybot.kittybot.commands.botowner;

import de.kittybot.kittybot.command.Category;
import de.kittybot.kittybot.command.Command;
import de.kittybot.kittybot.command.ctx.CommandContext;

import java.util.List;

public class TestCommand extends Command{

	public TestCommand(){
		super("test", "Only for testing weird stuff", Category.BOT_OWNER);
		this.setBotOwnerOnly();
	}

	@Override
	public void run(List<String> args, CommandContext ctx){
		ctx.sendSuccess("Test command working!");
	}

}
