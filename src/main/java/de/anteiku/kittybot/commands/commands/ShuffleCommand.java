package de.anteiku.kittybot.commands.commands;

import de.anteiku.kittybot.KittyBot;
import de.anteiku.kittybot.commands.ACommand;
import de.anteiku.kittybot.commands.CommandContext;

public class ShuffleCommand extends ACommand{

	public static String COMMAND = "shuffle";
	public static String USAGE = "shuffle";
	public static String DESCRIPTION = "Shuffles the current queue";
	protected static String[] ALIAS = {"mische"};

	public ShuffleCommand(KittyBot main){
		super(main, COMMAND, USAGE, DESCRIPTION, ALIAS);
		this.main = main;
	}

	@Override
	public void run(CommandContext ctx){
		var musicPlayer = KittyBot.commandManager.getMusicPlayer(ctx.getGuild());
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
