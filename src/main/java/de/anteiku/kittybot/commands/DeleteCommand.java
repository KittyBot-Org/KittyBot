package de.anteiku.kittybot.commands;

import de.anteiku.kittybot.Emotes;
import de.anteiku.kittybot.KittyBot;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

public class DeleteCommand extends Command{

	public static String COMMAND = "delete";
	public static String USAGE = "delete <guild id>";
	public static String DESCRIPTION = "Deletes databases";
	public static String[] ALIAS = {};

	public DeleteCommand(KittyBot main){
		super(main, COMMAND, USAGE, DESCRIPTION, ALIAS);
		this.main = main;
	}

	@Override
	public void run(String[] args, GuildMessageReceivedEvent event){
		if(event.getAuthor().getId().equals(KittyBot.ME)){
			if(args.length > 0){
				if(args[0].equalsIgnoreCase("all")){
					main.database.flush();
					event.getMessage().addReaction(Emotes.CHECK).queue();
				}
			}
			else{
				
				event.getMessage().addReaction(Emotes.CHECK).queue();
			}
		}
		else{
			sendError(event.getChannel(), "You are not allowed to use this command!");
		}
	}

}
