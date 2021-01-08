package de.kittybot.kittybot.commands.music;

import de.kittybot.kittybot.command.Args;
import de.kittybot.kittybot.command.Category;
import de.kittybot.kittybot.command.Command;
import de.kittybot.kittybot.command.CommandContext;
import de.kittybot.kittybot.modules.MusicModule;
import de.kittybot.kittybot.utils.MusicUtils;

public class PlayCommand extends Command{

	public PlayCommand(){
		super("play", "Used to play music", Category.MUSIC);
		addAliases("p");
		setUsage("<link/search term>");
	}

	@Override
	public void run(Args args, CommandContext ctx){
		if(!MusicUtils.checkVoiceRequirements(ctx)){
			return;
		}
		ctx.get(MusicModule.class).create(ctx).loadItem(ctx);
	}

}
