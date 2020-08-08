package de.anteiku.kittybot.commands.commands;

import de.anteiku.kittybot.KittyBot;
import de.anteiku.kittybot.commands.ACommand;
import de.anteiku.kittybot.commands.CommandContext;
import de.anteiku.kittybot.objects.Cache;

public class ShuffleCommand extends ACommand{

	public static final String COMMAND = "shuffle";
	public static final String USAGE = "shuffle";
	public static final String DESCRIPTION = "Shuffles the current queue";
	protected static final String[] ALIAS = {"mische"};

	public ShuffleCommand(){
		super(COMMAND, USAGE, DESCRIPTION, ALIAS);
	}

	@Override
	public void run(CommandContext ctx){
		var musicPlayer = Cache.getMusicPlayer(ctx.getGuild());
		if(musicPlayer == null){
			sendError(ctx, "No active music player found!");
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
