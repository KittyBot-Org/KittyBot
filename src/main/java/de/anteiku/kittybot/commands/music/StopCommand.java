package de.anteiku.kittybot.commands.music;

import de.anteiku.kittybot.objects.Cache;
import de.anteiku.kittybot.objects.command.ACommand;
import de.anteiku.kittybot.objects.command.Category;
import de.anteiku.kittybot.objects.command.CommandContext;

public class StopCommand extends ACommand{

	public static final String COMMAND = "stop";
	public static final String USAGE = "stop";
	public static final String DESCRIPTION = "Stops me from playing stuff";
	protected static final String[] ALIAS = {"s", "quit", "stopp", "stfu"};
	protected static final Category CATEGORY = Category.MUSIC;

	public StopCommand(){
		super(COMMAND, USAGE, DESCRIPTION, ALIAS, CATEGORY);
	}

	@Override
	public void run(CommandContext ctx){
		Cache.destroyMusicPlayer(ctx.getGuild());
		sendAnswer(ctx, "Successfully disconnected");
	}

}
