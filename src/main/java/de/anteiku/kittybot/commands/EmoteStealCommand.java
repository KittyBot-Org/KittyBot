package de.anteiku.kittybot.commands;

import de.anteiku.kittybot.KittyBot;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Icon;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

public class EmoteStealCommand extends ACommand{

	public static String COMMAND = "steal";
	public static String USAGE = "steal <Emote, Emote, ...> or <url> <name>";
	public static String DESCRIPTION = "Steals some emotes";
	protected static String[] ALIAS = {"grab", "klau"};

	public EmoteStealCommand(KittyBot main){
		super(main, COMMAND, USAGE, DESCRIPTION, ALIAS);
		this.main = main;
	}

	@Override
	public void run(String[] args, GuildMessageReceivedEvent event){
		if(!event.getMember().hasPermission(Permission.MANAGE_EMOTES)){
			sendError(event, "Sorry you don't have the permission to manage emotes :(");
			return;
		}
		List<Emote> emotes = event.getMessage().getEmotes();
		List<Emote> guildEmotes = event.getGuild().getEmotes();
		int emotesStolen = 0;
		int emotesNotStolen = 0;
		if(!emotes.isEmpty()) {
			for(Emote emote : emotes) {
				if(guildEmotes.contains(emote)) {
					emotesNotStolen++;
					continue;
				}
				if(createEmoteFromURL(event.getGuild(), emote.getName(), emote.getImageUrl())){
					emotesStolen++;
				}
				else{
					sendError(event, "There was a problem stealing " + emote.getAsMention());
					emotesNotStolen++;
				}
			}
			String emotesStolenMsg = "";
			String emotesNotStolenMsg = "";
			if(emotesStolen > 0) {
				emotesStolenMsg = emotesStolen + " Emotes stolen\n";
			}
			if(emotesNotStolen > 0) {
				emotesNotStolenMsg = emotesNotStolen + " Emotes not stolen";
			}
			sendAnswer(event, emotesStolenMsg + emotesNotStolenMsg);
		}
		else if(args.length >= 2){
			try {
				new URL(args[0]).toURI();
				if(createEmoteFromURL(event.getGuild(), args[1], args[0])){
					sendAnswer(event, "Emote stolen");
				}
				else{
					sendError(event, "There was a problem creating the emote");
				}
			}
			catch(MalformedURLException | URISyntaxException e){
				sendError(event.getMessage(), "Please provide a valid url");
			}
		}
		else{
			sendUsage(event);
		}
	}
	
	private boolean createEmoteFromURL(Guild guild, String name, String url){
		try{
			guild.createEmote(name, Icon.from(new URL(url).openStream())).queue();
			return true;
		}
		catch(IOException e){
			LOG.error("Error while creating emote in guild " + guild.getId(), e);
		}
		return false;
	}

}
