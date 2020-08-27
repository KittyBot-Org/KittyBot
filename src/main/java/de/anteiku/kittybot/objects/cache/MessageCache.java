package de.anteiku.kittybot.objects.cache;

import de.anteiku.kittybot.objects.messages.MessageData;

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
			LAST_MESSAGE_EDITED_DATA.put(messageId, data);
		}
		MESSAGE_CACHE.put(messageId, message);
	}

	public static void uncacheMessage(final String channelId, final String messageId){
		MESSAGE_CACHE.remove(messageId);
		LAST_MESSAGE_DELETED_CACHE.remove(channelId);
		LAST_MESSAGE_EDITED_DATA.remove(messageId);
		LAST_MESSAGE_EDITED_CACHE.remove(channelId, messageId);
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

	public static Map<String, MessageData> getCache(){
		return MESSAGE_CACHE;
	}

}