package de.kittybot.kittybot.commands.dev.dev.test;

import de.kittybot.kittybot.slashcommands.CommandContext;
import de.kittybot.kittybot.slashcommands.application.OptionChoice;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionBoolean;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionString;
import de.kittybot.kittybot.slashcommands.application.options.SubCommand;
import de.kittybot.kittybot.slashcommands.Options;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.requests.restaction.CommandReplyAction;

import java.util.stream.Collectors;

public class ResponseCommand extends SubCommand{

	public ResponseCommand(){
		super("response", "Let's you choose the response type");
		addOptions(
			new CommandOptionString("type", "The response type you want").required().addChoices(
				new OptionChoice(CommandReplyAction.ResponseType.CHANNEL_MESSAGE_WITH_SOURCE),
				new OptionChoice(CommandReplyAction.ResponseType.DEFERRED_CHANNEL_MESSAGE_WITH_SOURCE)
			),
			new CommandOptionBoolean("ephemeral", "Weather the response should be a ephemeral message")
		);
	}

	@Override
	public void run(Options options, CommandContext ctx){
		var content = options.stream().map(SlashCommandEvent.OptionData::getAsString).collect(Collectors.joining(", "));
		ctx.getEvent().reply(content).setEphemeral(options.getBoolean("ephemeral", false)).queue();
	}

}
