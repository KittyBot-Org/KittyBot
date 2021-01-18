package de.kittybot.kittybot.commands.music;

import de.kittybot.kittybot.slashcommands.application.Category;
import de.kittybot.kittybot.slashcommands.application.Command;
import de.kittybot.kittybot.slashcommands.application.CommandOptionChoice;
import de.kittybot.kittybot.slashcommands.application.RunnableCommand;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionString;
import de.kittybot.kittybot.slashcommands.context.CommandContext;
import de.kittybot.kittybot.slashcommands.context.Options;
import de.kittybot.kittybot.utils.annotations.Ignore;

@SuppressWarnings("unused")
@Ignore
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
