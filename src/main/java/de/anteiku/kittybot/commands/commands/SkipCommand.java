package de.anteiku.kittybot.commands.commands;

import de.anteiku.kittybot.commands.ACommand;
import de.anteiku.kittybot.commands.CommandContext;
import de.anteiku.kittybot.objects.Cache;

public class SkipCommand extends ACommand{

	public static final String COMMAND = "skip";
	public static final String USAGE = "skip";
	public static final String DESCRIPTION = "Skips the current track";
	protected static final String[] ALIAS = {"überspring"};

	public SkipCommand(){
		super(COMMAND, USAGE, DESCRIPTION, ALIAS);
	}

	@Override
	public void run(CommandContext ctx){
		var musicPlayer = Cache.getMusicPlayer(ctx.getGuild());
		if(musicPlayer == null){
			sendError(ctx, "No active music player found!");
			return;
		}
		musicPlayer.nextTrack();
		sendAnswer(ctx, "Track skipped");
	}

}
