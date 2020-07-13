package de.anteiku.kittybot.commands.commands;

import de.anteiku.kittybot.KittyBot;
import de.anteiku.kittybot.commands.ACommand;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class QueueCommand extends ACommand{

	public static String COMMAND = "queue";
	public static String USAGE = "queue <playlist/song/video>";
	public static String DESCRIPTION = "Queues what you want him to play later";
	protected static String[] ALIAS = {"q"};

	public QueueCommand(KittyBot main){
		super(main, COMMAND, USAGE, DESCRIPTION, ALIAS);
		this.main = main;
	}

	@Override
	public void run(String[] args, GuildMessageReceivedEvent event){
		var musicPlayer = main.commandManager.getMusicPlayer(event.getGuild());
		if(musicPlayer == null){
			sendError(event, "No active music player found!");
			return;
		}

		musicPlayer.loadItem(this, event, args);
		//TODO maybe create one if no one is created yet?
	}

}
