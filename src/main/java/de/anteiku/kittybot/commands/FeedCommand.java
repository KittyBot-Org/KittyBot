package de.anteiku.kittybot.commands;

import de.anteiku.kittybot.KittyBot;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

public class FeedCommand extends Command{

	public static final String COMMAND = "feed";
	public static final String USAGE = "feed <@user>";
	public static final String DESCRIPTION = "Feeds a user";
	protected static final String[] ALIAS = {"füttern"};

	public FeedCommand(KittyBot main){
		super(main, COMMAND, USAGE, DESCRIPTION, ALIAS);
	}

	@Override
	public void run(String[] args, GuildMessageReceivedEvent event){
		if(args.length != 1){
			sendUsage(event.getMessage());
			return;
		}
		sendReactionImage(event, "feed", "feeds");
		
	}

}
