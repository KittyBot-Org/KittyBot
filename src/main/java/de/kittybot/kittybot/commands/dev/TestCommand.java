package de.kittybot.kittybot.commands.dev;

import de.kittybot.kittybot.slashcommands.application.Category;
import de.kittybot.kittybot.slashcommands.application.Command;
import de.kittybot.kittybot.slashcommands.application.CommandOptionChoice;
import de.kittybot.kittybot.slashcommands.context.CommandContext;
import de.kittybot.kittybot.slashcommands.interaction.InteractionDataOption;
import de.kittybot.kittybot.slashcommands.context.Options;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionBoolean;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionString;
import de.kittybot.kittybot.slashcommands.application.options.SubCommand;
import de.kittybot.kittybot.slashcommands.interaction.response.InteractionResponse;
import de.kittybot.kittybot.slashcommands.interaction.response.InteractionResponseType;
import de.kittybot.kittybot.utils.Colors;
import net.dv8tion.jda.api.EmbedBuilder;

import java.time.Instant;
import java.util.stream.Collectors;

public class TestCommand extends Command{

	public TestCommand(){
		super("test", "Test Description", Category.DEV);
		addOptions(
				new TestSubCommand()
		);
	}

	public static class TestSubCommand extends SubCommand{

		public TestSubCommand(){
			super("response", "Let's you choose the response type");
			addOptions(
					new CommandOptionString("type", "The response type you want").required().addChoices(
							new CommandOptionChoice<>("ACKNOWLEDGE", "acknowledge"),
							new CommandOptionChoice<>("ACKNOWLEDGE_WITH_SOURCE", "acknowledge_with_source"),
							new CommandOptionChoice<>("CHANNEL_MESSAGE", "channel_message"),
							new CommandOptionChoice<>("CHANNEL_MESSAGE_WITH_SOURCE", "channel_message_with_source")
					),
					new CommandOptionBoolean("ephemeral", "Weather the response should be a ephemeral message")
			);
		}

		@Override
		public void run(Options options, CommandContext ctx){
			var member = ctx.getMember();
			var content = options.stream().map(InteractionDataOption::getValue).map(Object::toString).collect(Collectors.joining(", "));
			var response = new InteractionResponse.Builder();
			switch(options.getString("type")){
				case "acknowledge":
					response.setType(InteractionResponseType.ACKNOWLEDGE);
					break;
				case "acknowledge_with_source":
					response.setType(InteractionResponseType.ACKNOWLEDGE_WITH_SOURCE);
					break;
				case "channel_message":
					response.setType(InteractionResponseType.CHANNEL_MESSAGE);
					break;
				case "channel_message_with_source":
					response.setType(InteractionResponseType.CHANNEL_MESSAGE_WITH_SOURCE);
					break;
			}
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