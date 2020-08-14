package de.anteiku.kittybot.objects.command;

import de.anteiku.kittybot.database.Database;
import de.anteiku.kittybot.objects.Cache;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

public class CommandManager{

	private static final Logger LOG = LoggerFactory.getLogger(CommandManager.class);

	public final Map<String, ACommand> commands;

	public CommandManager(){
		this.commands = new LinkedHashMap<>();
	}

	public static CommandManager build(ACommand... commands){
		return new CommandManager().addCommands(commands);
	}

	public CommandManager addCommands(ACommand... commands){
		for(ACommand command : commands){
			this.commands.put(command.getCommand(), command);
		}
		return this;
	}

	public boolean checkCommands(GuildMessageReceivedEvent event){
		long start = System.nanoTime();
		String message = cutCommandPrefix(event.getGuild(), event.getMessage().getContentRaw());
		if(message != null){
			String command = getCommandString(message);
			for(Map.Entry<String, ACommand> c : commands.entrySet()){
				var cmd = c.getValue();
				if(cmd.checkCmd(command)){
					//event.getChannel().sendTyping().queue(); answer is sending too fast and I don't want to block the thread lol
					var ctx = new CommandContextImpl(event, command, getCommandArguments(message));
					LOG.info("Command: {}, args: {}, by: {}, from: {}", command, ctx.getArgs(), event.getAuthor().getName(), event.getGuild().getName());
					cmd.run(ctx);
					Database.addCommandStatistics(event.getGuild().getId(), event.getMessageId(), event.getAuthor().getId(), command, System.nanoTime() - start);
					return true;
				}
			}
		}
		return false;
	}

	private String cutCommandPrefix(Guild guild, String message){
		String prefix;
		var botId = guild.getSelfMember().getId();
		if(message.startsWith(prefix = Cache.getCommandPrefix(guild.getId())) || message.startsWith(
				prefix = "<@!" + botId + ">") || message.startsWith(prefix = "<@" + botId + ">")){
			return message.substring(prefix.length()).trim();
		}
		return null;
	}

	private String getCommandString(String raw){
		return raw.split("\\s+")[0];
	}

	private String[] getCommandArguments(String message){
		String[] args = message.split(" ");
		return Arrays.copyOfRange(args, 1, args.length);
	}

}
