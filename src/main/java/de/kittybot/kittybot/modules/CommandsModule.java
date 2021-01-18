package de.kittybot.kittybot.modules;

import de.kittybot.kittybot.slashcommands.application.Command;
import de.kittybot.kittybot.module.Module;
import de.kittybot.kittybot.utils.Config;
import io.github.classgraph.ClassGraph;
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
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CommandsModule extends Module{

	private static final Logger LOG = LoggerFactory.getLogger(CommandsModule.class);

	public static final Route COMMAND_CREATE = Route.custom(Method.POST, "applications/{application.id}/commands");
	public static final Route COMMANDS_GET = Route.custom(Method.GET, "applications/{application.id}/commands");
	public static final Route COMMAND_DELETE = Route.custom(Method.DELETE, "applications/{application.id}/commands/{command.id}");

	public static final Route GUILD_COMMAND_CREATE = Route.custom(Method.POST, "applications/{application.id}/guilds/{guild.id}/commands");
	public static final Route GUILD_COMMANDS_GET = Route.custom(Method.GET, "applications/{application.id}/guilds/{guild.id}/commands");
	public static final Route GUILD_COMMAND_DELETE = Route.custom(Method.DELETE, "applications/{application.id}/guilds/{guild.id}/commands/{command.id}");

	private static final String COMMANDS_PACKAGE = "de.kittybot.kittybot.commands";

	private Map<String, Command> commands;

	@Override
	public void onEnable(){
		scanCommands();
	}

	public void scanCommands(){
		LOG.info("Loading commands...");
		this.commands = new HashMap<>();
		try(var result = new ClassGraph().acceptPackages(COMMANDS_PACKAGE).verbose().scan()){
			for(var cls : result.getSubclasses(Command.class.getName())){
				var instance = cls.loadClass().getDeclaredConstructors()[0].newInstance();
				if(!(instance instanceof Command)){
					continue;
				}
				var command = (Command) instance;
				this.commands.put(command.getName(), command);
			}
			LOG.info("Loaded {} commands", this.commands.size());
		}
		catch(IllegalAccessException | InvocationTargetException | InstantiationException e){
			LOG.error("There was an error while registering commands!", e);
		}
	}

	public void deployDiffs(long guildId){
		var oldCmds = readCommands(guildId).stream().collect(Collectors.toMap(cmd -> cmd.getString("name"), cmd -> cmd));
		var newCmds = this.commands.values().stream().collect(Collectors.toMap(Command::getName, Command::toJSON));

		var cmdsToCreate = oldCmds.entrySet().stream().filter(cmd -> !newCmds.containsKey(cmd.getKey()) || newCmds.get(cmd.getKey()).toString().equals(cmd.getValue().toString())).map(Map.Entry::getValue).collect(Collectors.toSet());
		var cmdsToDelete = newCmds.keySet().stream().filter(cmd -> !oldCmds.containsKey(cmd)).map(oldCmds::get).collect(Collectors.toSet());

		if(!cmdsToDelete.isEmpty()){
			cmdsToDelete.forEach(cmd -> deleteCommand(cmd.getLong("id"), guildId));
		}
		if(!cmdsToCreate.isEmpty()){
			cmdsToCreate.forEach(cmd -> deployCommand(cmd, guildId));
		}
	}

	public void deployAllCommands(long guildId){
		LOG.info("Registering commands...");
		for(var cmd : this.commands.values()){
			deployCommand(cmd.toJSON(), guildId);
		}
		LOG.info("Registered " + this.commands.size() + " commands...");
	}

	public void deployCommand(DataObject obj, long guildId){
		var json = obj.toString();
		LOG.debug("Registering command: {}", json);
		var rqBody = RequestBody.create(json, MediaType.parse("application/json"));

		var route = guildId == -1L ? COMMAND_CREATE.compile(String.valueOf(Config.BOT_ID)) : GUILD_COMMAND_CREATE.compile(String.valueOf(Config.BOT_ID), String.valueOf(guildId));
		try(var resp = post(route, rqBody).execute()){
			if(!resp.isSuccessful()){
				var body = resp.body();
				LOG.error("Registering command '" + obj.getString("name") + "' failed. Body: {}", body == null ? "null" : body.string() + "\nRequest Body: " + json);
				return;
			}
			LOG.debug("Registered command with name: {}", obj.getString("name"));
		}
		catch(IOException e){
			LOG.error("Error while processing registerCommands", e);
		}
	}

	public List<DataObject> readCommands(long guildId){
		var route = guildId == -1L ? COMMANDS_GET.compile(String.valueOf(Config.BOT_ID)) : GUILD_COMMANDS_GET.compile(String.valueOf(Config.BOT_ID), String.valueOf(guildId));
		var cmds = new ArrayList<DataObject>();
		try(var resp = get(route).execute()){
			var body = resp.body();
			if(!resp.isSuccessful() || body == null){
				return cmds;
			}
			var json = DataArray.fromJson(body.string());
			for(var i = 0; i < json.length(); i++){
				cmds.add(json.getObject(i));
			}
		}
		catch(IOException e){
			LOG.error("Error while reading commands", e);
		}
		return cmds;
	}

	public void deleteAllCommands(long guildId){
		var cmds = readCommands(guildId);
		for(var cmd : cmds){
			deleteCommand(cmd.getLong("id"), guildId);
		}
	}

	public void deleteCommand(long commandId, long guildId){
		LOG.debug("Registering command: {}", commandId);
		var route = guildId == -1L ? COMMAND_DELETE.compile(String.valueOf(Config.BOT_ID), String.valueOf(commandId)) : GUILD_COMMAND_DELETE.compile(String.valueOf(Config.BOT_ID), String.valueOf(guildId), String.valueOf(commandId));

		try(var resp = delete(route).execute()){
			if(!resp.isSuccessful()){
				var body = resp.body();
				LOG.error("Error while deleting command: {}", body == null ? "null" : body.string());
			}
		}
		catch(IOException e){
			LOG.error("Error while deleting command", e);
		}
	}

	private Call post(Route.CompiledRoute route, RequestBody body){
		return this.modules.getHttpClient().newCall(newBuilder(route).post(body).build());
	}

	private Request.Builder newBuilder(Route.CompiledRoute route){
		return new Request.Builder()
				.url(Requester.DISCORD_API_PREFIX + route.getCompiledRoute())
				.addHeader("Authorization", "Bot " + Config.BOT_TOKEN);
	}

	private Call get(Route.CompiledRoute route){
		return this.modules.getHttpClient().newCall(newBuilder(route).get().build());
	}

	private Call delete(Route.CompiledRoute route){
		return this.modules.getHttpClient().newCall(newBuilder(route).delete().build());
	}

	public Map<String, Command> getCommands(){
		return this.commands;
	}

}
