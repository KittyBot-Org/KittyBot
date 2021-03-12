package de.kittybot.kittybot.commands.music;

import de.kittybot.kittybot.modules.MusicModule;
import de.kittybot.kittybot.slashcommands.GuildCommandContext;
import de.kittybot.kittybot.slashcommands.Options;
import de.kittybot.kittybot.slashcommands.application.Category;
import de.kittybot.kittybot.slashcommands.application.RunGuildCommand;
import de.kittybot.kittybot.utils.MusicUtils;

@SuppressWarnings("unused")
public class SkipCommand extends RunGuildCommand{

	public SkipCommand(){
		super("skip", "Skips the current song", Category.MUSIC);
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
		ctx.reply("Skipped to the next song");
		scheduler.next(true);
		scheduler.setPaused(false);
	}

}
