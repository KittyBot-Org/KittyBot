package de.kittybot.kittybot.commands.music;

import de.kittybot.kittybot.modules.MusicModule;
import de.kittybot.kittybot.objects.music.SearchProvider;
import de.kittybot.kittybot.slashcommands.application.Category;
import de.kittybot.kittybot.slashcommands.application.Command;
import de.kittybot.kittybot.slashcommands.application.CommandOptionChoice;
import de.kittybot.kittybot.slashcommands.application.RunnableCommand;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionString;
import de.kittybot.kittybot.slashcommands.context.CommandContext;
import de.kittybot.kittybot.slashcommands.context.Options;
import net.dv8tion.jda.api.Permission;

@SuppressWarnings("unused")
public class PlayCommand extends Command implements RunnableCommand{

	public PlayCommand(){
		super("play", "Plays a link or searches on yt/sc", Category.MUSIC);
		addOptions(
			new CommandOptionString("query", "A link or search query to play from").required(),
			new CommandOptionString("search-provider", "Which search provider use")
				.addChoices(
					new CommandOptionChoice<>("youtube", "yt"),
					new CommandOptionChoice<>("soundcloud", "sc")
				)
		);
	}

	@Override
	public void run(Options options, CommandContext ctx){
		if(!ctx.getGuild().getSelfMember().hasPermission(ctx.getChannel(), Permission.MESSAGE_WRITE, Permission.MESSAGE_ADD_REACTION, Permission.MESSAGE_EXT_EMOJI, Permission.MESSAGE_HISTORY, Permission.VIEW_CHANNEL)){
			ctx.error("Please make sure I have following permissions in this channel: `Send Messages`, `Add Reactions`, `Use External Emoji`, `Read Message History`, `View Channel`");
			return;
		}
		var player = ctx.get(MusicModule.class).get(ctx.getGuildId());
		if(player == null){
			player = ctx.get(MusicModule.class).create(ctx);
		}
		var searchProvider = SearchProvider.YOUTUBE;
		if(options.has("search-provider")){
			searchProvider = SearchProvider.getByShortname(options.getString("search-provider"));
		}
		player.loadItem(ctx, options.getString("query"), searchProvider);
	}

}
