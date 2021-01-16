package de.kittybot.kittybot.main.commands.music;

import de.kittybot.kittybot.command.old.Args;
import de.kittybot.kittybot.command.Category;
import de.kittybot.kittybot.command.old.Command;
import de.kittybot.kittybot.command.old.CommandContext;
import de.kittybot.kittybot.modules.MusicModule;
import de.kittybot.kittybot.utils.Colors;
import de.kittybot.kittybot.utils.MessageUtils;
import de.kittybot.kittybot.utils.MusicUtils;
import net.dv8tion.jda.api.EmbedBuilder;

@SuppressWarnings("unused")
public class HistoryCommand extends Command{

	public HistoryCommand(){
		super("history", "Displays the current history played songs", Category.MUSIC);
		addAliases("hist");
		setUsage("<link/search term>");
	}

	@Override
	public void run(Args args, CommandContext ctx){
		var player = ctx.get(MusicModule.class).get(ctx.getGuildId());
		if(!MusicUtils.checkCommandRequirements(ctx, player)){
			return;
		}
		var tracks = player.getHistory();
		if(tracks.isEmpty()){
			ctx.sendAnswer(new EmbedBuilder()
					.setColor(Colors.KITTYBOT_BLUE)
					.setDescription("The history is empty. Play some tracks to fill it`")
			);
			return;
		}
		player.sendTracks(tracks, ctx.getUserId(), "Currently " + tracks.size() + " " + MessageUtils.pluralize("track", tracks) + " are in the history");
	}

}
