package de.anteiku.kittybot.commands.commands;

import de.anteiku.kittybot.KittyBot;
import de.anteiku.kittybot.commands.ACommand;
import de.anteiku.kittybot.commands.CommandContext;

public class SkipCommand extends ACommand{

	public static String COMMAND = "skip";
	public static String USAGE = "skip";
	public static String DESCRIPTION = "Skips the current track";
	protected static String[] ALIAS = {"überspring"};

	public SkipCommand(KittyBot main){
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
		musicPlayer.nextTrack();
		sendAnswer(ctx, "Track skipped");
	}

}
