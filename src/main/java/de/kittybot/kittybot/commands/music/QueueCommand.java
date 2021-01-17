package de.kittybot.kittybot.commands.music;

import de.kittybot.kittybot.slashcommands.application.Category;
import de.kittybot.kittybot.slashcommands.application.Command;
import de.kittybot.kittybot.slashcommands.application.CommandOptionChoice;
import de.kittybot.kittybot.slashcommands.application.RunnableCommand;
import de.kittybot.kittybot.slashcommands.context.CommandContext;
import de.kittybot.kittybot.slashcommands.context.Options;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionString;
import de.kittybot.kittybot.modules.MusicModule;
import de.kittybot.kittybot.modules.SettingsModule;
import de.kittybot.kittybot.objects.SearchProvider;
import de.kittybot.kittybot.utils.Colors;
import de.kittybot.kittybot.utils.MessageUtils;
import net.dv8tion.jda.api.EmbedBuilder;

@SuppressWarnings("unused")
public class QueueCommand extends Command implements RunnableCommand{

	public QueueCommand(){
		super("queue", "Queues a link or search result from yt/sc", Category.MUSIC);
		addOptions(
				new CommandOptionString("link", "A link to play from"),
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

		if(options.has("link")){
			if(player == null){
				player = ctx.get(MusicModule.class).create(ctx);
			}
			var searchProvider = SearchProvider.YOUTUBE;
			if(options.has("search-provider")){
				searchProvider = SearchProvider.getByShortname(options.getString("search-provider"));
			}
			player.loadItem(ctx, options.getString("link"), searchProvider);
			return;
		}
		var tracks = player.getQueue();
		if(tracks.isEmpty()){
			var prefix = ctx.get(SettingsModule.class).getPrefix(ctx.getGuildId());
			ctx.reply(new EmbedBuilder()
					.setColor(Colors.KITTYBOT_BLUE)
					.setDescription("The queue is empty. You can queue new tracks with `/play <link/search-term>` or `/queue <link/search-term>`")
			);
			return;
		}
		ctx.acknowledge(true);
		player.sendTracks(tracks, ctx.getUserId(), "Currently " + tracks.size() + " " + MessageUtils.pluralize("track", tracks) + " are queued");

	}

}
