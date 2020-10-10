package de.kittybot.kittybot.commands.music;

import de.kittybot.kittybot.cache.MusicPlayerCache;
import de.kittybot.kittybot.objects.command.ACommand;
import de.kittybot.kittybot.objects.command.Category;
import de.kittybot.kittybot.objects.command.CommandContext;
import de.kittybot.kittybot.utils.AudioUtils;

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
		final var playing = musicPlayer.getPlayer().getPlayingTrack();
		if(playing == null){
			sendError(ctx, "There is currently no song playing");
			return;
		}
		if(!musicPlayer.getRequesterId().equals(ctx.getUser().getId())){
			sendError(ctx, "You have to be the requester of the song to control it");
			return;
		}
		musicPlayer.nextTrack();
		AudioUtils.reactSuccess(ctx);
	}

}
