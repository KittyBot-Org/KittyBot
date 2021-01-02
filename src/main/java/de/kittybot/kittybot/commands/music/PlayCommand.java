package de.kittybot.kittybot.commands.music;

import de.kittybot.kittybot.command.Args;
import de.kittybot.kittybot.command.Category;
import de.kittybot.kittybot.command.Command;
import de.kittybot.kittybot.command.ctx.CommandContext;

public class PlayCommand extends Command{

	public PlayCommand(){
		super("play", "Used to play/search/queue music", Category.MUSIC);
		addAliases("p");
		setUsage("<link/search term>");
	}

	@Override
	public void run(Args args, CommandContext ctx){

	}

}
