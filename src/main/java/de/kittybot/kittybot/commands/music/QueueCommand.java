package de.kittybot.kittybot.commands.music;

import de.kittybot.kittybot.modules.MusicModule;
import de.kittybot.kittybot.objects.music.MusicPlayer;
import de.kittybot.kittybot.objects.music.SearchProvider;
import de.kittybot.kittybot.slashcommands.application.Category;
import de.kittybot.kittybot.slashcommands.application.Command;
import de.kittybot.kittybot.slashcommands.application.CommandOptionChoice;
import de.kittybot.kittybot.slashcommands.application.RunnableCommand;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionString;
import de.kittybot.kittybot.slashcommands.context.CommandContext;
import de.kittybot.kittybot.slashcommands.context.Options;
import de.kittybot.kittybot.utils.Colors;
import de.kittybot.kittybot.utils.MessageUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;

@SuppressWarnings("unused")
public class QueueCommand extends Command implements RunnableCommand{

	public QueueCommand(){
		super("queue", "Queues a link or search result from yt/sc", Category.MUSIC);
		addOptions(
			new CommandOptionString("query", "A link or search query to play from"),
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

		if(options.has("query")){
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
			ctx.reply(new EmbedBuilder()
				.setColor(Colors.KITTYBOT_BLUE)
				.setDescription("The queue is empty. You can queue new tracks with `/play <link/search-term>` or `/queue <link/search-term>`")
			);
			return;
		}
		MusicPlayer finalPlayer = player;
		ctx.acknowledge(true).queue(success ->
			finalPlayer.sendTracks(tracks, ctx.getUserId(), "Currently " + tracks.size() + " " + MessageUtils.pluralize("track", tracks) + " are queued")
		);

	}

}
