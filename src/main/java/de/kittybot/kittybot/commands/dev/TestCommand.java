package de.kittybot.kittybot.commands.dev;

import de.kittybot.kittybot.slashcommands.application.CommandOptionChoice;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionBoolean;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionString;
import de.kittybot.kittybot.slashcommands.application.options.SubCommand;
import de.kittybot.kittybot.slashcommands.application.options.SubCommandGroup;
import de.kittybot.kittybot.slashcommands.context.CommandContext;
import de.kittybot.kittybot.slashcommands.context.Options;
import de.kittybot.kittybot.slashcommands.interaction.InteractionDataOption;
import de.kittybot.kittybot.slashcommands.interaction.response.InteractionResponse;
import de.kittybot.kittybot.slashcommands.interaction.response.InteractionResponseType;
import de.kittybot.kittybot.utils.Colors;
import net.dv8tion.jda.api.EmbedBuilder;

import java.time.Instant;
import java.util.stream.Collectors;

public class TestCommand extends SubCommandGroup{

	public TestCommand(){
		super("test", "Test Description");
		addOptions(
			new ResponseCommand()
		);
	}

	private static class ResponseCommand extends SubCommand{

		public ResponseCommand(){
			super("response", "Let's you choose the response type");
			addOptions(
				new CommandOptionString("type", "The response type you want").required().addChoices(
					new CommandOptionChoice<>(InteractionResponseType.ACKNOWLEDGE.name(), InteractionResponseType.ACKNOWLEDGE),
					new CommandOptionChoice<>(InteractionResponseType.ACKNOWLEDGE_WITH_SOURCE.name(), InteractionResponseType.ACKNOWLEDGE_WITH_SOURCE),
					new CommandOptionChoice<>(InteractionResponseType.CHANNEL_MESSAGE.name(), InteractionResponseType.CHANNEL_MESSAGE),
					new CommandOptionChoice<>(InteractionResponseType.CHANNEL_MESSAGE_WITH_SOURCE.name(), InteractionResponseType.CHANNEL_MESSAGE_WITH_SOURCE)
				),
				new CommandOptionBoolean("ephemeral", "Weather the response should be a ephemeral message")
			);
		}

		@Override
		public void run(Options options, CommandContext ctx){
			var member = ctx.getMember();
			var content = options.stream().map(InteractionDataOption::getValue).map(Object::toString).collect(Collectors.joining(", "));
			var response = new InteractionResponse.Builder();
			response.setType(InteractionResponseType.valueOf(options.getString("type")));
			if(options.has("ephemeral")){
				response.setEphemeral(options.getBoolean("ephemeral"));
			}
			if(response.isEphemeral()){
				response.setContent(content);
			}
			else{
				response.addEmbeds(new EmbedBuilder()
					.setTitle("Response")
					.setColor(Colors.KITTYBOT_BLUE)
					.setDescription(content)
					.setFooter(member.getEffectiveName(), member.getUser().getEffectiveAvatarUrl())
					.setTimestamp(Instant.now())
					.build()
				);
			}
			ctx.reply(response.build());
		}

	}

}