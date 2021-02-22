package de.kittybot.kittybot.commands.music;

import de.kittybot.kittybot.modules.MusicModule;
import de.kittybot.kittybot.slashcommands.application.Category;
import de.kittybot.kittybot.slashcommands.application.RunGuildCommand;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionInteger;
import de.kittybot.kittybot.slashcommands.interaction.GuildInteraction;
import de.kittybot.kittybot.slashcommands.interaction.Options;
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
	public void run(Options options, GuildInteraction ia){
		var scheduler = ia.get(MusicModule.class).getScheduler(ia.getGuildId());
		if(!MusicUtils.checkCommandRequirements(ia, scheduler)){
			return;
		}
		if(!MusicUtils.checkMusicPermissions(ia, scheduler)){
			return;
		}
		var newPos = options.getLong("seconds") * 1000;
		var lavalinkPlayer = scheduler.getPlayer();
		if(newPos > scheduler.getPlayingTrack().getDuration()){
			scheduler.next(true);
			ia.reply("Skipped to next track");
			return;
		}
		lavalinkPlayer.seekTo(newPos);
		ia.reply("Sought to `" + TimeUtils.formatDuration(newPos) + "`");
	}

}
