package de.kittybot.kittybot.commands.music;

import de.kittybot.kittybot.modules.MusicModule;
import de.kittybot.kittybot.slashcommands.application.Category;
import de.kittybot.kittybot.slashcommands.application.Command;
import de.kittybot.kittybot.slashcommands.application.RunnableCommand;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionInteger;
import de.kittybot.kittybot.slashcommands.context.CommandContext;
import de.kittybot.kittybot.slashcommands.context.Options;
import de.kittybot.kittybot.utils.MusicUtils;
import de.kittybot.kittybot.utils.TimeUtils;
import de.kittybot.kittybot.utils.annotations.Ignore;

@SuppressWarnings("unused")
public class SeekCommand extends Command implements RunnableCommand{

	public SeekCommand(){
		super("seek", "Seeks the current song to given amount of seconds", Category.MUSIC);
		addOptions(
			new CommandOptionInteger("seconds", "Seconds to seek to")
		);
	}

	@Override
	public void run(Options options, CommandContext ctx){
		var player = ctx.get(MusicModule.class).get(ctx.getGuildId());
		if(!MusicUtils.checkCommandRequirements(ctx, player)){
			return;
		}
		if(!MusicUtils.checkMusicPermissions(ctx, player)){
			return;
		}
		var newPos = options.getLong("seconds") * 1000;
		var lavalinkPlayer = player.getPlayer();
		if(newPos > player.getPlayingTrack().getDuration()){
			player.next();
			ctx.reply("Skipped to next track");
			return;
		}
		lavalinkPlayer.seekTo(newPos);
		ctx.reply("Sought to `" + TimeUtils.formatDuration(newPos) + "`");
	}

}
