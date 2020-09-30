package de.kittybot.kittybot.objects.command;

import de.kittybot.kittybot.database.Database;
import de.kittybot.kittybot.objects.cache.PrefixCache;
import io.github.classgraph.ClassGraph;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.jooq.types.YearToSecond;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CommandManager{

	private static final Logger LOG = LoggerFactory.getLogger(CommandManager.class);
	private static final ClassGraph CLASS_GRAPH = new ClassGraph().whitelistPackages("de.kittybot.kittybot.commands");
	private static final Map<String, ACommand> COMMANDS = new ConcurrentHashMap<>();
	private static final Map<String, ACommand> DISTINCT_COMMANDS = new ConcurrentHashMap<>();

	public static void registerCommands(){
		try(var result = CLASS_GRAPH.scan()){
			for(var cls : result.getAllClasses()){
				var instance = (ACommand) cls.loadClass().getDeclaredConstructor().newInstance();
				var command = instance.getCommand();
				COMMANDS.put(command, instance);
				DISTINCT_COMMANDS.put(command, instance);
				for(var alias : instance.getAliases()){
					COMMANDS.put(alias, instance);
				}
			}
		}
		catch(Exception e){
			LOG.error("There was an error while registering commands!", e);
		}
	}

	public static boolean checkCommands(GuildMessageReceivedEvent event){
		long start = System.nanoTime();
		String message = cutCommandPrefix(event.getGuild(), event.getMessage().getContentRaw());
		if(message != null){
			String command = getCommandString(message);
			for(var entry : COMMANDS.entrySet()){
				var cmd = entry.getValue();
				if(cmd.checkCmd(command)){
					//event.getChannel().sendTyping().queue(); answer is sending too fast and I don't want to block the thread lol
					var ctx = new CommandContext(event, command, message);
					LOG.info("Command: {}, args: {}, by: {}, from: {}({})", command, ctx.getArgs(), event.getAuthor().getName(), event.getGuild().getName(), event.getGuild().getId());
					cmd.run(ctx);
					Database.addCommandStatistics(event.getGuild().getId(), event.getMessageId(), event.getAuthor().getId(), command, YearToSecond.valueOf(Duration.of(System.nanoTime() - start, ChronoUnit.NANOS)));
					return true;
				}
			}
		}
		return false;
	}

	private static String cutCommandPrefix(Guild guild, String message){
		String prefix;
		var botId = guild.getSelfMember().getId();
		if(message.startsWith(prefix = PrefixCache.getCommandPrefix(guild.getId())) || message.startsWith(
				prefix = "<@!" + botId + ">") || message.startsWith(prefix = "<@" + botId + ">")){
			return message.substring(prefix.length()).trim();
		}
		return null;
	}

	private static String getCommandString(String raw){
		return raw.split("\\s+")[0];
	}

	public static Map<String, ACommand> getCommands(){
		return COMMANDS;
	}

	public static Map<String, ACommand> getDistinctCommands(){
		return DISTINCT_COMMANDS;
	}

}
