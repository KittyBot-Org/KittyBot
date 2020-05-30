package de.anteiku.kittybot.commands.commands;

import de.anteiku.kittybot.KittyBot;
import de.anteiku.kittybot.commands.ACommand;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.List;

public class EmoteCommand extends ACommand{

	public static String COMMAND = "emote";
	public static String USAGE = "emote <Emote, Emote, ...>";
	public static String DESCRIPTION = "Prints download links to emotes";
	protected static String[] ALIAS = {};

	public EmoteCommand(KittyBot main){
		super(main, COMMAND, USAGE, DESCRIPTION, ALIAS);
		this.main = main;
	}
	
	@Override
	public void run(String[] args, GuildMessageReceivedEvent event){
		List<Emote> emotes = event.getMessage().getEmotes();
		if(!emotes.isEmpty()){
			StringBuilder links = new StringBuilder();
			for(Emote emote : emotes){
				String link = emote.getImageUrl();
				if(links.length() + link.length() > Message.MAX_CONTENT_LENGTH - 20){
					sendAnswer(event, "Emote links: \n" + links.toString());
					links = new StringBuilder();
				}
				links.append(link).append("\n");
			}
			sendAnswer(event, "Emote links: \n" + links.toString());
		}
		else{
			sendUsage(event);
		}
	}
	
}
