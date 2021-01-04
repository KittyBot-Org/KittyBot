package de.kittybot.kittybot.commands.music;

import de.kittybot.kittybot.command.Args;
import de.kittybot.kittybot.command.Category;
import de.kittybot.kittybot.command.Command;
import de.kittybot.kittybot.command.CommandContext;
import de.kittybot.kittybot.utils.Colors;
import de.kittybot.kittybot.utils.MessageUtils;
import de.kittybot.kittybot.utils.MusicUtils;
import net.dv8tion.jda.api.EmbedBuilder;

import java.util.stream.Collectors;

public class QueueCommand extends Command{

	public QueueCommand(){
		super("queue", "Used to queue music", Category.MUSIC);
		addAliases("q");
		setUsage("<link/search term>");
	}

	@Override
	public void run(Args args, CommandContext ctx){
		if(args.isEmpty()){
			var player = ctx.getMusicManager().get(ctx.getGuildId());
			if(player == null){
				ctx.sendError("Please play something before requesting the queue");
				return;
			}
			var tracks = player.getQueue();
			if(tracks.size() == 0){
				var prefix = ctx.getGuildSettingsManager().getPrefix(ctx.getGuildId());
				ctx.sendAnswer(new EmbedBuilder()
						.setColor(Colors.KITTYBOT_BLUE)
						.setDescription("The queue is empty. You can queue new tracks with `" + prefix + "p <link/search-term>` or `" + prefix + "q <link/search-term>`")
				);
				return;
			}
			ctx.sendAnswer(new EmbedBuilder()
					.setColor(Colors.KITTYBOT_BLUE)
					.setDescription(MusicUtils.formatTracks("Currently " + tracks.size() + " " + MessageUtils.pluralize("track", tracks) + " is queued:", tracks))
			);
			return;
		}
		if(!MusicUtils.checkVoiceRequirements(ctx)){
			return;
		}
		ctx.getMusicManager().create(ctx).loadItem(ctx);
	}

}
