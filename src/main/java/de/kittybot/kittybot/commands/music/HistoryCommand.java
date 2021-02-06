package de.kittybot.kittybot.commands.music;

import de.kittybot.kittybot.modules.MusicModule;
import de.kittybot.kittybot.slashcommands.application.Category;
import de.kittybot.kittybot.slashcommands.application.Command;
import de.kittybot.kittybot.slashcommands.application.RunnableCommand;
import de.kittybot.kittybot.slashcommands.context.CommandContext;
import de.kittybot.kittybot.slashcommands.context.Options;
import de.kittybot.kittybot.utils.Colors;
import de.kittybot.kittybot.utils.MessageUtils;
import de.kittybot.kittybot.utils.MusicUtils;
import net.dv8tion.jda.api.EmbedBuilder;

@SuppressWarnings("unused")
public class HistoryCommand extends Command implements RunnableCommand{

	public HistoryCommand(){
		super("history", "Displays the last played tracks", Category.MUSIC);
	}

	@Override
	public void run(Options options, CommandContext ctx){
		var scheduler = ctx.get(MusicModule.class).getScheduler(ctx.getGuildId());
		if(!MusicUtils.checkCommandRequirements(ctx, scheduler)){
			return;
		}
		var tracks = scheduler.getHistory();
		if(tracks.isEmpty()){
			ctx.reply(new EmbedBuilder()
				.setColor(Colors.KITTYBOT_BLUE)
				.setDescription("The history is empty. Play some tracks to fill it-")
			);
			return;
		}
		MusicUtils.sendTracks(tracks, ctx.getModules(), ctx.getChannel(), ctx.getUserId(), "Currently " + tracks.size() + " " + MessageUtils.pluralize("track", tracks) + " are in the history");
	}

}
