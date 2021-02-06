package de.kittybot.kittybot.commands.music;

import de.kittybot.kittybot.modules.MusicModule;
import de.kittybot.kittybot.slashcommands.application.Category;
import de.kittybot.kittybot.slashcommands.application.Command;
import de.kittybot.kittybot.slashcommands.application.RunnableCommand;
import de.kittybot.kittybot.slashcommands.context.CommandContext;
import de.kittybot.kittybot.slashcommands.context.Options;
import de.kittybot.kittybot.utils.MusicUtils;

@SuppressWarnings("unused")
public class StopCommand extends Command implements RunnableCommand{

	public StopCommand(){
		super("stop", "Stops playing music", Category.MUSIC);
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
		ctx.acknowledge(true).queue();
		ctx.get(MusicModule.class).destroy(ctx.getGuildId(), ctx.getUserId());
	}

}
