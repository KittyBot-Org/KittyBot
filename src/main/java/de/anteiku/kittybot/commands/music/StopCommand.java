package de.anteiku.kittybot.commands.music;

import de.anteiku.kittybot.objects.cache.MusicPlayerCache;
import de.anteiku.kittybot.objects.command.ACommand;
import de.anteiku.kittybot.objects.command.Category;
import de.anteiku.kittybot.objects.command.CommandContext;

public class StopCommand extends ACommand{

	public static final String COMMAND = "stop";
	public static final String USAGE = "stop";
	public static final String DESCRIPTION = "Stops me from playing stuff";
	protected static final String[] ALIASES = {"quit", "stopp", "stfu"};
	protected static final Category CATEGORY = Category.MUSIC;

	public StopCommand(){
		super(COMMAND, USAGE, DESCRIPTION, ALIASES, CATEGORY);
	}

	@Override
	public void run(CommandContext ctx){
		var musicPlayer = MusicPlayerCache.getMusicPlayer(ctx.getGuild());
		if(MusicPlayerCache.getMusicPlayer(ctx.getGuild()) == null){
			sendError(ctx, "No active music player found!");
			return;
		}
		if(!musicPlayer.isDJ(ctx.getMember())){
			sendError(ctx, "You have to be the DJ to stop the music");
			return;
		}
		MusicPlayerCache.destroyMusicPlayer(ctx.getGuild());
		sendAnswer(ctx, "Successfully disconnected");
	}

}
