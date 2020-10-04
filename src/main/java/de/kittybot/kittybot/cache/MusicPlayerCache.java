package de.kittybot.kittybot.cache;

import de.kittybot.kittybot.KittyBot;
import de.kittybot.kittybot.objects.audio.GuildMusicManager;
import net.dv8tion.jda.api.entities.Guild;

import java.util.HashMap;
import java.util.Map;

public class MusicPlayerCache{

	private static final Map<String, GuildMusicManager> MUSIC_MANAGERS = new HashMap<>();

	private MusicPlayerCache() {}

	public static GuildMusicManager getMusicManager(Guild guild){
		return getMusicManager(guild, false);
	}

	public static GuildMusicManager getMusicManager(Guild guild, boolean createIfAbsent){
		final var guildId = guild.getId();
		var manager = MUSIC_MANAGERS.get(guildId);
		if (manager == null && createIfAbsent){
			final var newManager = new GuildMusicManager(KittyBot.getAudioPlayerManager());
			manager = newManager;
			guild.getAudioManager().setSendingHandler(manager.getSendHandler());
			MUSIC_MANAGERS.put(guildId, newManager);
		}
		return manager;
	}

	public static void destroyMusicPlayer(Guild guild){
		final var musicManager = getMusicManager(guild, false);
		if(musicManager == null){
			return;
		}
		musicManager.getPlayer().destroy();
		ReactiveMessageCache.removeReactiveMessage(guild, musicManager.getControllerMessageId());
		MUSIC_MANAGERS.remove(guild.getId());
		guild.getAudioManager().closeAudioConnection();
		guild.getAudioManager().setSendingHandler(null);
	}

	public static void pruneCache(Guild guild){ // just a convenience method to match other pruneCache methods
		destroyMusicPlayer(guild);
	}
}