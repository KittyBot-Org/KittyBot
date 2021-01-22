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
public class ForwardCommand extends Command implements RunnableCommand{

	public ForwardCommand(){
		super("forward", "Forwards the current song by given amount of seconds", Category.MUSIC);
		addOptions(
			new CommandOptionInteger("seconds", "Seconds to forward").required()
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
		var forward = options.getLong("seconds") * 1000;
		var lavalinkPlayer = player.getPlayer();
		var position = lavalinkPlayer.getTrackPosition();
		var newPos = position + forward;
		if(newPos > player.getPlayingTrack().getDuration()){
			player.next();
			ctx.reply("Skipped to next track");
			return;
		}
		lavalinkPlayer.seekTo(newPos);
		ctx.reply("Forwarded track to `" + TimeUtils.formatDuration(newPos) + "`");
	}

}
