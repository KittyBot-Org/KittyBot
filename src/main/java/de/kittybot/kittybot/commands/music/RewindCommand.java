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

@SuppressWarnings("unused")
public class RewindCommand extends Command implements RunnableCommand{

	public RewindCommand(){
		super("rewind", "Rewinds the current song by given amount of seconds", Category.MUSIC);
		addOptions(
			new CommandOptionInteger("seconds", "Seconds to rewind").required()
		);
	}

	@Override
	public void run(Options options, CommandContext ctx){
		var scheduler = ctx.get(MusicModule.class).getScheduler(ctx.getGuildId());
		if(!MusicUtils.checkCommandRequirements(ctx, scheduler)){
			return;
		}
		if(!MusicUtils.checkMusicPermissions(ctx, scheduler)){
			return;
		}
		var rewind = options.getInt("seconds") * 1000;
		var lavalinkPlayer = scheduler.getPlayer();
		var position = lavalinkPlayer.getTrackPosition();
		var newPos = position - rewind;
		if(newPos <= 0){
			newPos = 0;
		}
		lavalinkPlayer.seekTo(newPos);
		ctx.reply("Rewinded track to `" + TimeUtils.formatDuration(newPos) + "`");
	}

}
