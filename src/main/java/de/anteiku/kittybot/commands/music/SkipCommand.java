package de.anteiku.kittybot.commands.music;

import de.anteiku.kittybot.command.ACommand;
import de.anteiku.kittybot.command.Category;
import de.anteiku.kittybot.command.CommandContext;
import de.anteiku.kittybot.objects.cache.MusicPlayerCache;

public class SkipCommand extends ACommand{

	public static final String COMMAND = "skip";
	public static final String USAGE = "skip";
	public static final String DESCRIPTION = "Skips the current track";
	protected static final String[] ALIASES = {"überspring"};
	protected static final Category CATEGORY = Category.MUSIC;

	public SkipCommand(){
		super(COMMAND, USAGE, DESCRIPTION, ALIASES, CATEGORY);
	}

	@Override
	public void run(CommandContext ctx){
		var musicPlayer = MusicPlayerCache.getMusicPlayer(ctx.getGuild());
		if(musicPlayer == null){
			sendError(ctx, "No active music player found!");
			return;
		}
		musicPlayer.nextTrack();
		sendAnswer(ctx, "Track skipped");
	}

}
