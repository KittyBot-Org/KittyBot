package de.anteiku.kittybot.commands;

import de.anteiku.kittybot.KittyBot;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.Arrays;

public class SubscribeCommand extends ACommand{
	
	public static String COMMAND = "subscribe";
	public static String USAGE = "subscribe <daily|weekly> <tag> <tag> ... <tag>";
	public static String DESCRIPTION = "Subscribes to Hentai pictures";
	protected static String[] ALIAS = {"sub", "abonieren", "abo"};
	
	public SubscribeCommand(KittyBot main){
		super(main, COMMAND, USAGE, DESCRIPTION, ALIAS);
	}
	
	//TODO everything lmao
	@Override
	public void run(String[] args, GuildMessageReceivedEvent event){
		if(args.length < 2){
			sendUsage(event.getChannel());
		}
		else{
			String[] tags = Arrays.copyOfRange(args, 1, args.length);
			if(args[0].equalsIgnoreCase("daily")){
			
			}
			else if(args[0].equalsIgnoreCase("weekly")){
			
			}
			else{
				sendUsage(event.getChannel());
			}
		}
	}
	
}
