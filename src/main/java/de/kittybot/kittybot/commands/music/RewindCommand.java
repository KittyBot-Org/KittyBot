package de.kittybot.kittybot.commands.music;

import de.kittybot.kittybot.objects.command.ACommand;
import de.kittybot.kittybot.objects.command.Category;
import de.kittybot.kittybot.objects.command.CommandContext;
import de.kittybot.kittybot.utils.MusicUtils;

public class RewindCommand extends ACommand{

	public static final String COMMAND = "rewind";
	public static final String USAGE = "rewind <seconds>";
	public static final String DESCRIPTION = "Rewinds the current song by given amount of seconds";
	protected static final String[] ALIASES = {};
	protected static final Category CATEGORY = Category.MUSIC;

	public RewindCommand(){
		super(COMMAND, USAGE, DESCRIPTION, ALIASES, CATEGORY);
	}

	@Override
	public void run(CommandContext ctx){
		MusicUtils.seekTrack(ctx);
	}

}
