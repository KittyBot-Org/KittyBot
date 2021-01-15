package de.kittybot.kittybot.command.interactions.commands;

import de.kittybot.kittybot.command.interactions.application.ApplicationCommand;
import de.kittybot.kittybot.command.interactions.application.ApplicationCommandOption;
import de.kittybot.kittybot.command.interactions.application.ApplicationCommandOptionType;
import de.kittybot.kittybot.command.interactions.interaction.Interaction;
import de.kittybot.kittybot.command.interactions.response.InteractionResponseData;
import de.kittybot.kittybot.command.interactions.response.InteractionResponseType;

public class TestCommand extends ApplicationCommand{

	public TestCommand(){
		super("test", "Test Description");
		addOptions(
				new TestSubCommand()
		);
	}

	public static class TestSubCommand extends ApplicationCommandOption{

		public TestSubCommand(){
			super(ApplicationCommandOptionType.USER, "user", "description", true, true);
		}

		@Override
		public void run(Interaction interaction){
			interaction.respond(InteractionResponseType.CHANNEL_MESSAGE_WITH_SOURCE,
					new InteractionResponseData.Builder()
							.setEphemeral()
							.setContent("YAY")
							.build()
			);
		}

	}

}
