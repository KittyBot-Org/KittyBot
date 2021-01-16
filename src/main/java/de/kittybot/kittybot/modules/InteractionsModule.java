package de.kittybot.kittybot.modules;

import de.kittybot.kittybot.command.application.Command;
import de.kittybot.kittybot.command.application.CommandOptionsHolder;
import de.kittybot.kittybot.command.application.RunnableCommand;
import de.kittybot.kittybot.command.context.CommandContext;
import de.kittybot.kittybot.command.interaction.Interaction;
import de.kittybot.kittybot.command.interaction.InteractionOptionsHolder;
import de.kittybot.kittybot.command.interaction.Options;
import de.kittybot.kittybot.command.response.Response;
import de.kittybot.kittybot.command.response.ResponseType;
import de.kittybot.kittybot.module.Module;
import de.kittybot.kittybot.utils.Config;
import de.kittybot.kittybot.utils.exporters.Metrics;
import io.github.classgraph.ClassGraph;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.RawGatewayEvent;
import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.api.utils.data.DataObject;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class InteractionsModule extends Module{

	private static final Logger LOG = LoggerFactory.getLogger(InteractionsModule.class);
	private static final String COMMANDS_URL = "https://discord.com/api/v8/applications/%d/guilds/%d/commands";
	private static final String COMMAND_URL = "https://discord.com/api/v8/applications/%d/guilds/%d/commands/%d";
	private static final String INTERACTION_RESPONSE_URL = "https://discord.com/api/v8/interactions/%d/%s/callback";
	private static final String COMMANDS_PACKAGE = "de.kittybot.kittybot.commands";

	private List<Command> commands;

	@Override
	public void onEnable(){
		//deleteAllCommands();
		loadCommands();
		registerCommands();
	}

	@Override
	public void onRawGateway(@NotNull RawGatewayEvent event){
		if(event.getType().equals("INTERACTION_CREATE")){
			LOG.info("interaction: {}", event.getPayload());
			var interaction = Interaction.fromJSON(this.modules, event.getPayload());
			process(interaction);
		}
	}

	public void process(Interaction interaction){
		var start = System.currentTimeMillis();
		var settings = this.modules.get(SettingsModule.class).getSettings(interaction.getGuild().getIdLong());

		if(settings.isBotIgnoredUser(interaction.getMember().getIdLong())){
			reply(interaction, new Response.Builder().setType(ResponseType.ACKNOWLEDGE).setContent("I ignore u baka").ephemeral().build());
			return;
		}

		if(settings.isBotDisabledInChannel(interaction.getChannelId())){
			reply(interaction, new Response.Builder().setType(ResponseType.ACKNOWLEDGE).setContent("I'm disabled in this channel").ephemeral().build());
			return;
		}

		var data = interaction.getData();
		for(var cmd : this.commands){
			if(cmd.getName().equalsIgnoreCase(data.getName())){
				processOptions(cmd, interaction, interaction.getData());

				Metrics.COMMAND_LATENCY.observe((double) (System.currentTimeMillis() - start));
				Metrics.COMMAND_COUNTER.labels(cmd.getName()).inc();
				return;
			}
		}
	}

	public void processOptions(CommandOptionsHolder applicationHolder, Interaction interaction, InteractionOptionsHolder holder) {
		if(applicationHolder instanceof Command){
			var cmd = ((Command) applicationHolder);
			var member = interaction.getMember();
			if(cmd.isDevOnly() && !Config.DEV_IDS.contains(member.getIdLong())){
				reply(interaction, new Response.Builder().setType(ResponseType.ACKNOWLEDGE).setContent("This command is developer only").ephemeral().build());
				return;
			}

			var missingPerms = cmd.getPermissions().stream().dropWhile(member.getPermissions()::contains).collect(Collectors.toSet());
			if(!missingPerms.isEmpty()){
				reply(interaction, new Response.Builder().setType(ResponseType.ACKNOWLEDGE).setContent("You are missing following permissions to use this command:\n" +
						missingPerms.stream().map(Permission::getName).collect(Collectors.joining(", "))
				).ephemeral().build());
				return;
			}
		}

		if(applicationHolder instanceof RunnableCommand){
			((RunnableCommand) applicationHolder).run(new Options(holder.getOptions()), new CommandContext(interaction, this.modules));
			return;
		}

		for(var option : applicationHolder.getOptions()){
			var opt = holder.getOptions().get(0);
			if(opt.getName().equalsIgnoreCase(option.getName())){
				processOptions(option, interaction, opt);
				return;
			}
		}
	}

	public void reply(Interaction interaction, Response response){
		var rqBody = RequestBody.create(response.toJSON().toJson(), MediaType.parse("application/json"));

		try(var resp = post(INTERACTION_RESPONSE_URL, rqBody, interaction.getId(), interaction.getToken()).execute()){
			var body = resp.body();
			LOG.info("Response Body: {}", body == null ? "null" : body.string());
		}
		catch(IOException e){
			LOG.error("Error while processing interaction response", e);
		}
	}

	public void loadCommands(){
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

	public void deleteAllCommands(){
		var guildId = 730879265956167740L;
		try(var resp = get(COMMANDS_URL, Config.BOT_ID, guildId).execute()){
			var body = resp.body();
			if(body == null){
				return;
			}
			var strBody = body.string();
			var json = DataArray.fromJson(strBody);
			for(var i = 0; i < json.length(); i++){
				var cmd = json.getObject(i);
				deleteCommand(guildId, cmd.getLong("id"));
			}
			LOG.info("Loaded following commands: {}", strBody);
		}
		catch(IOException e){
			LOG.error("Error while clearing commands", e);
		}
	}

	public void deleteCommand(long guildId, long commandId){
		try(var resp = delete(COMMAND_URL, Config.BOT_ID, guildId, commandId).execute()){
			if(!resp.isSuccessful()){
				var body = resp.body();
				LOG.error("Error while deleting command: {}", body == null ? "null" : body.string());
			}
		}
		catch(IOException e){
			LOG.error("Error while deleting command", e);
		}
	}

	public void registerCommands(){
		for(var cmd : commands){
			LOG.info("Registering command: {}", cmd.toJSON());
			var rqBody = RequestBody.create(cmd.toJSON().toString(), MediaType.parse("application/json"));
			try(var resp = post(COMMANDS_URL, rqBody, Config.BOT_ID, 730879265956167740L).execute()){
				if(!resp.isSuccessful()){
					var body = resp.body();
					LOG.error("Request failed. Body: {}", body == null ? "null" : body.string());
					continue;
				}
				LOG.info("Registered command with name: {}", cmd.getName());
			}
			catch(IOException e){
				LOG.error("Error while processing registerCommands", e);
			}
		}
	}

	private DataArray parseOptions(de.kittybot.kittybot.command.old.Command command){
		return DataArray.fromCollection(command.getChildren().stream().map(
				cmd -> DataObject.empty().put("type", 1).put("name", cmd.getName()).put("description", cmd.getDescription()).put("options", parseOptions(cmd))
		).collect(Collectors.toSet()));
	}

	private Call post(String url, RequestBody body, Object... params){
		return this.modules.getHttpClient().newCall(newBuilder(url, params).post(body).build());
	}

	private Call get(String url, Object... params){
		return this.modules.getHttpClient().newCall(newBuilder(url, params).get().build());
	}

	private Call delete(String url, Object... params){
		return this.modules.getHttpClient().newCall(newBuilder(url, params).delete().build());
	}

	private Request.Builder newBuilder(String url, Object... params){
		return new Request.Builder()
				.url(String.format(url, params))
				.addHeader("Authorization", "Bot " + Config.BOT_TOKEN);
	}

	public List<Command> getCommands(){
		return this.commands;
	}

}
