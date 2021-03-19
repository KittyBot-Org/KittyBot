package de.kittybot.kittybot.commands.music;

import de.kittybot.kittybot.modules.MusicModule;
import de.kittybot.kittybot.objects.music.SearchProvider;
import de.kittybot.kittybot.slashcommands.GuildCommandContext;
import de.kittybot.kittybot.slashcommands.Options;
import de.kittybot.kittybot.slashcommands.application.Category;
import de.kittybot.kittybot.slashcommands.application.OptionChoice;
import de.kittybot.kittybot.slashcommands.application.RunGuildCommand;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionString;
import de.kittybot.kittybot.utils.MessageUtils;
import de.kittybot.kittybot.utils.MusicUtils;
import net.dv8tion.jda.api.Permission;

@SuppressWarnings("unused")
public class QueueCommand extends RunGuildCommand{

	public QueueCommand(){
		super("queue", "Queues a link or search result from yt/sc", Category.MUSIC);
		addOptions(
			new CommandOptionString("query", "A link or search query to play from"),
			new CommandOptionString("search-provider", "Which search provider use")
				.addChoices(
					new OptionChoice(SearchProvider.YOUTUBE),
					new OptionChoice(SearchProvider.SOUNDCLOUD)/*,
					new CommandOptionChoice<>("spotify", "sp")*/
				)
		);
	}

	@Override
	public void run(Options options, GuildCommandContext ctx){
		if(!ctx.getGuild().getSelfMember().hasPermission(ctx.getChannel(), Permission.MESSAGE_WRITE, Permission.MESSAGE_ADD_REACTION, Permission.MESSAGE_EXT_EMOJI, Permission.MESSAGE_HISTORY, Permission.VIEW_CHANNEL)){
			ctx.error("Please make sure I have following permissions in this channel: `Send Messages`, `Add Reactions`, `Use External Emoji`, `Read Message History`, `View Channel`");
			return;
		}
		if(!MusicUtils.checkMusicRequirements(ctx)){
			return;
		}
		var musicModule = ctx.get(MusicModule.class);

		if(options.has("query")){
			var searchProvider = SearchProvider.YOUTUBE;
			if(options.has("search-provider")){
				searchProvider = SearchProvider.getByShortname(options.getString("search-provider"));
			}
			musicModule.play(ctx, options.getString("query"), searchProvider);
			return;
		}
		var manager = musicModule.get(ctx.getGuildId());
		var scheduler = manager.getScheduler();
		var tracks = scheduler.getQueue();
		if(tracks.isEmpty()){
			ctx.reply("The queue is empty. You can queue new tracks with `/play <query/search-term>` or `/queue <query/search-term>`");
			return;
		}
		ctx.acknowledge(true).queue(success ->
			MusicUtils.sendTracks(tracks, ctx.getModules(), ctx.getChannel(), ctx.getUserId(), "Currently " + tracks.size() + " " + MessageUtils.pluralize("track", tracks) + " are queued")
		);

	}

}
