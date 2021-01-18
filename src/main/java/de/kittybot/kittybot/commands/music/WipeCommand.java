package de.kittybot.kittybot.commands.music;

import de.kittybot.kittybot.slashcommands.application.Category;
import de.kittybot.kittybot.slashcommands.application.Command;
import de.kittybot.kittybot.slashcommands.application.RunnableCommand;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionInteger;
import de.kittybot.kittybot.slashcommands.context.CommandContext;
import de.kittybot.kittybot.slashcommands.context.Options;
import de.kittybot.kittybot.utils.annotations.Ignore;

@SuppressWarnings("unused")
@Ignore
public class WipeCommand extends Command implements RunnableCommand{

	public WipeCommand(){
		super("wipe", "Wipes songs from the queue", Category.MUSIC);
		addOptions(
			new CommandOptionInteger("from", "Queue index to delete from").required(),
			new CommandOptionInteger("to", "Queue index inclusive to delete until. Omit for from+1")
		);
	}

	@Override
	public void run(Options options, CommandContext ctx){

	}

}
