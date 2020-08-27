package de.anteiku.kittybot.commands.music;

import de.anteiku.kittybot.command.ACommand;
import de.anteiku.kittybot.command.Category;
import de.anteiku.kittybot.command.CommandContext;
import de.anteiku.kittybot.objects.cache.MusicPlayerCache;

public class StopCommand extends ACommand{

	public static final String COMMAND = "stop";
	public static final String USAGE = "stop";
	public static final String DESCRIPTION = "Stops me from playing stuff";
	protected static final String[] ALIASES = {"s", "quit", "stopp", "stfu"};
	protected static final Category CATEGORY = Category.MUSIC;

	public StopCommand(){
		super(COMMAND, USAGE, DESCRIPTION, ALIASES, CATEGORY);
	}

	@Override
	public void run(CommandContext ctx){
		MusicPlayerCache.destroyMusicPlayer(ctx.getGuild());
		sendAnswer(ctx, "Successfully disconnected");
	}

}
