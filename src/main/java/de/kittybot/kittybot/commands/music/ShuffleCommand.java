package de.kittybot.kittybot.commands.music;

import de.kittybot.kittybot.modules.MusicModule;
import de.kittybot.kittybot.modules.SettingsModule;
import de.kittybot.kittybot.slashcommands.GuildCommandContext;
import de.kittybot.kittybot.slashcommands.Options;
import de.kittybot.kittybot.slashcommands.application.Category;
import de.kittybot.kittybot.slashcommands.application.RunGuildCommand;
import de.kittybot.kittybot.utils.MusicUtils;

@SuppressWarnings("unused")
public class ShuffleCommand extends RunGuildCommand{

	public ShuffleCommand(){
		super("shuffle", "Shuffles all queued tracks", Category.MUSIC);
	}

	@Override
	public void run(Options options, GuildCommandContext ctx){
		var scheduler = ctx.get(MusicModule.class).getScheduler(ctx.getGuildId());
		if(!MusicUtils.checkCommandRequirements(ctx, scheduler)){
			return;
		}
		if(!ctx.get(SettingsModule.class).hasDJRole(ctx.getMember())){
			ctx.error("Only DJs are allowed shuffle");
			return;
		}
		if(scheduler.shuffle()){
			ctx.reply("Queue shuffled");
			return;
		}
		ctx.error("Queue is empty. Nothing to shuffle");
	}

}
