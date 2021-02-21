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
public class RewindCommand extends RunGuildCommand{

	public RewindCommand(){
		super("rewind", "Rewinds the current song by given amount of seconds", Category.MUSIC);
		addOptions(
			new CommandOptionInteger("seconds", "Seconds to rewind").required()
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
		var rewind = options.getInt("seconds") * 1000;
		var lavalinkPlayer = scheduler.getPlayer();
		var position = lavalinkPlayer.getTrackPosition();
		var newPos = position - rewind;
		if(newPos <= 0){
			newPos = 0;
		}
		lavalinkPlayer.seekTo(newPos);
		ia.reply("Rewinded track to `" + TimeUtils.formatDuration(newPos) + "`");
	}

}
