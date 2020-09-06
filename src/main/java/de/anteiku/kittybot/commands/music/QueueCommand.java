package de.anteiku.kittybot.commands.music;

import de.anteiku.kittybot.objects.cache.MusicPlayerCache;
import de.anteiku.kittybot.objects.command.ACommand;
import de.anteiku.kittybot.objects.command.Category;
import de.anteiku.kittybot.objects.command.CommandContext;
import de.anteiku.kittybot.utils.Utils;

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
		var musicPlayer = MusicPlayerCache.getMusicPlayer(ctx.getGuild());
		if(musicPlayer == null){
			sendError(ctx, "No active music player found!");
			return;
		}
		Utils.processQueue(this, ctx, musicPlayer);
	}

}
