package de.anteiku.kittybot.commands.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import de.anteiku.kittybot.command.ACommand;
import de.anteiku.kittybot.command.Category;
import de.anteiku.kittybot.command.CommandContext;
import de.anteiku.kittybot.objects.Cache;

import static de.anteiku.kittybot.utils.Utils.*;

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
		var voiceState = ctx.getMember().getVoiceState();
		if(voiceState != null && !voiceState.inVoiceChannel()){
			sendError(ctx, "To use this command you need to be connected to a voice channel");
			return;
		}
		var musicPlayer = Cache.getMusicPlayer(ctx.getGuild());
		if(musicPlayer == null){
			sendError(ctx, "No active music player found!");
			return;
		}
		if(ctx.getArgs().length == 0){
			var queue = musicPlayer.getQueue();
			if(queue.isEmpty()){
				sendAnswer(ctx, "There are currently no tracks queued");
				return;
			}
			var message = new StringBuilder("Currently ").append(queue.size())
					.append(" ")
					.append(pluralize("track", queue))
					.append(" ")
					.append(queue.size() > 1 ? "are" : "is")
					.append(" queued:\n");
			for(AudioTrack track : queue){
				message.append(formatTrackTitle(track)).append(" ").append(formatDuration(track.getDuration())).append("\n");
			}
			sendAnswer(ctx, message.toString());
			return;
		}
		musicPlayer.loadItem(this, ctx);
	}

}
