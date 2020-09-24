package de.kittybot.kittybot.commands.music;

import de.kittybot.kittybot.objects.command.ACommand;
import de.kittybot.kittybot.objects.command.Category;
import de.kittybot.kittybot.objects.command.CommandContext;
import de.kittybot.kittybot.utils.MusicUtils;

public class SeekCommand extends ACommand{

	public static final String COMMAND = "seek";
	public static final String USAGE = "seek <seconds>";
	public static final String DESCRIPTION = "Seeks the current song to given amount of seconds";
	protected static final String[] ALIASES = {"goto"};
	protected static final Category CATEGORY = Category.MUSIC;

	public SeekCommand(){
		super(COMMAND, USAGE, DESCRIPTION, ALIASES, CATEGORY);
	}

	@Override
	public void run(CommandContext ctx){
		MusicUtils.seekTrack(ctx);
	}

}
