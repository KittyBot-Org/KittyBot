package de.kittybot.kittybot.commands.music;

import de.kittybot.kittybot.modules.MusicModule;
import de.kittybot.kittybot.slashcommands.application.Category;
import de.kittybot.kittybot.slashcommands.application.RunGuildCommand;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionBoolean;
import de.kittybot.kittybot.slashcommands.interaction.GuildInteraction;
import de.kittybot.kittybot.slashcommands.interaction.Options;
import de.kittybot.kittybot.utils.MusicUtils;

@SuppressWarnings("unused")
public class PauseCommand extends RunGuildCommand{

	public PauseCommand(){
		super("pause", "Pauses/Unpauses the current track", Category.MUSIC);
		addOptions(
			new CommandOptionBoolean("pause", "If it should pause or resume. Omit for toggle")
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
		boolean pause;
		if(options.has("pause")){
			pause = options.getBoolean("pause");
		}
		else{
			pause = !scheduler.isPaused();
		}
		scheduler.setPaused(pause);
		ia.reply("Toggled pause");
	}

}
