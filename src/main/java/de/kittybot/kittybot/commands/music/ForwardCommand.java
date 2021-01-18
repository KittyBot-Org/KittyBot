package de.kittybot.kittybot.commands.music;

import de.kittybot.kittybot.slashcommands.application.Category;
import de.kittybot.kittybot.slashcommands.application.Command;
import de.kittybot.kittybot.slashcommands.application.RunnableCommand;
import de.kittybot.kittybot.slashcommands.context.CommandContext;
import de.kittybot.kittybot.slashcommands.context.Options;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionInteger;
import de.kittybot.kittybot.utils.annotations.Ignore;

@SuppressWarnings("unused")
@Ignore
public class ForwardCommand extends Command implements RunnableCommand{

	public ForwardCommand(){
		super("forward", "Forwards the current song by given amount of seconds", Category.MUSIC);
		addOptions(
				new CommandOptionInteger("seconds", "Seconds to forward")
		);
	}

	@Override
	public void run(Options options, CommandContext ctx){
		// TODO implement
		ctx.error("not implemented yet");
	}

}
