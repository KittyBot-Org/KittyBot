package de.kittybot.kittybot.commands.music;

import de.kittybot.kittybot.command.Args;
import de.kittybot.kittybot.command.Category;
import de.kittybot.kittybot.command.Command;
import de.kittybot.kittybot.command.CommandContext;
import de.kittybot.kittybot.modules.MusicModule;
import de.kittybot.kittybot.utils.MusicUtils;

public class PauseCommand extends Command{

	public PauseCommand(){
		super("pause", "Used to pause music", Category.MUSIC);
	}

	@Override
	public void run(Args args, CommandContext ctx){
		if(!MusicUtils.checkVoiceRequirements(ctx)){
			return;
		}
		ctx.get(MusicModule.class).get(ctx.getGuildId()).pause();
		ctx.sendSuccess("Toggled pause");
	}

}
