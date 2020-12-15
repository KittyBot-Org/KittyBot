package de.kittybot.kittybot.managers;

import de.kittybot.kittybot.command.Command;
import de.kittybot.kittybot.command.ctx.CommandContext;
import de.kittybot.kittybot.command.ctx.ReactionContext;
import io.github.classgraph.ClassGraph;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageDeleteEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class CommandManager extends ListenerAdapter{

	private static final Logger LOG = LoggerFactory.getLogger(CommandManager.class);
	private static final String ARGUMENT_REGEX = "\\s+";

	private final Map<String, Command> commands;
	private final Map<String, Command> allCommands;
	private final Set<Long> adminIds;
	private final GuildSettingsManager guildSettingsManager;
	private final CommandResponseManager commandResponseManager;
	private final ReactiveMessageManager reactiveMessageManager;

	public CommandManager(GuildSettingsManager guildSettingsManager, CommandResponseManager commandResponseManager, ReactiveMessageManager reactiveMessageManager, String commandsPackage, Set<Long> adminIds, Object[] initArgs){
		this.guildSettingsManager = guildSettingsManager;
		this.commandResponseManager = commandResponseManager;
		this.reactiveMessageManager = reactiveMessageManager;
		this.adminIds = adminIds;
		this.commands = new HashMap<>();
		this.allCommands = new HashMap<>();
		try(var result = new ClassGraph().acceptPackages(commandsPackage).scan()){
			for(var cls : result.getAllClasses()){
				var constructors = cls.loadClass().getDeclaredConstructors();
				if(constructors.length == 0){
					LOG.error("You stupid idiot forgot to add a constructor to your '{}' command class ", cls.getSimpleName());
					continue;
				}
				if(constructors[0].getParameterCount() == initArgs.length && !(constructors[0].getParameterTypes()[0].isNestmateOf(Command.class))){
					var instance = constructors[0].newInstance(initArgs);
					if(!(instance instanceof Command)){
						LOG.warn("You stupid idiot have a non command class named '{}' in your commands package", cls.getSimpleName());
						continue;
					}
					var command = (Command) instance;
					this.commands.put(command.getPath(), command);
					this.allCommands.put(command.getPath(), command);
					for(var cmd : command.getChildren()){
						this.allCommands.put(cmd.getPath(), cmd);
					}
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
		var message = trimPrefix(event);
		if(message == null){
			return;
		}
		var args = Arrays.asList(message.split(ARGUMENT_REGEX));
		for(var command : this.commands.values()){
			if(command.check(args.get(0))){
				command.process(new CommandContext(event, this, command.getPath(), args, message));
				return;
			}
		}
	}

	@Override
	public void onGuildMessageDelete(@NotNull GuildMessageDeleteEvent event){
		var commandResponse = this.commandResponseManager.get(event.getMessageIdLong());
		if(commandResponse != -1 && event.getGuild().getSelfMember().hasPermission(Permission.MESSAGE_MANAGE)){
			this.commandResponseManager.remove(event.getMessageIdLong());
			event.getChannel().deleteMessageById(commandResponse).reason("deleted due to command deletion").queue();
		}
	}

	@Override
	public void onGuildMessageReactionAdd(@Nonnull GuildMessageReactionAddEvent event){
		if(event.getMember().getUser().isBot()){
			return;
		}
		var reactiveMessage = this.reactiveMessageManager.getReactiveMessage(event.getMessageIdLong());
		if(reactiveMessage == null){
			return;
		}
		if(reactiveMessage.getAllowed() == -1L || reactiveMessage.getAllowed() == event.getUserIdLong()){
			this.allCommands.get(reactiveMessage.getPath()).onReactionAdd(new ReactionContext(event, this, reactiveMessage));
			return;
		}
		event.getReaction().removeReaction(event.getUser()).queue();
	}

	@Override
	public void onGuildReady(@NotNull GuildReadyEvent event){
		this.guildSettingsManager.insertGuildSettingsIfNotExists(event.getGuild());
	}

	@Override
	public void onGuildJoin(@Nonnull GuildJoinEvent event){
		this.guildSettingsManager.insertGuildSettings(event.getGuild());
	}

	@Override
	public void onGuildLeave(@Nonnull GuildLeaveEvent event){
		var guildId = event.getGuild().getIdLong();
		this.guildSettingsManager.deleteGuildSettings(guildId);
		//this.reactiveMessageManager.prune(guildId);
	}


	private String trimPrefix(GuildMessageReceivedEvent event){
		var message = event.getMessage().getContentRaw();
		var guild = event.getGuild();
		var botId = guild.getSelfMember().getId();
		var prefix = this.guildSettingsManager.getPrefix(event.getGuild().getIdLong());
		if(message.startsWith(prefix) || message.startsWith(prefix = "<@!" + botId + ">") || message.startsWith(prefix = "<@" + botId + ">")){
			return message.substring(prefix.length()).trim();
		}
		return null;
	}

	public Collection<Command> getCommands(){
		return this.commands.values();
	}

	public Set<Long> getAdminIds(){
		return this.adminIds;
	}

	public CommandResponseManager getCommandResponseManager(){
		return this.commandResponseManager;
	}

	public GuildSettingsManager getGuildSettingsManager(){
		return this.guildSettingsManager;
	}

	public ReactiveMessageManager getReactiveMessageManager(){
		return this.reactiveMessageManager;
	}

	public static class Builder{

		private final String commandsPackage;
		private final Set<Long> botOwnerIds;
		private Object[] initArgs;
		private GuildSettingsManager guildSettingsManager;
		private CommandResponseManager commandResponseManager;
		private ReactiveMessageManager reactiveMessageManager;

		public Builder(String commandsPackage){
			this.commandsPackage = commandsPackage;
			this.botOwnerIds = new HashSet<>();
			this.initArgs = new Object[]{};
		}

		public Builder addBotOwnerIds(Collection<Long> ids){
			this.botOwnerIds.addAll(ids);
			return this;
		}

		public Builder setInitArgs(Object... initArgs){
			this.initArgs = initArgs;
			return this;
		}

		public Builder setGuildSettingsManager(GuildSettingsManager guildSettingsManager){
			this.guildSettingsManager = guildSettingsManager;
			return this;
		}

		public Builder setCommandResponseManager(CommandResponseManager commandResponseManager){
			this.commandResponseManager = commandResponseManager;
			return this;
		}

		public Builder setReactiveMessageManager(ReactiveMessageManager reactiveMessageManager){
			this.reactiveMessageManager = reactiveMessageManager;
			return this;
		}

		public CommandManager build(){
			return new CommandManager(
					this.guildSettingsManager,
					this.commandResponseManager,
					this.reactiveMessageManager,
					this.commandsPackage,
					this.botOwnerIds,
					this.initArgs
			);
		}

	}

}
