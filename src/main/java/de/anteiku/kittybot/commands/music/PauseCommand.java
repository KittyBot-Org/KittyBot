package de.anteiku.kittybot.commands.music;

import de.anteiku.kittybot.objects.Cache;
import de.anteiku.kittybot.objects.command.ACommand;
import de.anteiku.kittybot.objects.command.CommandContext;

public class PauseCommand extends ACommand{

	public static final String COMMAND = "pause";
	public static final String USAGE = "pause";
	public static final String DESCRIPTION = "Pauses the current track";
	protected static final String[] ALIAS = {};

	public PauseCommand(){
		super(COMMAND, USAGE, DESCRIPTION, ALIAS);
	}

	@Override
	public void run(CommandContext ctx){
		var musicPlayer = Cache.getMusicPlayer(ctx.getGuild());
		if(musicPlayer == null){
			sendError(ctx, "No active music player found!");
			return;
		}
		if(musicPlayer.getPlayer().getPlayingTrack() == null){
			sendError(ctx, "There are currently no tracks queued");
			return;
		}
		var paused = !musicPlayer.getPlayer().isPaused();
		musicPlayer.getPlayer().setPaused(paused);
		sendAnswer(ctx, "Track " + (paused ? "paused" : "resumed"));
	}

}
