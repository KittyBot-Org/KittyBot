package de.kittybot.kittybot.commands.music;

import de.kittybot.kittybot.command.Args;
import de.kittybot.kittybot.command.Category;
import de.kittybot.kittybot.command.Command;
import de.kittybot.kittybot.command.context.CommandContext;
import de.kittybot.kittybot.modules.MusicModule;

@SuppressWarnings("unused")
public class PlayCommand extends Command{

	public PlayCommand(){
		super("play", "Used to play music", Category.MUSIC);
		addAliases("p");
		setUsage("<link/search term>");
	}

	@Override
	public void run(Args args, CommandContext ctx){
		if(args.isEmpty()){
			ctx.sendError("Please provide a link or search term");
			return;
		}
		var player = ctx.get(MusicModule.class).get(ctx.getGuildId());
		if(player == null){
			player = ctx.get(MusicModule.class).create(ctx);
		}
		player.loadItem(ctx);
	}

}
