package de.kittybot.kittybot.modules;

import de.kittybot.kittybot.command.Command;
import de.kittybot.kittybot.command.interactions.application.ApplicationCommand;
import de.kittybot.kittybot.command.interactions.commands.TestCommand;
import de.kittybot.kittybot.command.interactions.interaction.Interaction;
import de.kittybot.kittybot.command.interactions.response.InteractionResponse;
import de.kittybot.kittybot.command.interactions.response.InteractionResponseData;
import de.kittybot.kittybot.command.interactions.response.InteractionResponseType;
import de.kittybot.kittybot.module.Module;
import de.kittybot.kittybot.utils.Config;
import net.dv8tion.jda.api.events.RawGatewayEvent;
import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.api.utils.data.DataObject;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class Interactions extends Module{

	private static final Logger LOG = LoggerFactory.getLogger(Interactions.class);
	private static final String REGISTER_COMMAND_URL = "https://discord.com/api/v8/applications/%d/guilds/%d/commands";
	private static final String INTERACTION_RESPONSE_URL = "https://discord.com/api/v8/interactions/%d/%s/callback";

	private List<ApplicationCommand> commands;

	@Override
	public void onEnable(){
		loadCommands();
		registerCommands();
	}

	@Override
	public void onRawGateway(@NotNull RawGatewayEvent event){
		if(event.getType().equals("APPLICATION_COMMAND_UPDATE")){
			LOG.info("APPLICATION_COMMAND_UPDATE: {}", event.getPayload());

		} else if(event.getType().equals("INTERACTION_CREATE")){
			LOG.info("interaction: {}", event.getPayload());
			var interaction = Interaction.fromJSON(this.modules, event.getPayload());
			process(interaction);
		}
	}

	public void process(Interaction interaction){
		var data = interaction.getData();
		for(var cmd : this.commands){
			if(cmd.getName().equalsIgnoreCase(data.getName())){
				cmd.getOptions().get(0).run(interaction);
			}
		}
	}

	public void respond(Interaction interaction, InteractionResponseType type, InteractionResponseData data){
		var rqBody = RequestBody.create(new InteractionResponse(type, data).toJSON().toJson(), MediaType.parse("application/json"));

		try(var resp = post(INTERACTION_RESPONSE_URL, rqBody, interaction.getId(), interaction.getToken()).execute()){
			var body = resp.body();
			LOG.info("Response Body: {}", body == null ? "null" : body.string());
		}
		catch(IOException e){
			LOG.error("Error while processing interaction response", e);
		}
	}

	public void respond(Interaction interaction, InteractionResponseType type){
		respond(interaction, type, null);
	}

	public void loadCommands(){
		this.commands = new ArrayList<>();
		Collections.addAll(commands,
				new TestCommand()
		);
	}

	public void registerCommands(){
		for(var cmd : commands){
			var rqBody = RequestBody.create(cmd.toJSON().toString(), MediaType.parse("application/json"));
			try(var resp = post(REGISTER_COMMAND_URL, rqBody, Config.BOT_ID, 608506410803658753L).execute()){
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

	private DataArray parseOptions(Command command){
		return DataArray.fromCollection(command.getChildren().stream().map(
				cmd -> DataObject.empty().put("type", 1).put("name", cmd.getName()).put("description", cmd.getDescription()).put("options", parseOptions(cmd))
		).collect(Collectors.toSet()));
	}

	private Call post(String url, RequestBody body, Object... params){
		return this.modules.getHttpClient().newCall(new Request.Builder().post(body)
				.url(String.format(url, params))
				.addHeader("Authorization", "Bot " + Config.BOT_TOKEN)
				.build()
		);
	}

}
