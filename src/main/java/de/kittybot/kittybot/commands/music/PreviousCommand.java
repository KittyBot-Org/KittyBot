package de.kittybot.kittybot.commands.music;

import de.kittybot.kittybot.modules.MusicModule;
import de.kittybot.kittybot.slashcommands.GuildCommandContext;
import de.kittybot.kittybot.slashcommands.Options;
import de.kittybot.kittybot.slashcommands.application.Category;
import de.kittybot.kittybot.slashcommands.application.RunGuildCommand;
import de.kittybot.kittybot.utils.MusicUtils;

@SuppressWarnings("unused")
public class PreviousCommand extends RunGuildCommand{

	public PreviousCommand(){
		super("previous", "Plays the previous song", Category.MUSIC);
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
		if(scheduler.getHistory().isEmpty()){
			ctx.error("Can't go back because the history is empty");
			return;
		}
		ctx.reply("Went back to the previous song");
		scheduler.previous();
		scheduler.setPaused(false);
	}

}
