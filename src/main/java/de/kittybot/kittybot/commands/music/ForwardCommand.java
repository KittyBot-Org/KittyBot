package de.kittybot.kittybot.commands.music;

import de.kittybot.kittybot.objects.command.ACommand;
import de.kittybot.kittybot.objects.command.Category;
import de.kittybot.kittybot.objects.command.CommandContext;
import de.kittybot.kittybot.utils.MusicUtils;

public class ForwardCommand extends ACommand{

	public static final String COMMAND = "forward";
	public static final String USAGE = "forward <seconds>";
	public static final String DESCRIPTION = "Forwards the current song by given amount of seconds";
	protected static final String[] ALIASES = {};
	protected static final Category CATEGORY = Category.MUSIC;

	public ForwardCommand(){
		super(COMMAND, USAGE, DESCRIPTION, ALIASES, CATEGORY);
	}

	@Override
	public void run(CommandContext ctx){
		MusicUtils.seekTrack(ctx);
	}

}
