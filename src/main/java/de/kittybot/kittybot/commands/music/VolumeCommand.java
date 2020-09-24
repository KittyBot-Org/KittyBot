package de.kittybot.kittybot.commands.music;

import de.kittybot.kittybot.objects.cache.MusicPlayerCache;
import de.kittybot.kittybot.objects.command.ACommand;
import de.kittybot.kittybot.objects.command.Category;
import de.kittybot.kittybot.objects.command.CommandContext;

public class VolumeCommand extends ACommand{

	public static final String COMMAND = "volume";
	public static final String USAGE = "volume <0-200>";
	public static final String DESCRIPTION = "Sets the current volume";
	protected static final String[] ALIASES = {"vol", "v", "lautstärke"};
	protected static final Category CATEGORY = Category.MUSIC;

	public VolumeCommand(){
		super(COMMAND, USAGE, DESCRIPTION, ALIASES, CATEGORY);
	}

	@Override
	public void run(CommandContext ctx){
		var voiceState = ctx.getMember().getVoiceState();
		if(voiceState == null || !voiceState.inVoiceChannel()){
			sendError(ctx, "To use this command you need to be connected to a voice channel");
			return;
		}
		var musicPlayer = MusicPlayerCache.getMusicPlayer(ctx.getGuild());
		if(musicPlayer == null){
			sendError(ctx, "No active music player found!");
			return;
		}
		var channel = musicPlayer.getPlayer().getLink().getChannel();
		if(channel == null || voiceState.getChannel() == null || !channel.equals(voiceState.getChannel().getId())){
			sendError(ctx, "To use this command you need to be connected to the same voice channel as me");
			return;
		}
		if(musicPlayer.getQueue().isEmpty()){
			sendError(ctx, "There are currently no tracks queued");
			return;
		}
		musicPlayer.shuffle();
		sendAnswer(ctx, "Queue shuffled");
	}

}