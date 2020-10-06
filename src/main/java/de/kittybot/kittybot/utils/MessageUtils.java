package de.kittybot.kittybot.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MessageUtils{

	private static final Logger LOG = LoggerFactory.getLogger(MessageUtils.class);

	public static List<String> loadMessageFile(String fileName){
		var inputStream = MessageUtils.class.getClassLoader().getResourceAsStream("messages/" + fileName + "_messages.txt");
		if(inputStream == null){
			LOG.error("Message file not found");
			return null;
		}
		var reader = new BufferedReader(new InputStreamReader((inputStream), StandardCharsets.UTF_8));
		List<String> set = new ArrayList<>();
		try{
			String line;
			while((line = reader.readLine()) != null){
				set.add(line);
			}
			reader.close();
		}
		catch(IOException e){
			LOG.error("Error reading message file", e);
		}
		return set;
	}

	public static <T> String join(Set<T> set, Function<? super T, ? extends String> mapper){
		return join(set, mapper, ", ");
	}

	public static <T> String join(Set<T> set, Function<? super T, ? extends String> mapper, String delimiter){
		return set.stream().map(mapper).collect(Collectors.joining(delimiter));
	}

	public static String maskLink(String title, String url){
		return "[" + title + "](" + url + ")";
	}

	public static String getUserMention(String userId){
		return "<@" + userId + ">";
	}

	public static String getRoleMention(String roleId){
		return "<@&" + roleId + ">";
	}

	public static String getChannelMention(String channelId){
		return "<#" + channelId + ">";
	}

	public static String getEmoteMention(String emoteId){
		return "<:i:" + emoteId + ">";
	}

}
