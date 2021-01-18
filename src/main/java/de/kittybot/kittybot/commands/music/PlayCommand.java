package de.kittybot.kittybot.commands.music;

import de.kittybot.kittybot.modules.MusicModule;
import de.kittybot.kittybot.objects.SearchProvider;
import de.kittybot.kittybot.slashcommands.application.Category;
import de.kittybot.kittybot.slashcommands.application.Command;
import de.kittybot.kittybot.slashcommands.application.CommandOptionChoice;
import de.kittybot.kittybot.slashcommands.application.RunnableCommand;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionString;
import de.kittybot.kittybot.slashcommands.context.CommandContext;
import de.kittybot.kittybot.slashcommands.context.Options;

@SuppressWarnings("unused")
public class PlayCommand extends Command implements RunnableCommand{

	public PlayCommand(){
		super("play", "Plays a link or searches on yt/sc", Category.MUSIC);
		addOptions(
			new CommandOptionString("link", "A link to play from").required(),
			new CommandOptionString("search-provider", "Which search provider use")
				.addChoices(
					new CommandOptionChoice<>("youtube", "yt"),
					new CommandOptionChoice<>("soundcloud", "sc")
				)
		);
	}

	@Override
	public void run(Options options, CommandContext ctx){
		var player = ctx.get(MusicModule.class).get(ctx.getGuildId());
		if(player == null){
			player = ctx.get(MusicModule.class).create(ctx);
		}
		var searchProvider = SearchProvider.YOUTUBE;
		if(options.has("search-provider")){
			searchProvider = SearchProvider.getByShortname(options.getString("search-provider"));
		}
		player.loadItem(ctx, options.getString("link"), searchProvider);
	}

}
