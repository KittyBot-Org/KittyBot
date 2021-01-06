package de.kittybot.kittybot.commands.music;

import de.kittybot.kittybot.command.Args;
import de.kittybot.kittybot.command.Category;
import de.kittybot.kittybot.command.Command;
import de.kittybot.kittybot.command.CommandContext;
import de.kittybot.kittybot.utils.MusicUtils;

public class ShuffleCommand extends Command{

	public ShuffleCommand(){
		super("shuffle", "Used to shuffle the queue", Category.MUSIC);
	}

	@Override
	public void run(Args args, CommandContext ctx){
		if(!MusicUtils.checkVoiceRequirements(ctx)){
			return;
		}
		if(ctx.getMusicModule().get(ctx.getGuildId()).shuffle()){
			ctx.sendSuccess("Queue shuffled");
			return;
		}
		ctx.sendError("Queue is empty. Nothing to shuffle");
	}

}
