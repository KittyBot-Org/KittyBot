package de.kittybot.kittybot.commands.music;

import de.kittybot.kittybot.modules.MusicModule;
import de.kittybot.kittybot.slashcommands.application.Category;
import de.kittybot.kittybot.slashcommands.application.RunGuildCommand;
import de.kittybot.kittybot.slashcommands.interaction.GuildInteraction;
import de.kittybot.kittybot.slashcommands.interaction.Options;
import de.kittybot.kittybot.utils.MusicUtils;

@SuppressWarnings("unused")
public class StopCommand extends RunGuildCommand{

	public StopCommand(){
		super("stop", "Stops playing music", Category.MUSIC);
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
		ia.acknowledge(true).queue(success -> ia.get(MusicModule.class).destroy(ia.getGuildId(), ia.getUserId()));
	}

}
