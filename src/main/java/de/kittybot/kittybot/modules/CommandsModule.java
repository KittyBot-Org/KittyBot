package de.kittybot.kittybot.modules;

import de.kittybot.kittybot.objects.enums.Environment;
import de.kittybot.kittybot.objects.module.Module;
import de.kittybot.kittybot.slashcommands.application.Command;
import de.kittybot.kittybot.utils.Config;
import de.kittybot.kittybot.utils.annotations.Ignore;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.requests.restaction.CommandUpdateAction;
import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.entities.GuildImpl;
import net.dv8tion.jda.internal.requests.Method;
import net.dv8tion.jda.internal.requests.Requester;
import net.dv8tion.jda.internal.requests.Route;
import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CommandsModule extends Module{

	private static final Logger LOG = LoggerFactory.getLogger(CommandsModule.class);
	private static final String COMMANDS_PACKAGE = "de.kittybot.kittybot.commands";

	private Map<String, Command> commands;

	@Override
	public void onEnable(){
		scanCommands();
	}

	@Override
	public void onReady(@NotNull ReadyEvent event){
		if(Environment.getCurrent() == Environment.PRODUCTION){
			deployAllCommands(-1L);
		}
	}

	@Override
	public void onGuildReady(@NotNull GuildReadyEvent event){
		if(Environment.getCurrent() == Environment.DEVELOPMENT && Config.SUPPORT_GUILD_ID == event.getGuild().getIdLong()){
			deployAllCommands(Config.SUPPORT_GUILD_ID);
		}
	}

	public void scanCommands(){
		LOG.info("Loading commands...");
		try(var result = new ClassGraph().acceptPackages(COMMANDS_PACKAGE).enableAnnotationInfo().scan()){
			this.commands = result.getAllClasses().stream()
				.filter(cls -> !cls.hasAnnotation(Ignore.class.getName()))
				.map(ClassInfo::loadClass)
				.filter(Command.class::isAssignableFrom)
				.map(clazz -> {
					try{
						return (Command) clazz.getDeclaredConstructor().newInstance();
					}
					catch(Exception e){
						LOG.info("Error while registering command: '{}'", clazz.getSimpleName(), e);
					}
					return null;
				})
				.filter(Objects::nonNull)
				.collect(Collectors.toMap(Command::getName, Function.identity()));
		}
		LOG.info("Loaded {} commands", this.commands.size());
	}

	public void deployAllCommands(long guildId){
		CommandUpdateAction action;
		if(guildId == -1L){
			action = this.modules.getJDA().updateCommands();
		}
		else{
			var guild = this.modules.getGuildById(guildId);
			if(guild == null){
				return;
			}
			action = guild.updateCommands();
		}
		action.addCommands(this.commands.values().stream().map(Command::toData).collect(Collectors.toList())).queue();
	}

	public void deleteAllCommands(long guildId){
		CommandUpdateAction action;
		if(guildId == -1L){
			action = this.modules.getJDA().updateCommands();
		}
		else{
			var guild = this.modules.getGuildById(guildId);
			if(guild == null){
				return;
			}
			action = guild.updateCommands();
		}
		action.queue();
	}

	public Map<String, Command> getCommands(){
		return this.commands;
	}

}
