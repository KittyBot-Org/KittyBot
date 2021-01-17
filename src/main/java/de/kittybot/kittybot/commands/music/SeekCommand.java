package de.kittybot.kittybot.commands.music;

import de.kittybot.kittybot.slashcommands.application.Category;
import de.kittybot.kittybot.slashcommands.application.Command;
import de.kittybot.kittybot.slashcommands.application.RunnableCommand;
import de.kittybot.kittybot.slashcommands.context.CommandContext;
import de.kittybot.kittybot.slashcommands.context.Options;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionInteger;

@SuppressWarnings("unused")
public class SeekCommand extends Command implements RunnableCommand{

	public SeekCommand(){
		super("seek", "Seeks the current song to given amount of seconds", Category.MUSIC);
		addOptions(
				new CommandOptionInteger("seconds", "Seconds to seek to")
		);
	}

	@Override
	public void run(Options options, CommandContext ctx){
		// TODO implement
		ctx.error("not implemented yet");
	}

}
