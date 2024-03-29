package de.kittybot.kittybot.modules;

import de.kittybot.kittybot.objects.enums.Environment;
import de.kittybot.kittybot.objects.module.Module;
import de.kittybot.kittybot.slashcommands.application.Command;
import de.kittybot.kittybot.utils.Config;
import de.kittybot.kittybot.utils.annotations.Ignore;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.api.utils.data.DataObject;
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
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CommandsModule extends Module{

	public static final Route COMMANDS_CREATE = Route.custom(Method.PUT, "applications/{application.id}/commands");
	public static final Route GUILD_COMMANDS_CREATE = Route.custom(Method.PUT, "applications/{application.id}/guilds/{guild.id}/commands");
	public static final Route GUILD_COMMANDS = Route.custom(Method.GET, "applications/{application.id}/guilds/{guild.id}/commands");
	public static final Route GUILD_COMMAND_CREATE = Route.custom(Method.POST, "applications/{application.id}/guilds/{guild.id}/commands");
	public static final Route GUILD_COMMAND_EDIT = Route.custom(Method.PATCH, "applications/{application.id}/guilds/{guild.id}/commands/{command.id}");
	public static final Route GUILD_COMMAND_DELETE = Route.custom(Method.DELETE, "applications/{application.id}/guilds/{guild.id}/commands/{command.id}");

	private static final Logger LOG = LoggerFactory.getLogger(CommandsModule.class);
	private static final String COMMANDS_PACKAGE = "de.kittybot.kittybot.commands";

	private Map<Long, Command> commands;

	@Override
	public Set<Class<? extends Module>> getDependencies(){
		return Set.of(RequestModule.class);
	}

	@Override
	public void onEnable(){
		this.commands = new HashMap<>();
		var commands = scanCommands();
		var env = Environment.getCurrent();
		if(env == Environment.PRODUCTION){
			deployCommands(-1L, commands);
		}
		else if(env == Environment.DEVELOPMENT){
			if(Config.SUPPORT_GUILD_ID != -1){
				deployCommands(Config.SUPPORT_GUILD_ID, commands);
			}
		}
	}

	public Map<String, Command> scanCommands(){
		LOG.info("Loading commands...");
		try(var result = new ClassGraph().acceptPackages(COMMANDS_PACKAGE).enableAnnotationInfo().scan()){
			var commands = result.getAllClasses().stream()
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
			LOG.info("Loaded {} commands", commands.size());
			return commands;
		}
	}

	public void deployCommands(long guildId, Map<String, Command> commands){
		LOG.info("Registering commands {}...", guildId == -1 ? "global" : "for guild " + guildId);

		var rqBody = RequestBody.create(
			MediaType.parse("application/json"),
			DataArray.fromCollection(commands.values().stream().map(Command::toJSON).collect(Collectors.toList())).toJson()
		);

		var route = guildId == -1L ? COMMANDS_CREATE.compile(String.valueOf(Config.BOT_ID)) : GUILD_COMMANDS_CREATE.compile(String.valueOf(Config.BOT_ID), String.valueOf(guildId));
		try(var resp = newCall(route, rqBody).execute()){
			if(!resp.isSuccessful()){
				var body = resp.body();
				LOG.error("Registering commands failed. Request Body: {}, Response Body: {}", commands, body == null ? "null" : body.string());
			}
			var body = resp.body();
			if(body == null){
				return;
			}
			if(guildId == -1L && Environment.is(Environment.PRODUCTION) && !Config.DISCORD_SERVICES_TOKEN.isBlank()){
				this.modules.get(RequestModule.class).uploadCommands(commands.values());
			}
			LOG.info("Registered " + commands.size() + " commands...");
			loadCommands(DataArray.fromJson(body.byteStream()), commands);
		}
		catch(IOException e){
			LOG.error("Error while processing registerCommands", e);
		}
	}

	private Call newCall(Route.CompiledRoute route, RequestBody body){
		return this.modules.getHttpClient().newCall(newBuilder(route).method(route.getMethod().name(), body).build());
	}

	private void loadCommands(DataArray data, Map<String, Command> commandMap){
		for(var i = 0; i < data.length(); i++){
			var o = data.getObject(i);
			var cmd = commandMap.get(o.getString("name"));
			if(cmd != null){
				this.commands.put(o.getLong("id"), cmd);
			}
		}
	}

	private Request.Builder newBuilder(Route.CompiledRoute route){
		return new Request.Builder()
			.url(Requester.DISCORD_API_PREFIX + route.getCompiledRoute())
			.addHeader("Authorization", "Bot " + Config.BOT_TOKEN);
	}

	public void reDeployCommands(long guildId){
		deployCommands(guildId, getCommands().values().stream().collect(Collectors.toMap(Command::getName, Function.identity())));
	}

	public Map<Long, Command> getCommands(){
		return this.commands;
	}

	public long registerGuildCommand(long guildId, DataObject command){
		var rqBody = RequestBody.create(MediaType.parse("application/json"), command.toJson());

		var route = GUILD_COMMAND_CREATE.compile(String.valueOf(Config.BOT_ID), String.valueOf(guildId));
		try(var resp = newCall(route, rqBody).execute()){
			if(!resp.isSuccessful()){
				var body = resp.body();
				LOG.error("Registering command failed. Request Body: {}, Response Body: {}", command, body == null ? "null" : body.string());
				return -1L;
			}
			var body = resp.body();
			if(body == null){
				LOG.error("empty body received");
				return -1L;
			}
			LOG.info("Registered command");
			return DataObject.fromJson(body.byteStream()).getLong("id");
		}
		catch(IOException e){
			LOG.error("Error while processing registerGuildCommand", e);
		}
		return -1L;
	}

	public boolean deleteGuildCommand(long guildId, long commandId){
		var route = GUILD_COMMAND_DELETE.compile(String.valueOf(Config.BOT_ID), String.valueOf(guildId), String.valueOf(commandId));
		try(var resp = newCall(route, null).execute()){
			if(resp.code() == 204){
				LOG.info("Deleted command");
				return true;
			}
			var body = resp.body();
			LOG.error("Deleting command failed. Command ID: {}, Response Body: {}", commandId, body == null ? "null" : body.string());
		}
		catch(IOException e){
			LOG.error("Error while processing deleteGuildCommand", e);
		}
		return false;
	}

	public boolean editGuildCommand(long guildId, long commandId, DataObject command){
		var rqBody = RequestBody.create(MediaType.parse("application/json"), command.toJson());

		var route = GUILD_COMMAND_EDIT.compile(String.valueOf(Config.BOT_ID), String.valueOf(guildId), String.valueOf(commandId));
		try(var resp = newCall(route, rqBody).execute()){
			if(resp.code() == 200){
				LOG.info("Edited command");
				return true;
			}
			var body = resp.body();
			LOG.error("Editing command failed. Command ID: {}, Response Body: {}", commandId, body == null ? "null" : body.string());
		}
		catch(IOException e){
			LOG.error("Error while processing editGuildCommand", e);
		}
		return false;
	}

	public DataArray getGuildCommands(long guildId){
		var route = GUILD_COMMANDS.compile(String.valueOf(Config.BOT_ID), String.valueOf(guildId));
		try(var resp = newCall(route, null).execute()){
			var body = resp.body();
			if(!resp.isSuccessful()){
				LOG.error("Registering command failed. Guild ID: {}, Response Body: {}", guildId, body == null ? "null" : body.string());
				return null;
			}
			if(body == null){
				return null;
			}
			return DataArray.fromJson(body.byteStream());
		}
		catch(IOException e){
			LOG.error("Error while processing deleteGuildCommand", e);
		}
		return null;
	}

	public void deleteAllCommands(long guildId){
		LOG.info("Deleting commands {}...", guildId == -1 ? "global" : "for guild " + guildId);
		var rqBody = RequestBody.create(MediaType.parse("application/json"), DataArray.empty().toString());

		var route = guildId == -1L ? COMMANDS_CREATE.compile(String.valueOf(Config.BOT_ID)) : GUILD_COMMANDS_CREATE.compile(String.valueOf(Config.BOT_ID), String.valueOf(guildId));
		try(var resp = newCall(route, rqBody).execute()){
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

}
