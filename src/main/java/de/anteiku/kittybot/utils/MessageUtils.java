package de.anteiku.kittybot.utils;

import net.dv8tion.jda.api.entities.Message;
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

	public static String rebuildMessage(Message message, String when, String when2){
		var content = message.getContentRaw();
		var last = content.lastIndexOf(when);
		if(last == -1){
			return "";
		}
		content = content.substring(last + when.length());
		var first = content.indexOf(when2);
		return content.substring(first + when2.length());
	}

}
