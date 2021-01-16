package de.kittybot.kittybot.main.commands.music;

import de.kittybot.kittybot.command.old.Args;
import de.kittybot.kittybot.command.Category;
import de.kittybot.kittybot.command.old.Command;
import de.kittybot.kittybot.command.old.CommandContext;
import de.kittybot.kittybot.modules.MusicModule;
import de.kittybot.kittybot.utils.MusicUtils;

@SuppressWarnings("unused")
public class ShuffleCommand extends Command{

	public ShuffleCommand(){
		super("shuffle", "Used to shuffle the queue", Category.MUSIC);
	}

	@Override
	public void run(Args args, CommandContext ctx){
		var player = ctx.get(MusicModule.class).get(ctx.getGuildId());
		if(!MusicUtils.checkCommandRequirements(ctx, player)){
			return;
		}
		if(ctx.get(MusicModule.class).get(ctx.getGuildId()).shuffle()){
			ctx.sendSuccess("Queue shuffled");
			return;
		}
		ctx.sendError("Queue is empty. Nothing to shuffle");
	}

}
