package de.anteiku.kittybot.utils;

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

	public static List<String> splitMessage(String content){ // https://github.com/JDA-Applications/JDA-Utilities/blob/master/command/src/main/java/com/jagrosh/jdautilities/command/CommandEvent.java#L986-#L1009
		var msgs = new ArrayList<String>();
		while (content.length() > 2000){
			var idk = 2000 - (content.length() % 2000);
			var index = content.lastIndexOf("\n", 2000);
			if (index < idk){
				index = content.lastIndexOf(" ", 2000);
			}
			if (index < idk){
				index = 2000;
			}
			String temp = content.substring(0, index).trim();
			if (!temp.equals("")){
				msgs.add(temp);
			}
			content = content.substring(index).trim();
		}
		if (!content.equals("")){
			msgs.add(content);
		}
		return msgs;
	}
}
