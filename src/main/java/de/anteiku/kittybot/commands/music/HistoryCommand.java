package de.anteiku.kittybot.commands.music;

import de.anteiku.kittybot.objects.cache.MusicPlayerCache;
import de.anteiku.kittybot.objects.command.ACommand;
import de.anteiku.kittybot.objects.command.Category;
import de.anteiku.kittybot.objects.command.CommandContext;
import de.anteiku.kittybot.utils.Utils;

public class HistoryCommand extends ACommand{

	public static final String COMMAND = "history";
	public static final String USAGE = "history";
	public static final String DESCRIPTION = "Shows the current track history";
	protected static final String[] ALIASES = {"h"};
	protected static final Category CATEGORY = Category.MUSIC;

	public HistoryCommand(){
		super(COMMAND, USAGE, DESCRIPTION, ALIASES, CATEGORY);
	}

	@Override
	public void run(CommandContext ctx){
		Utils.processHistory(ctx, MusicPlayerCache.getMusicPlayer(ctx.getGuild()));
	}

}
