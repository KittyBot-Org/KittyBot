package de.kittybot.kittybot.commands.music;

import de.kittybot.kittybot.modules.MusicModule;
import de.kittybot.kittybot.slashcommands.application.Category;
import de.kittybot.kittybot.slashcommands.application.RunGuildCommand;
import de.kittybot.kittybot.slashcommands.interaction.GuildInteraction;
import de.kittybot.kittybot.slashcommands.interaction.Options;
import de.kittybot.kittybot.utils.MusicUtils;

@SuppressWarnings("unused")
public class SkipCommand extends RunGuildCommand{

	public SkipCommand(){
		super("skip", "Skips the current song", Category.MUSIC);
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
		ia.reply("Skipped to the next song");
		scheduler.next(true);
		scheduler.setPaused(false);
	}

}
