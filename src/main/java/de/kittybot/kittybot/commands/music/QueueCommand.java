package de.kittybot.kittybot.commands.music;

import de.kittybot.kittybot.cache.MusicManagerCache;
import de.kittybot.kittybot.objects.command.ACommand;
import de.kittybot.kittybot.objects.command.Category;
import de.kittybot.kittybot.objects.command.CommandContext;
import de.kittybot.kittybot.utils.MusicUtils;
import de.kittybot.kittybot.utils.Utils;

public class QueueCommand extends ACommand{

	public static final String COMMAND = "queue";
	public static final String USAGE = "queue <playlist/song/video>";
	public static final String DESCRIPTION = "Queues what you want Kitty to play later";
	protected static final String[] ALIASES = {"q"};
	protected static final Category CATEGORY = Category.MUSIC;

	public QueueCommand(){
		super(COMMAND, USAGE, DESCRIPTION, ALIASES, CATEGORY);
	}

	@Override
	public void run(CommandContext ctx){
		final var connectionFailure = MusicUtils.checkVoiceChannel(ctx);
		if(connectionFailure != null){
			sendError(ctx, "I can't play music as " + connectionFailure.getReason());
			return;
		}
		var musicPlayer = MusicManagerCache.getMusicPlayer(ctx.getGuild());
		if(musicPlayer == null){
			sendError(ctx, "No active music player found");
			return;
		}
		if(ctx.getArgs().length == 0){
			var queue = musicPlayer.getQueue();
			if(queue.isEmpty()){
				sendSuccess(ctx, "There are currently no tracks queued");
				return;
			}
			var message = new StringBuilder("Currently **").append(queue.size())
					.append("** ")
					.append(Utils.pluralize("track", queue))
					.append(" ")
					.append(queue.size() > 1 ? "are" : "is")
					.append(" queued:\n");
			for(var track : queue){
				message.append(Utils.formatTrackTitle(track)).append(" ").append(Utils.formatDuration(track.getDuration())).append("\n");
			}
			sendSuccess(ctx, message.toString());
			return;
		}
		musicPlayer.loadItem(this, ctx);
	}

}
