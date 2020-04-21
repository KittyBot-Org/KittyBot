package de.anteiku.kittybot.commands.commands;

import de.anteiku.kittybot.KittyBot;
import de.anteiku.kittybot.commands.ACommand;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Icon;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.ContextException;
import net.dv8tion.jda.api.requests.restaction.AuditableRestAction;

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
				if(createEmoteFromURL(event, emote.getName(), emote.getImageUrl())){
					emotesStolen++;
				}
				else{
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
				if(createEmoteFromURL(event, args[1], args[0])){
					sendAnswer(event, "Emote stolen");
				}
			}
			catch(MalformedURLException | URISyntaxException e){
				sendError(event, "Please provide a valid url");
			}
		}
		else{
			sendUsage(event);
		}
	}
	
	private boolean createEmoteFromURL(GuildMessageReceivedEvent event, String name, String url){
		try{
			event.getGuild().createEmote(name, Icon.from(new URL(url).openStream())).queue(
					null,
					failure -> sendError(event, "Error creating emote: " + failure.getMessage())
			);
			return true;
		}
		catch(IOException e){
			LOG.error("Error while creating emote in guild " + event.getGuild().getId(), e);
			sendError(event, "There was a problem creating the emote");
		}
		return false;
	}

}
