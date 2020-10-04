package de.kittybot.kittybot.commands.music;

import de.kittybot.kittybot.objects.cache.MusicPlayerCache;
import de.kittybot.kittybot.objects.command.ACommand;
import de.kittybot.kittybot.objects.command.Category;
import de.kittybot.kittybot.objects.command.CommandContext;

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
		if(MusicPlayerCache.getMusicPlayer(ctx.getGuild()) == null){
			sendError(ctx, "No active music player found!");
			return;
		}
		MusicPlayerCache.destroyMusicPlayer(ctx.getGuild());
		sendAnswer(ctx, "Successfully disconnected");
	}

}
