package de.kittybot.kittybot.commands.music;

import de.kittybot.kittybot.cache.MusicPlayerCache;
import de.kittybot.kittybot.objects.command.ACommand;
import de.kittybot.kittybot.objects.command.Category;
import de.kittybot.kittybot.objects.command.CommandContext;
import de.kittybot.kittybot.utils.Utils;

import static de.kittybot.kittybot.utils.Utils.formatDuration;
import static de.kittybot.kittybot.utils.Utils.formatTrackTitle;

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
		var musicPlayer = MusicPlayerCache.getMusicManager(ctx.getGuild());
		if(musicPlayer == null){
			sendError(ctx, "No active music player found!");
			return;
		}
		var history = musicPlayer.getHistory();
		if(history.isEmpty()){
			sendAnswer(ctx, "There are currently no tracks in history");
			return;
		}
		var message = new StringBuilder("Currently **").append(history.size())
				.append("** ")
				.append(Utils.pluralize("track", history))
				.append(" ")
				.append(history.size() > 1 ? "are" : "is")
				.append(" in the history:\n");
		history.forEach(track -> message.append(formatTrackTitle(track)).append(" ").append(formatDuration(track.getDuration())).append("\n"));
		sendAnswer(ctx, message.toString());
	}

}
