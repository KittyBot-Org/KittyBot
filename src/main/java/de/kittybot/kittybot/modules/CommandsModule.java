package de.kittybot.kittybot.modules;

import de.kittybot.kittybot.objects.enums.Environment;
import de.kittybot.kittybot.objects.module.Module;
import de.kittybot.kittybot.slashcommands.application.Command;
import de.kittybot.kittybot.utils.Config;
import de.kittybot.kittybot.utils.annotations.Ignore;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.internal.requests.Method;
import net.dv8tion.jda.internal.requests.Requester;
import net.dv8tion.jda.internal.requests.Route;
import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CommandsModule extends Module{

	public static final Route COMMANDS_CREATE = Route.custom(Method.PUT, "applications/{application.id}/commands");
	public static final Route GUILD_COMMANDS_CREATE = Route.custom(Method.PUT, "applications/{application.id}/guilds/{guild.id}/commands");

	private static final Logger LOG = LoggerFactory.getLogger(CommandsModule.class);
	private static final String COMMANDS_PACKAGE = "de.kittybot.kittybot.commands";
	private static final Set<Class<? extends Module>> DEPENDENCIES = Set.of(RequestModule.class);

	private Map<String, Command> commands;

	@Override
	public Set<Class<? extends Module>> getDependencies(){
		return DEPENDENCIES;
	}

	@Override
	public void onEnable(){
		scanCommands();
		var env = Environment.getCurrentEnv();
		if(env == Environment.PRODUCTION){
			deployAllCommands(-1L);
		}
		else if(env == Environment.DEVELOPMENT){
			if(Config.SUPPORT_GUILD_ID != -1){
				deployAllCommands(Config.SUPPORT_GUILD_ID);
			}
		}
	}

	public void scanCommands(){
		LOG.info("Loading commands...");
		try(var result = new ClassGraph().acceptPackages(COMMANDS_PACKAGE).enableAnnotationInfo().scan()){
			this.commands = result.getSubclasses(Command.class.getName()).stream()
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
		LOG.info("Registering commands {}...", guildId == -1 ? "global" : "for guild " + guildId);

		var commands = DataArray.fromCollection(this.commands.values().stream().map(Command::toJSON).collect(Collectors.toList()));
		var rqBody = RequestBody.create(commands.toJson(), MediaType.parse("application/json"));

		var route = guildId == -1L ? COMMANDS_CREATE.compile(String.valueOf(Config.BOT_ID)) : GUILD_COMMANDS_CREATE.compile(String.valueOf(Config.BOT_ID), String.valueOf(guildId));
		try(var resp = put(route, rqBody).execute()){
			if(!resp.isSuccessful()){
				var body = resp.body();
				LOG.error("Registering commands failed. Body: {}", body == null ? "null" : body.string());
				return;
			}
		}
		catch(IOException e){
			LOG.error("Error while processing registerCommands", e);
		}
		if(guildId == -1L && !Config.DISCORDSERVICES_TOKEN.isBlank()){
			this.modules.get(RequestModule.class).uploadCommands(this.commands);
		}
		LOG.info("Registered " + this.commands.size() + " commands...");
	}

	private Call put(Route.CompiledRoute route, RequestBody body){
		return this.modules.getHttpClient().newCall(newBuilder(route).put(body).build());
	}

	private Request.Builder newBuilder(Route.CompiledRoute route){
		return new Request.Builder()
			.url(Requester.DISCORD_API_PREFIX + route.getCompiledRoute())
			.addHeader("Authorization", "Bot " + Config.BOT_TOKEN);
	}

	public void deleteAllCommands(long guildId){
		LOG.info("Deleting commands {}...", guildId == -1 ? "global" : "for guild " + guildId);
		var rqBody = RequestBody.create(DataArray.empty().toString(), MediaType.parse("application/json"));

		var route = guildId == -1L ? COMMANDS_CREATE.compile(String.valueOf(Config.BOT_ID)) : GUILD_COMMANDS_CREATE.compile(String.valueOf(Config.BOT_ID), String.valueOf(guildId));
		try(var resp = put(route, rqBody).execute()){
			if(!resp.isSuccessful()){
				var body = resp.body();
				LOG.error("Deleting commands failed. Body: {}", body == null ? "null" : body.string());
				return;
			}
		}
		catch(IOException e){
			LOG.error("Error while processing deleteAllCommands", e);
		}
		LOG.info("Deleted all commands...");
	}

	public Map<String, Command> getCommands(){
		return this.commands;
	}

}
