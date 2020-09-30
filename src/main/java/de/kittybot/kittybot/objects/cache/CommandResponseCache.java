package de.kittybot.kittybot.objects.cache;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CommandResponseCache{

	private static final Map<String, String> COMMAND_RESPONSES = new ConcurrentHashMap<>();

	public static void addCommandResponse(Message command, Message response){
		COMMAND_RESPONSES.put(command.getId(), response.getId());
	}

	public static void deleteCommandResponse(TextChannel channel, String command){
		var commandResponse = COMMAND_RESPONSES.get(command);
		if(commandResponse != null){
			channel.deleteMessageById(commandResponse).queue();
			COMMAND_RESPONSES.remove(command);
		}
	}

	public static void pruneCache(Guild guild){
		COMMAND_RESPONSES.remove(guild.getId());
	}

}