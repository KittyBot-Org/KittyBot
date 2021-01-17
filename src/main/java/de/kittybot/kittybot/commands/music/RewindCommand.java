package de.kittybot.kittybot.commands.music;

import de.kittybot.kittybot.slashcommands.application.Category;
import de.kittybot.kittybot.slashcommands.application.Command;
import de.kittybot.kittybot.slashcommands.application.RunnableCommand;
import de.kittybot.kittybot.slashcommands.context.CommandContext;
import de.kittybot.kittybot.slashcommands.context.Options;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionInteger;

@SuppressWarnings("unused")
public class RewindCommand extends Command implements RunnableCommand{

	public RewindCommand(){
		super("rewind", "Rewinds the current song by given amount of seconds", Category.MUSIC);
		addOptions(
				new CommandOptionInteger("seconds", "Seconds to rewind")
		);
	}

	@Override
	public void run(Options options, CommandContext ctx){
		// TODO implement
		ctx.error("not implemented yet");
	}

}
