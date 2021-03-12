package de.kittybot.kittybot.commands.music;

import de.kittybot.kittybot.modules.MusicModule;
import de.kittybot.kittybot.slashcommands.GuildCommandContext;
import de.kittybot.kittybot.slashcommands.Options;
import de.kittybot.kittybot.slashcommands.application.Category;
import de.kittybot.kittybot.slashcommands.application.RunGuildCommand;
import de.kittybot.kittybot.utils.MessageUtils;
import de.kittybot.kittybot.utils.MusicUtils;

@SuppressWarnings("unused")
public class HistoryCommand extends RunGuildCommand{

	public HistoryCommand(){
		super("history", "Displays the last played tracks", Category.MUSIC);
	}

	@Override
	public void run(Options options, GuildCommandContext ctx){
		var scheduler = ctx.get(MusicModule.class).getScheduler(ctx.getGuildId());
		if(!MusicUtils.checkCommandRequirements(ctx, scheduler)){
			return;
		}
		var tracks = scheduler.getHistory();
		if(tracks.isEmpty()){
			ctx.reply("The history is empty. Play some tracks to fill it");
			return;
		}
		MusicUtils.sendTracks(tracks, ctx.getModules(), ctx.getChannel(), ctx.getUserId(), "Currently " + tracks.size() + " " + MessageUtils.pluralize("track", tracks) + " are in the history");
	}

}
