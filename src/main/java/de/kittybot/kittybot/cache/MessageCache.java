package de.kittybot.kittybot.cache;

import de.kittybot.kittybot.objects.messages.MessageData;
import net.dv8tion.jda.api.entities.Guild;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MessageCache{

	private static final Map<String, MessageData> MESSAGE_CACHE = new ConcurrentHashMap<>();
	private static final Map<String, String> LAST_MESSAGE_DELETED_CACHE = new HashMap<>();

	private static final Map<String, String> LAST_MESSAGE_EDITED_CACHE = new HashMap<>();
	private static final Map<String, MessageData> LAST_MESSAGE_EDITED_DATA = new HashMap<>();

	private MessageCache(){
		super();
	}

	public static MessageData getLastDeletedMessage(final String channelId){
		final var latest = LAST_MESSAGE_DELETED_CACHE.get(channelId);
		return latest == null ? null : MESSAGE_CACHE.get(latest);
	}

	public static void setLastDeletedMessage(final String channelId, final String messageId){
		LAST_MESSAGE_DELETED_CACHE.put(channelId, messageId);
	}

	public static void cacheMessage(final String messageId, final MessageData message){
		final var data = MESSAGE_CACHE.get(messageId);
		if(data != null){
			LAST_MESSAGE_EDITED_DATA.put(messageId, data.setTimeEdited(message.getTimeEdited()));
		}
		MESSAGE_CACHE.put(messageId, message);
	}

	public static void uncacheMessage(final String channelId, final String messageId){
		MESSAGE_CACHE.remove(messageId);
		LAST_MESSAGE_DELETED_CACHE.remove(channelId);
		LAST_MESSAGE_EDITED_DATA.remove(messageId);
		LAST_MESSAGE_EDITED_CACHE.remove(channelId, messageId);
	}

	public static boolean isCached(final String messageId){
		return MESSAGE_CACHE.containsKey(messageId);
	}

	// MESSAGE EDITING CACHING

	public static void setLastEditedMessage(final String channelId, final String messageId){
		LAST_MESSAGE_EDITED_CACHE.put(channelId, messageId);
	}

	public static MessageData getLastEditedMessage(final String channelId){
		final var latest = LAST_MESSAGE_EDITED_CACHE.get(channelId);
		return latest == null ? null : LAST_MESSAGE_EDITED_DATA.get(latest);
	}

	public static void uncacheEditedMessage(final String channelId, final String messageId){
		LAST_MESSAGE_EDITED_CACHE.remove(channelId, messageId);
		LAST_MESSAGE_EDITED_DATA.remove(messageId);
	}

	public static void pruneCache(){
		MESSAGE_CACHE.entrySet().removeIf(entry -> entry.getValue().getTimeCreated().isBefore(OffsetDateTime.now().minusMinutes(10)));
	}

	public static void pruneCache(Guild guild){
		final var entries = MESSAGE_CACHE.entrySet();
		for(var entry : entries){
			final var dataGuildId = entry.getValue().getGuildId();
			final var guildId = guild.getId();
			if(!dataGuildId.equals(guildId)){
				continue;
			}
			final var messageId = entry.getKey();
			MESSAGE_CACHE.remove(messageId);
			LAST_MESSAGE_DELETED_CACHE.entrySet().removeIf(record -> record.getValue().equals(messageId));
			LAST_MESSAGE_EDITED_CACHE.entrySet().removeIf(record -> record.getValue().equals(messageId));
			LAST_MESSAGE_EDITED_DATA.entrySet().removeIf(record -> record.getValue().getGuildId().equals(guildId));
		}
	}

}