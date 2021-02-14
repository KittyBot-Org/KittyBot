package de.kittybot.kittybot.commands.music;

import de.kittybot.kittybot.modules.MusicModule;
import de.kittybot.kittybot.slashcommands.application.Category;
import de.kittybot.kittybot.slashcommands.application.Command;
import de.kittybot.kittybot.slashcommands.application.RunnableCommand;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionInteger;
import de.kittybot.kittybot.slashcommands.context.CommandContext;
import de.kittybot.kittybot.slashcommands.context.Options;
import de.kittybot.kittybot.utils.MusicUtils;

@SuppressWarnings("unused")
public class VolumeCommand extends Command implements RunnableCommand{

	public VolumeCommand(){
		super("volume", "Used to set the player volume", Category.MUSIC);
		addOptions(
			new CommandOptionInteger("volume", "The music volume")
		);
	}

	@Override
	public void run(Options options, CommandContext ctx){
		var scheduler = ctx.get(MusicModule.class).getScheduler(ctx.getGuildId());
		if(!MusicUtils.checkCommandRequirements(ctx, scheduler)){
			return;
		}
		int volume = options.getInt("volume");
		if(volume < 0 || volume > 150){
			ctx.error("Volume needs to between 0 and 150");
			return;
		}
		scheduler.setVolume(volume);
		ctx.reply("Volume set to: `" + volume + "`");
	}

}
