package de.kittybot.kittybot.commands.music;

import de.kittybot.kittybot.command.Category;
import de.kittybot.kittybot.command.application.Command;
import de.kittybot.kittybot.command.application.RunnableCommand;
import de.kittybot.kittybot.command.context.CommandContext;
import de.kittybot.kittybot.command.interaction.Options;
import de.kittybot.kittybot.command.options.CommandOptionInteger;
import de.kittybot.kittybot.modules.MusicModule;
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
		var player = ctx.get(MusicModule.class).get(ctx.getGuildId());
		if(!MusicUtils.checkCommandRequirements(ctx, player)){
			return;
		}
		int volume = options.getInt("volume");
		if(volume < 0 || volume > 100){
			ctx.error("Volume");
			return;
		}
		player.setVolume(volume);
		ctx.reply("Volume set to: `" + volume + "`");
	}

}
