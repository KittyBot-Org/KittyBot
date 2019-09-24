package de.anteiku.kittybot.commands;

import de.anteiku.kittybot.KittyBot;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

import java.util.List;

public class KissCommand extends Command{

	public static String COMMAND = "kiss";
	public static String USAGE = "kiss <@user>";
	public static String DESCRIPTION = "Sends a kiss to a user";
	public static String[] ALIAS = {"küss"};

	public KissCommand(KittyBot main){
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
			sendReactionImage(event, "kiss", "kisses", users);
		}
	}

}
