package de.anteiku.kittybot.commands.commands;

import de.anteiku.kittybot.KittyBot;
import de.anteiku.kittybot.commands.ACommand;
import de.anteiku.kittybot.commands.CommandContext;

public class PauseCommand extends ACommand{

	public static String COMMAND = "pause";
	public static String USAGE = "pause";
	public static String DESCRIPTION = "Pauses the current track";
	protected static String[] ALIAS = {};

	public PauseCommand(KittyBot main){
		super(main, COMMAND, USAGE, DESCRIPTION, ALIAS);
		this.main = main;
	}

	@Override
	public void run(CommandContext ctx){
		var musicPlayer = KittyBot.commandManager.getMusicPlayer(ctx.getGuild());
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
