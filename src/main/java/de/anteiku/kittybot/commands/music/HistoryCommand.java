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
		var voiceState = ctx.getMember().getVoiceState();
		if(voiceState != null && !voiceState.inVoiceChannel()){
			sendError(ctx, "To use this command you need to be connected to a voice channel");
			return;
		}
		var musicPlayer = MusicPlayerCache.getMusicPlayer(ctx.getGuild());
		if(musicPlayer == null){
			sendError(ctx, "No active music player found!");
			return;
		}
		Utils.processHistory(ctx, musicPlayer);
	}

}
