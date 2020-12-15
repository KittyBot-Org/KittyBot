package de.kittybot.kittybot.managers;

import de.kittybot.kittybot.main.KittyBot;
import de.kittybot.kittybot.objects.MusicPlayer;
import net.dv8tion.jda.api.entities.Guild;

import java.util.HashMap;
import java.util.Map;

public class MusicManager{

	private final KittyBot main;
	private final Map<Long, MusicPlayer> musicPlayers;

	public MusicManager(KittyBot main){
		this.main = main;
		this.musicPlayers = new HashMap<>();
	}

	public void addPlayer(Guild guild, MusicPlayer player){
		musicPlayers.put(guild.getIdLong(), player);
	}

	public void destroyPlayer(Guild guild){
		var musicPlayer = getPlayer(guild);
		if(musicPlayer == null){
			return;
		}
		this.main.getLavalinkManager().getLavalink().getLink(guild).destroy();
		musicPlayers.remove(guild.getIdLong());
	}

	public MusicPlayer getPlayer(Guild guild){
		return musicPlayers.get(guild.getIdLong());
	}

}
