package de.kittybot.kittybot.commands.music;

import de.kittybot.kittybot.cache.MusicManagerCache;
import de.kittybot.kittybot.objects.command.ACommand;
import de.kittybot.kittybot.objects.command.Category;
import de.kittybot.kittybot.objects.command.CommandContext;

public class ShuffleCommand extends ACommand{

	public static final String COMMAND = "shuffle";
	public static final String USAGE = "shuffle";
	public static final String DESCRIPTION = "Shuffles the current queue";
	protected static final String[] ALIASES = {"mische"};
	protected static final Category CATEGORY = Category.MUSIC;

	public ShuffleCommand(){
		super(COMMAND, USAGE, DESCRIPTION, ALIASES, CATEGORY);
	}

	@Override
	public void run(CommandContext ctx){
		var musicManager = MusicManagerCache.getMusicManager(ctx.getGuild());
		if(musicManager == null){
			sendError(ctx, "No active music player found!");
			return;
		}
		if(musicManager.getQueue().isEmpty()){
			sendError(ctx, "There are currently no tracks queued");
			return;
		}
		musicManager.shuffle();
		sendAnswer(ctx, "Queue shuffled");
	}

}
