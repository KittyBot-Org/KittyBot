package de.anteiku.kittybot.utils;

import de.anteiku.kittybot.objects.command.CommandContext;
import de.anteiku.kittybot.objects.paginator.Paginator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static de.anteiku.kittybot.objects.command.ACommand.sendAnswer;

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

	private static List<String> splitMessage(String content, int limit, String prefix, String suffix){ // https://github.com/JDA-Applications/JDA-Utilities/blob/master/command/src/main/java/com/jagrosh/jdautilities/command/CommandEvent.java#L986-#L1009
		var msgs = new ArrayList<String>();
		while (content.length() > limit){
			var idk = limit - (content.length() % limit);
			var index = content.lastIndexOf("\n", limit);
			if (index < idk){
				index = content.lastIndexOf(" ", limit);
			}
			if (index < idk){
				index = limit;
			}
			String temp = content.substring(0, index).trim();
			if (!temp.equals("")){
				msgs.add(prefix + temp + suffix);
			}
			content = content.substring(index).trim();
		}
		if (!content.equals("")){
			msgs.add(prefix + content + suffix);
		}
		return msgs;
	}

	public static void buildResponse(CommandContext ctx, StringBuilder message){
		buildResponse(ctx, message, 2000, "", "");
	}

	public static void buildResponse(CommandContext ctx, StringBuilder message, int limit, String prefix, String suffix) {
		var built = message.toString();
		if (built.length() <= limit){
			sendAnswer(ctx, prefix + message.toString() + suffix);
			return;
		}
		var descriptions = new HashMap<Integer, String>();
		var page = 0;
		var split = splitMessage(built, limit, prefix, suffix);
		for (var s : split) {
			descriptions.put(page, s);
			page++;
		}
		Paginator.createDescriptionPaginator(ctx.getMessage(), descriptions);
	}
}
