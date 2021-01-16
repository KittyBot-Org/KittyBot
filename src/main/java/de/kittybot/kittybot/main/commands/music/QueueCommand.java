package de.kittybot.kittybot.main.commands.music;

import de.kittybot.kittybot.command.old.Args;
import de.kittybot.kittybot.command.Category;
import de.kittybot.kittybot.command.old.Command;
import de.kittybot.kittybot.command.old.CommandContext;
import de.kittybot.kittybot.modules.MusicModule;
import de.kittybot.kittybot.modules.SettingsModule;
import de.kittybot.kittybot.utils.Colors;
import de.kittybot.kittybot.utils.MessageUtils;
import de.kittybot.kittybot.utils.MusicUtils;
import net.dv8tion.jda.api.EmbedBuilder;

@SuppressWarnings("unused")
public class QueueCommand extends Command{

	public QueueCommand(){
		super("queue", "Used to queue music", Category.MUSIC);
		addAliases("q");
		setUsage("<link/search term>");
	}

	@Override
	public void run(Args args, CommandContext ctx){
		var player = ctx.get(MusicModule.class).get(ctx.getGuildId());
		if(player == null){
			player = ctx.get(MusicModule.class).create(ctx);
		}
		if(!MusicUtils.checkCommandRequirements(ctx, player)){
			return;
		}
		if(!args.isEmpty()){
			player.loadItem(ctx);
			return;
		}
		var tracks = player.getQueue();
		if(tracks.isEmpty()){
			var prefix = ctx.get(SettingsModule.class).getPrefix(ctx.getGuildId());
			ctx.sendAnswer(new EmbedBuilder()
					.setColor(Colors.KITTYBOT_BLUE)
					.setDescription("The queue is empty. You can queue new tracks with `" + prefix + "p <link/search-term>` or `" + prefix + "q <link/search-term>`")
			);
			return;
		}
		player.sendTracks(tracks, ctx.getUserId(), "Currently " + tracks.size() + " " + MessageUtils.pluralize("track", tracks) + " are queued");
	}

}
