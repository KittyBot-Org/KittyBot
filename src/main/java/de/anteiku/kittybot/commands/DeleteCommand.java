package de.anteiku.kittybot.commands;

import de.anteiku.kittybot.Emotes;
import de.anteiku.kittybot.KittyBot;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

public class DeleteCommand extends Command{

	public static final String COMMAND = "delete";
	public static final String USAGE = "delete <guild id>";
	public static final String DESCRIPTION = "Deletes databases";
	public static final String[] ALIAS = {};

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
					event.getMessage().addReaction(Emotes.CHECK.get()).queue();
				}
			}
			else{
				
				event.getMessage().addReaction(Emotes.CHECK.get()).queue();
			}
		}
		else{
			sendError(event.getChannel(), "You are not allowed to use this command!");
		}
	}

}
