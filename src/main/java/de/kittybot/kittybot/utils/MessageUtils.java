package de.kittybot.kittybot.utils;

import de.kittybot.kittybot.objects.Emoji;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class MessageUtils{

	private static final Logger LOG = LoggerFactory.getLogger(MessageUtils.class);

	private static final String UNSET = "unset";

	private MessageUtils(){}

	public static String trimIfTooLong(String message){
		return message.length() > 2048 ? message.substring(0, 2045) + "..." : message;
	}

	public static String getBoolEmote(boolean bool){
		return bool ? Emoji.CHECK.get() : Emoji.X.get();
	}

	public static String maskLink(String title, String url){
		return "[" + title + "](" + url + ")";
	}

	public static <T> String pluralize(String text, Collection<T> collection){
		return pluralize(text, collection.size());
	}

	public static String pluralize(String text, int count){
		return count == 1 ? text : text + "s";
	}

	public static String getUserMention(long userId){
		if(userId == -1L){
			return UNSET;
		}
		return "<@!" + userId + ">";
	}

	public static String getRoleMention(long roleId){
		if(roleId == -1L){
			return UNSET;
		}
		return "<@&" + roleId + ">";
	}

	public static String getChannelMention(long channelId){
		if(channelId == -1L){
			return UNSET;
		}
		return "<#" + channelId + ">";
	}

	public static String getEmoteMention(long emoteId){
		if(emoteId == -1L){
			return UNSET;
		}
		return "<:i:" + emoteId + ">";
	}

	public static String getMessageLink(long guildId, long channelId, long messageId){
		return String.format("https://discord.com/channels/%d/%d/%d", guildId, channelId, messageId);
	}

	public static List<String> loadMessageFile(String fileName){
		var inputStream = MessageUtils.class.getClassLoader().getResourceAsStream("messages/" + fileName + "_messages.txt");
		var list = new ArrayList<String>();
		if(inputStream == null){
			LOG.error("Message file not found");
			return list;
		}
		var reader = new BufferedReader(new InputStreamReader((inputStream), StandardCharsets.UTF_8));
		try{
			String line;
			while((line = reader.readLine()) != null){
				list.add(line);
			}
			reader.close();
		}
		catch(IOException e){
			LOG.error("Error reading message file", e);
		}
		return list;
	}

}
