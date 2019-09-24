package de.anteiku.kittybot.commands;

import de.anteiku.kittybot.KittyBot;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

import java.util.List;

public class SlapCommand extends Command{
	
	public static String COMMAND = "slap";
	public static String USAGE = "slap <@user>";
	public static String DESCRIPTION = "Slaps a user";
	public static String[] ALIAS = {"schlag"};
	
	public SlapCommand(KittyBot main){
		super(main, COMMAND, USAGE, DESCRIPTION, ALIAS);
	}
	
	@Override
	public void run(String[] args, GuildMessageReceivedEvent event){
		if(args.length != 1){
			sendUsage(event.getMessage());
			return;
		}
		List<User> users = event.getMessage().getMentionedUsers();
		if(users.isEmpty() || users.contains(event.getAuthor())){
			sendError(event.getMessage(), "You need to mention a User(or not yourself :p)");
		}
		else{
			sendReactionImage(event, "hug", "hugs", users);
		}
	}
	
}
