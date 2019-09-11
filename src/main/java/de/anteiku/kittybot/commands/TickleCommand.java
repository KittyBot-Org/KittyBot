package de.anteiku.kittybot.commands;

import de.anteiku.kittybot.API;
import de.anteiku.kittybot.KittyBot;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

import java.awt.*;

public class TickleCommand extends Command{
	
	public static String COMMAND = "tickle";
	public static String USAGE = "tickle <@user>";
	public static String DESCRIPTION = "Tickles a user";
	public static String[] ALIAS = {"kitzel"};
	
	public TickleCommand(KittyBot main){
		super(main, COMMAND, USAGE, DESCRIPTION, ALIAS);
	}
	
	@Override
	public void run(String[] args, GuildMessageReceivedEvent event){
		if(args.length != 1){
			sendUsage(event.getChannel());
			return;
		}
		String id = API.getIdByMention(args[0]);
		if(id != null){
			User user = event.getJDA().getUserById(id);
			if(user != null){
				try{
					String url = API.getNeko("tickle");
					EmbedBuilder eb = new EmbedBuilder();
					eb.setColor(Color.pink);
					if(user.getId().equals(event.getAuthor().getId())){
						eb.setDescription(user.getAsMention() + " tickled **himself**!");
					}
					else{
						eb.setDescription(API.getNameByUser(event.getMember()) + " tickled **" + user.getAsMention() + "**!");
					}
					eb.setImage(url);
					event.getChannel().sendMessage(eb.build()).queue();
				}
				catch(Exception e){
					sendError(event.getChannel(), "That's a weird error, oops!");
				}
			}
			else{
				sendError(event.getChannel(), "User not found!");
			}
		}
		else{
			sendError(event.getChannel(), "You need to mention a User!");
		}
	}
	
}
