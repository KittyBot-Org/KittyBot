package de.kittybot.kittybot.commands.dev.dev.test;

import de.kittybot.kittybot.slashcommands.application.CommandOptionChoice;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionBoolean;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionString;
import de.kittybot.kittybot.slashcommands.application.options.SubCommand;
import de.kittybot.kittybot.slashcommands.interaction.Interaction;
import de.kittybot.kittybot.slashcommands.interaction.InteractionDataOption;
import de.kittybot.kittybot.slashcommands.interaction.Options;
import de.kittybot.kittybot.slashcommands.interaction.response.InteractionResponse;
import de.kittybot.kittybot.slashcommands.interaction.response.InteractionResponseType;
import net.dv8tion.jda.api.EmbedBuilder;

import java.util.stream.Collectors;

public class ResponseCommand extends SubCommand{

	public ResponseCommand(){
		super("response", "Let's you choose the response type");
		addOptions(
			new CommandOptionString("type", "The response type you want").required().addChoices(
				new CommandOptionChoice<>(InteractionResponseType.ACKNOWLEDGE),
				new CommandOptionChoice<>(InteractionResponseType.ACKNOWLEDGE_WITH_SOURCE),
				new CommandOptionChoice<>(InteractionResponseType.CHANNEL_MESSAGE),
				new CommandOptionChoice<>(InteractionResponseType.CHANNEL_MESSAGE_WITH_SOURCE)
			),
			new CommandOptionBoolean("ephemeral", "Weather the response should be a ephemeral message")
		);
	}

	@Override
	public void run(Options options, Interaction ia){
		var content = options.stream().map(InteractionDataOption::getValue).map(Object::toString).collect(Collectors.joining(", "));
		var response = new InteractionResponse.Builder()
			.setType(InteractionResponseType.valueOf(options.getString("type")));
		if(options.has("ephemeral")){
			response.setEphemeral(options.getBoolean("ephemeral"));
		}
		if(response.isEphemeral()){
			response.setContent(content);
		}
		else{
			response.addEmbeds(ia.applyDefaultStyle(new EmbedBuilder()
					.setTitle("Response")
					.setDescription(content)
				).build()
			);
		}
		ia.reply(response.build());
	}

}
