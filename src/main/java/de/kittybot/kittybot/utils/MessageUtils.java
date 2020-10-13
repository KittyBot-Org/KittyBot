package de.kittybot.kittybot.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class MessageUtils{

	private static final Logger LOG = LoggerFactory.getLogger(MessageUtils.class);

	private MessageUtils(){}

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

	public static String maskLink(String title, String url){
		return "[" + title + "](" + url + ")";
	}

	public static String getUserMention(String userId){
		if(userId.equals("-1")){
			return "unset";
		}
		return "<@" + userId + ">";
	}

	public static String getRoleMention(String roleId){
		if(roleId.equals("-1")){
			return "unset";
		}
		return "<@&" + roleId + ">";
	}

	public static String getChannelMention(String channelId){
		if(channelId.equals("-1")){
			return "unset";
		}
		return "<#" + channelId + ">";
	}

	public static String getEmoteMention(String emoteId){
		if(emoteId.equals("-1")){
			return "unset";
		}
		return "<:i:" + emoteId + ">";
	}

}
