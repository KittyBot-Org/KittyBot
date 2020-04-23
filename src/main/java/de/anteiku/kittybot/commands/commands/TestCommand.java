package de.anteiku.kittybot.commands.commands;

import de.anteiku.kittybot.KittyBot;
import de.anteiku.kittybot.commands.ACommand;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class TestCommand extends ACommand{

	public static String COMMAND = "test";
	public static String USAGE = "test";
	public static String DESCRIPTION = "Only for testing weird stuff";
	protected static String[] ALIAS = {};

	public TestCommand(KittyBot main){
		super(main, COMMAND, USAGE, DESCRIPTION, ALIAS);
	}

	@Override
	public void run(String[] args, GuildMessageReceivedEvent event){
		sendAnswer(event, "Test command working!");
	}

}
