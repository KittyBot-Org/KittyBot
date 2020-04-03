package de.anteiku.kittybot.commands;

import de.anteiku.kittybot.KittyBot;
import de.anteiku.kittybot.utils.Logger;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Icon;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.io.IOException;
import java.net.URL;
import java.util.List;

public class EmoteStealCommand extends ACommand{

	public static String COMMAND = "steal";
	public static String USAGE = "steal <Emote, Emote, Emote, ...>";
	public static String DESCRIPTION = "Steals some emotes";
	protected static String[] ALIAS = {"grab", "klau"};

	public EmoteStealCommand(KittyBot main){
		super(main, COMMAND, USAGE, DESCRIPTION, ALIAS);
		this.main = main;
	}

	@Override
	public void run(String[] args, GuildMessageReceivedEvent event){
		List<Emote> emotes = event.getMessage().getEmotes();
		if(!emotes.isEmpty()) {
			boolean success = true;
			for(Emote emote : emotes) {
				try{
					Icon icon = Icon.from(new URL(emote.getImageUrl()).openStream());
					event.getGuild().createEmote(emote.getName(), icon).queue();
				}
				catch(IOException e){
					Logger.error(e);
					sendAnswer(event.getChannel(), "There was a problem stealing " + emote.getAsMention());
					success = false;
				}
			}
			if(success) {
				sendAnswer(event.getChannel(), "All emotes stolen!");
			}
		}
		else {
			sendUsage(event.getChannel());
		}
	}

}
