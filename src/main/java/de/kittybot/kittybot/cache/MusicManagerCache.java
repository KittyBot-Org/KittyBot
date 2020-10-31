package de.kittybot.kittybot.cache;

import de.kittybot.kittybot.objects.music.GuildMusicManager;
import net.dv8tion.jda.api.entities.Guild;

import java.util.HashMap;
import java.util.Map;

public class MusicManagerCache{

	private static final Map<String, GuildMusicManager> MUSIC_MANAGERS = new HashMap<>();

	private MusicManagerCache(){}

	public static GuildMusicManager getMusicManager(Guild guild){
		return getMusicManager(guild, false);
	}

	public static GuildMusicManager getMusicManager(Guild guild, boolean createIfAbsent){
		final var guildId = guild.getId();
		var musicManager = MUSIC_MANAGERS.get(guildId);
		if(musicManager == null && createIfAbsent){
			final var newPlayer = new GuildMusicManager();
			musicManager = newPlayer;
			final var audioManager = guild.getAudioManager();
			audioManager.setSendingHandler(musicManager.getSendHandler());
			audioManager.setSelfDeafened(true);
			MUSIC_MANAGERS.put(guildId, newPlayer);
		}
		return musicManager;
	}

	public static void destroyMusicManager(Guild guild){
		final var musicManager = getMusicManager(guild);
		if(musicManager == null){
			return;
		}
		musicManager.getAudioPlayer().destroy();
		ReactiveMessageCache.removeReactiveMessage(guild, musicManager.getControllerMessageId());
		MUSIC_MANAGERS.remove(guild.getId());
		guild.getAudioManager().closeAudioConnection();
		guild.getAudioManager().setSendingHandler(null);
	}

	public static void pruneCache(Guild guild){ // just a convenience method to match other pruneCache methods
		destroyMusicManager(guild);
	}
}