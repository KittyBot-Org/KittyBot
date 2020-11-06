package de.kittybot.kittybot.commands.music;

import de.kittybot.kittybot.cache.MusicManagerCache;
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
		final var commandFailure = MusicUtils.checkUserChannelState(ctx);
		if(commandFailure != null){
			sendError(ctx, "You can't change the volume as " + commandFailure.getReason());
			return;
		}
		final var musicManager = MusicManagerCache.getMusicManager(ctx.getGuild());
		var args = ctx.getArgs();
		if(args.length == 0){
			sendError(ctx, "Please provide the volume to set");
			return;
		}
		final var channel = ctx.getChannel();
		if(args[0].equalsIgnoreCase("reset")){
			musicManager.setVolume(100);
			return;
		}
		var oldVolume = musicManager.getVolume();
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
		musicManager.setVolume(newVolume);
	}

}
