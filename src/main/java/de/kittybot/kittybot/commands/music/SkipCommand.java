package de.kittybot.kittybot.commands.music;

import de.kittybot.kittybot.command.Args;
import de.kittybot.kittybot.command.Category;
import de.kittybot.kittybot.command.Command;
import de.kittybot.kittybot.command.context.CommandContext;
import de.kittybot.kittybot.modules.MusicModule;
import de.kittybot.kittybot.utils.MusicUtils;

@SuppressWarnings("unused")
public class SkipCommand extends Command{

	public SkipCommand(){
		super("skip", "Used to skip to the next song", Category.MUSIC);
		addAliases("next");
	}

	@Override
	public void run(Args args, CommandContext ctx){
		var player = ctx.get(MusicModule.class).get(ctx.getGuildId());
		if(!MusicUtils.checkCommandRequirements(ctx, player)){
			return;
		}
		if(!MusicUtils.checkMusicPermissions(ctx, player)){
			return;
		}
		if(player.getQueue().isEmpty()){
			ctx.sendError("The queue is empty. Nothing to skip to");
			return;
		}
		player.next();
		player.setPaused(false);
		ctx.sendSuccess("Skipped to the next song");
	}

}
