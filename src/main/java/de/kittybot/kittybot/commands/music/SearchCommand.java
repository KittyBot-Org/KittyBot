package de.kittybot.kittybot.commands.music;

import de.kittybot.kittybot.slashcommands.application.Category;
import de.kittybot.kittybot.slashcommands.application.CommandOptionChoice;
import de.kittybot.kittybot.slashcommands.application.RunGuildCommand;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionString;
import de.kittybot.kittybot.slashcommands.interaction.GuildInteraction;
import de.kittybot.kittybot.slashcommands.interaction.Options;
import de.kittybot.kittybot.utils.annotations.Ignore;

@SuppressWarnings("unused")
@Ignore
public class SearchCommand extends RunGuildCommand{

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
	public void run(Options options, GuildInteraction ia){
		// TODO implement
		ia.error("not implemented yet");
	}

}
