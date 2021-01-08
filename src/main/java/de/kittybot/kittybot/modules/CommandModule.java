package de.kittybot.kittybot.modules;

import de.kittybot.kittybot.command.Args;
import de.kittybot.kittybot.command.Command;
import de.kittybot.kittybot.command.CommandContext;
import de.kittybot.kittybot.command.ReactionContext;
import de.kittybot.kittybot.module.Module;
import de.kittybot.kittybot.module.Modules;
import de.kittybot.kittybot.utils.Config;
import de.kittybot.kittybot.utils.exporters.Metrics;
import io.github.classgraph.ClassGraph;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class CommandModule extends Module{

	public static final String ARGUMENT_REGEX = "\\s+";
	private static final Logger LOG = LoggerFactory.getLogger(CommandModule.class);
	private static final String COMMAND_PACKAGE = "de.kittybot.kittybot.commands";
	private static final Pattern BOT_MENTION = Pattern.compile("<@!?" + Config.BOT_ID + ">");

	private final Modules modules;
	private final Map<String, Command> commands;
	private final Map<String, Command> allCommands;

	public CommandModule(Modules modules){
		this.modules = modules;
		this.commands = new HashMap<>();
		this.allCommands = new HashMap<>();
		try(var result = new ClassGraph().acceptPackages(COMMAND_PACKAGE).scan()){
			for(var cls : result.getAllClasses()){
				var constructors = cls.loadClass().getDeclaredConstructors();
				if(constructors.length == 0){
					LOG.error("You stupid idiot forgot to add a constructor to your '{}' command class ", cls.getSimpleName());
					continue;
				}
				if(constructors[0].getParameterCount() > 0){
					continue;
				}
				var instance = constructors[0].newInstance();
				if(!(instance instanceof Command)){
					LOG.warn("You stupid idiot have a non command class '{}' in your commands package", cls.getSimpleName());
					continue;
				}
				var command = (Command) instance;
				this.commands.put(command.getPath(), command);
				this.allCommands.put(command.getPath(), command);
				for(var cmd : command.getChildren()){
					this.allCommands.put(cmd.getPath(), cmd);
				}
			}
			LOG.info("Loaded {} root commands & {} commands in total", this.commands.size(), this.allCommands.size());
		}
		catch(IllegalAccessException | InvocationTargetException | InstantiationException e){
			LOG.error("There was an error while registering commands!", e);
		}
	}

	@Override
	public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event){
		var start = System.currentTimeMillis();
		var settings = this.modules.get(SettingsModule.class).getSettings(event.getGuild().getIdLong());

		if(settings.isBotDisabledInChannel(event.getChannel().getIdLong())){
			return;
		}
		if(settings.isBotIgnoredUser(event.getAuthor().getIdLong())){
			return;
		}

		var message = trimPrefix(event);
		if(message == null){
			return;
		}

		var args = new Args(Arrays.asList(message.split(CommandModule.ARGUMENT_REGEX)));
		for(var command : this.commands.values()){
			if(command.check(args.get(0))){
				command.process(new CommandContext(event, this.modules, command.getPath(), args, message));
				Metrics.COMMAND_COUNTER.labels(args.get(0)).inc();
				Metrics.COMMAND_LATENCY.observe(System.currentTimeMillis() - start);
				return;
			}
		}

	}

	@Override
	public void onGuildMessageReactionAdd(@Nonnull GuildMessageReactionAddEvent event){
		if(event.getMember().getUser().isBot()){
			return;
		}
		var settings = this.modules.get(SettingsModule.class).getSettings(event.getGuild().getIdLong());
		if(settings.isBotDisabledInChannel(event.getChannel().getIdLong())){
			return;
		}
		if(settings.isBotIgnoredUser(event.getUserIdLong())){
			return;
		}

		var reactiveMessage = this.modules.get(ReactiveMessageModule.class).getReactiveMessage(event.getMessageIdLong());
		if(reactiveMessage == null){
			return;
		}
		if(reactiveMessage.getAllowed() == -1L || reactiveMessage.getAllowed() == event.getUserIdLong()){
			this.allCommands.get(reactiveMessage.getPath()).onReactionAdd(
					new ReactionContext(event, this, this.modules.get(ReactiveMessageModule.class), reactiveMessage));
			return;
		}
		event.getReaction().removeReaction(event.getUser()).queue();
	}

	private String trimPrefix(GuildMessageReceivedEvent event){
		var message = event.getMessage().getContentRaw();
		var prefix = this.modules.get(SettingsModule.class).getPrefix(event.getGuild().getIdLong());
		if(message.startsWith(prefix)){
			return message.substring(prefix.length()).trim();
		}
		var matcher = BOT_MENTION.matcher(message);
		if(matcher.find()){
			return message.substring(matcher.regionEnd()).trim();
		}
		return null;
	}

	public Collection<Command> getCommands(){
		return this.commands.values();
	}

}
