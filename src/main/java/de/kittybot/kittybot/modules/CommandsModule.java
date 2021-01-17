package de.kittybot.kittybot.modules;

import de.kittybot.kittybot.slashcommands.application.Command;
import de.kittybot.kittybot.module.Module;
import de.kittybot.kittybot.utils.Config;
import io.github.classgraph.ClassGraph;
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
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class CommandsModule extends Module{

	public static final Route GUILD_COMMAND_CREATE = Route.custom(Method.POST, "applications/{application.id}/guilds/{guild.id}/commands");
	public static final Route GUILD_COMMAND_GET = Route.custom(Method.GET, "applications/{application.id}/guilds/{guild.id}/commands");
	public static final Route GUILD_COMMAND_DELETE = Route.custom(Method.DELETE, "applications/{application.id}/guilds/{guild.id}/commands/{command.id}");
	private static final Logger LOG = LoggerFactory.getLogger(CommandsModule.class);
	private static final String COMMANDS_PACKAGE = "de.kittybot.kittybot.commands";

	private static final long DEV_GUILD_ID = 730879265956167740L;

	private List<Command> commands;

	@Override
	public void onEnable(){
		//deleteAllCommands();
		loadCommands();
		registerCommands();
	}

	public void loadCommands(){
		LOG.info("Loading commands...");
		this.commands = new ArrayList<>();
		try(var result = new ClassGraph().acceptPackages(COMMANDS_PACKAGE).scan()){
			for(var cls : result.getSubclasses(Command.class.getName())){
				var instance = cls.loadClass().getDeclaredConstructors()[0].newInstance();
				if(!(instance instanceof Command)){
					continue;
				}
				var command = (Command) instance;
				this.commands.add(command);
			}
			LOG.info("Loaded {} commands", this.commands.size());
		}
		catch(IllegalAccessException | InvocationTargetException | InstantiationException e){
			LOG.error("There was an error while registering commands!", e);
		}
	}

	public void registerCommands(){
		LOG.info("Registering commands...");
		for(var cmd : commands){
			registerCommand(cmd);
		}
		LOG.info("Registered " + this.commands.size() + "commands...");
	}

	public void registerCommand(Command cmd){
		LOG.debug("Registering command: {}", cmd.toJSON());
		var rqBody = RequestBody.create(cmd.toJSON().toString(), MediaType.parse("application/json"));
		try(var resp = post(GUILD_COMMAND_CREATE.compile(String.valueOf(Config.BOT_ID), String.valueOf(DEV_GUILD_ID)), rqBody).execute()){
			if(!resp.isSuccessful()){
				var body = resp.body();
				LOG.error("Registered command failed. Body: {}", body == null ? "null" : body.string());
				return;
			}
			LOG.debug("Registered command with name: {}", cmd.getName());
		}
		catch(IOException e){
			LOG.error("Error while processing registerCommands", e);
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

	public void deleteAllCommands(){
		try(var resp = get(GUILD_COMMAND_DELETE.compile(String.valueOf(Config.BOT_ID), String.valueOf(DEV_GUILD_ID))).execute()){
			var body = resp.body();
			if(body == null){
				return;
			}
			var strBody = body.string();
			var json = DataArray.fromJson(strBody);
			for(var i = 0; i < json.length(); i++){
				var cmd = json.getObject(i);
				deleteCommand(DEV_GUILD_ID, cmd.getLong("id"));
			}
			LOG.debug("Loaded following commands: {}", strBody);
		}
		catch(IOException e){
			LOG.error("Error while clearing commands", e);
		}
	}

	private Call get(Route.CompiledRoute route){
		return this.modules.getHttpClient().newCall(newBuilder(route).get().build());
	}

	public void deleteCommand(long guildId, long commandId){
		LOG.debug("Registering command: {}", commandId);
		try(var resp = delete(GUILD_COMMAND_DELETE.compile(String.valueOf(Config.BOT_ID), String.valueOf(guildId), String.valueOf(commandId))).execute()){
			if(!resp.isSuccessful()){
				var body = resp.body();
				LOG.error("Error while deleting command: {}", body == null ? "null" : body.string());
			}
		}
		catch(IOException e){
			LOG.error("Error while deleting command", e);
		}
	}

	private Call delete(Route.CompiledRoute route){
		return this.modules.getHttpClient().newCall(newBuilder(route).delete().build());
	}

	public List<Command> getCommands(){
		return this.commands;
	}

}
