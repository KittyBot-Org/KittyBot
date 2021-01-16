package de.kittybot.kittybot.main.commands.music;

import de.kittybot.kittybot.command.old.Args;
import de.kittybot.kittybot.command.Category;
import de.kittybot.kittybot.command.old.Command;
import de.kittybot.kittybot.command.old.CommandContext;
import de.kittybot.kittybot.modules.MusicModule;
import de.kittybot.kittybot.utils.MusicUtils;

@SuppressWarnings("unused")
public class VolumeCommand extends Command{

	public VolumeCommand(){
		super("volume", "Used to set the player volume", Category.MUSIC);
		addAliases("vol");
		setUsage("<0-200>");
	}

	@Override
	public void run(Args args, CommandContext ctx){
		var player = ctx.get(MusicModule.class).get(ctx.getGuildId());
		if(!MusicUtils.checkCommandRequirements(ctx, player)){
			return;
		}
		if(args.isEmpty()){
			ctx.sendUsage(this);
			return;
		}
		int volume;
		try{
			volume = Integer.parseInt(args.get(0));
		}
		catch(NumberFormatException ignored){
			ctx.sendError("Please provide a valid number between 0-200");
			return;
		}
		player.setVolume(volume);
		ctx.sendSuccess("Volume set to: `" + volume + "`");
	}

}
