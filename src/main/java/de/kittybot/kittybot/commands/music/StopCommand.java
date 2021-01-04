package de.kittybot.kittybot.commands.music;

import de.kittybot.kittybot.command.Args;
import de.kittybot.kittybot.command.Category;
import de.kittybot.kittybot.command.Command;
import de.kittybot.kittybot.command.CommandContext;
import de.kittybot.kittybot.utils.MusicUtils;

public class StopCommand extends Command{

	public StopCommand(){
		super("stop", "Used to stop music", Category.MUSIC);
		addAliases("stfu");
	}

	@Override
	public void run(Args args, CommandContext ctx){
		if(!MusicUtils.checkVoiceRequirements(ctx)){
			return;
		}
		ctx.getMusicManager().destroy(ctx.getGuildId());
		ctx.sendSuccess("Toggled pause");
	}

}
