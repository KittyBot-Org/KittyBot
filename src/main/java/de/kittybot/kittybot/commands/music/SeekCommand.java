package de.kittybot.kittybot.commands.music;

import de.kittybot.kittybot.modules.MusicModule;
import de.kittybot.kittybot.slashcommands.GuildCommandContext;
import de.kittybot.kittybot.slashcommands.Options;
import de.kittybot.kittybot.slashcommands.application.Category;
import de.kittybot.kittybot.slashcommands.application.RunGuildCommand;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionInteger;
import de.kittybot.kittybot.utils.MusicUtils;
import de.kittybot.kittybot.utils.TimeUtils;

@SuppressWarnings("unused")
public class SeekCommand extends RunGuildCommand{

	public SeekCommand(){
		super("seek", "Seeks the current song to given amount of seconds", Category.MUSIC);
		addOptions(
			new CommandOptionInteger("seconds", "Seconds to seek to")
		);
	}

	@Override
	public void run(Options options, GuildCommandContext ctx){
		var scheduler = ctx.get(MusicModule.class).getScheduler(ctx.getGuildId());
		if(!MusicUtils.checkCommandRequirements(ctx, scheduler)){
			return;
		}
		if(!MusicUtils.checkMusicPermissions(ctx, scheduler)){
			return;
		}
		var newPos = options.getLong("seconds") * 1000;
		var lavalinkPlayer = scheduler.getPlayer();
		if(newPos > scheduler.getPlayingTrack().getDuration()){
			scheduler.next(true);
			ctx.reply("Skipped to next track");
			return;
		}
		lavalinkPlayer.seekTo(newPos);
		ctx.reply("Sought to `" + TimeUtils.formatDuration(newPos) + "`");
	}

}
