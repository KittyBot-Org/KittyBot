package de.kittybot.kittybot.cache;

import de.kittybot.kittybot.objects.audio.MusicPlayer;
import net.dv8tion.jda.api.entities.Guild;

import java.util.HashMap;
import java.util.Map;

public class MusicPlayerCache{

	private static final Map<String, MusicPlayer> MUSIC_PLAYERS = new HashMap<>();

	private MusicPlayerCache(){}

	public static MusicPlayer getMusicPlayer(Guild guild){
		return getMusicPlayer(guild, false);
	}

	public static MusicPlayer getMusicPlayer(Guild guild, boolean createIfAbsent){
		final var guildId = guild.getId();
		var musicPlayer = MUSIC_PLAYERS.get(guildId);
		if(musicPlayer == null && createIfAbsent){
			final var newPlayer = new MusicPlayer();
			musicPlayer = newPlayer;
			guild.getAudioManager().setSendingHandler(musicPlayer.getSendHandler());
			MUSIC_PLAYERS.put(guildId, newPlayer);
		}
		return musicPlayer;
	}

	public static void destroyMusicPlayer(Guild guild){
		final var musicPlayer = getMusicPlayer(guild);
		if(musicPlayer == null){
			return;
		}
		musicPlayer.getPlayer().destroy();
		ReactiveMessageCache.removeReactiveMessage(guild, musicPlayer.getControllerMessageId());
		MUSIC_PLAYERS.remove(guild.getId());
		guild.getAudioManager().closeAudioConnection();
		guild.getAudioManager().setSendingHandler(null);
	}

	public static void pruneCache(Guild guild){ // just a convenience method to match other pruneCache methods
		destroyMusicPlayer(guild);
	}
}