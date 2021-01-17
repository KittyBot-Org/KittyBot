package de.kittybot.kittybot.commands.music;

import de.kittybot.kittybot.command.Category;
import de.kittybot.kittybot.command.application.Command;
import de.kittybot.kittybot.command.application.CommandOptionChoice;
import de.kittybot.kittybot.command.application.RunnableCommand;
import de.kittybot.kittybot.command.context.CommandContext;
import de.kittybot.kittybot.command.interaction.Options;
import de.kittybot.kittybot.command.options.CommandOptionInteger;
import de.kittybot.kittybot.command.options.CommandOptionString;

@SuppressWarnings("unused")
public class SearchCommand extends Command implements RunnableCommand{

	public SearchCommand(){
		super("search", "Searched for a given search-term on yt/sc", Category.MUSIC);
		addOptions(
				new CommandOptionString("search-term", "A search-term to search for").required(),
				new CommandOptionString("search-provider", "Which search provider use")
						.addChoices(
								new CommandOptionChoice<>("youtube", "yt"),
								new CommandOptionChoice<>("soundcloud", "sc")
						)
		);
	}

	@Override
	public void run(Options options, CommandContext ctx){
		// TODO implement
		ctx.error("not implemented yet");
	}

}
