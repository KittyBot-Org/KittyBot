package de.anteiku.kittybot.commands;

import de.anteiku.kittybot.KittyBot;
import de.anteiku.kittybot.objects.ReactiveMessage;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

public class CommandManager{

	private static final Logger LOG = LoggerFactory.getLogger(CommandManager.class);
	private final KittyBot main;
	public Map<String, ACommand> commands;

	public CommandManager(KittyBot main){
		this.main = main;
		this.commands = new LinkedHashMap<>();
	}

	public CommandManager addCommands(ACommand... commands){
		for(ACommand command : commands){
			this.commands.put(command.getCommand(), command);
		}
		return this;
	}

	public void addReactiveMessage(GuildMessageReceivedEvent event, Message message, ACommand cmd, String allowed){
		main.database.addReactiveMessage(
			event.getGuild().getId(), event.getAuthor().getId(), message.getId(), event.getMessage().getId(), cmd.command, allowed);
	}

	public void removeReactiveMessage(Guild guild, String messageId){
		main.database.removeReactiveMessage(guild.getId(), messageId);
	}

	public ReactiveMessage getReactiveMessage(Guild guild, String message){
		return main.database.isReactiveMessage(guild.getId(), message);
	}

	public boolean checkCommands(GuildMessageReceivedEvent event){
		long start = System.nanoTime();
		String message = startsWithPrefix(event.getGuild(), event.getMessage().getContentRaw());
		if(message != null){
			String command = getCommand(message);
			for(Map.Entry<String, ACommand> c : commands.entrySet()){
				ACommand cmd = c.getValue();
				if(cmd.checkCmd(command)){
					//event.getChannel().sendTyping().queue(); answer is sending too fast and I don't want to block the thread lol
					cmd.run(getArgs(message), event);
					long processingTime = (System.nanoTime() - start) / 1000000;
					main.database.addCommandStatistics(
						event.getGuild().getId(), event.getMessageId(), event.getAuthor().getId(), command, processingTime);
					LOG.info(
						"Command: {}, by: {}, from: {}, took: {}ms", command, event.getAuthor().getName(), event.getGuild().getName(),
						processingTime
					);
					return true;
				}
			}
		}
		return false;
	}

	private String startsWithPrefix(Guild guild, String message){
		String prefix;
		if(message.startsWith(prefix = main.database.getCommandPrefix(guild.getId()))||message.startsWith(
			prefix = "<@!" + guild.getSelfMember().getId() + ">")||message.startsWith(prefix = "<@" + guild.getSelfMember().getId() + ">")){
			return message.substring(prefix.length()).trim();
		}
		return null;
	}

	private String getCommand(String raw){
		return raw.split(" ")[0];
	}

	private String[] getArgs(String message){
		String[] args = message.split(" ");
		return Arrays.copyOfRange(args, 1, args.length);
	}

}
