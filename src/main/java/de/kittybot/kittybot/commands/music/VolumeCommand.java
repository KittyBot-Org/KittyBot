package de.kittybot.kittybot.commands.music;

import de.kittybot.kittybot.cache.MusicPlayerCache;
import de.kittybot.kittybot.objects.command.ACommand;
import de.kittybot.kittybot.objects.command.Category;
import de.kittybot.kittybot.objects.command.CommandContext;
import de.kittybot.kittybot.utils.MusicUtils;

public class VolumeCommand extends ACommand{

	public static final String COMMAND = "volume";
	public static final String USAGE = "volume <+-volume/reset>";
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
		var player = musicPlayer.getPlayer();
		var channel = player.getLink().getChannel();
		if(channel == null || voiceState.getChannel() == null || !channel.equals(voiceState.getChannel().getId())){
			sendError(ctx, "To use this command you need to be connected to the same voice channel as me");
			return;
		}
		var args = ctx.getArgs();
		if(args.length == 0){
			sendError(ctx, "Please provide the volume to set");
			return;
		}
		if(args[0].equalsIgnoreCase("reset")){
			player.setVolume(100);
			return;
		}
		var oldVolume = player.getVolume();
		var newVolume = 0;
		try{
			newVolume = MusicUtils.parseVolume(args[0], oldVolume);
		}
		catch(final NumberFormatException ex){
			sendError(ctx, "Please provide the volume to set");
			return;
		}
		if(newVolume == oldVolume){
			return;
		}
		player.setVolume(newVolume);
		musicPlayer.updateMusicControlMessage(ctx.getChannel());
	}

}
