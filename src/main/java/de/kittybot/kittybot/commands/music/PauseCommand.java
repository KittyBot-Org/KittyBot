package de.kittybot.kittybot.commands.music;

import de.kittybot.kittybot.command.Args;
import de.kittybot.kittybot.command.Category;
import de.kittybot.kittybot.command.Command;
import de.kittybot.kittybot.command.CommandContext;
import de.kittybot.kittybot.utils.Colors;
import de.kittybot.kittybot.utils.MessageUtils;
import de.kittybot.kittybot.utils.MusicUtils;
import net.dv8tion.jda.api.EmbedBuilder;

public class PauseCommand extends Command{

	public PauseCommand(){
		super("pause", "Used to pause music", Category.MUSIC);;
	}

	@Override
	public void run(Args args, CommandContext ctx){
		if(!MusicUtils.checkVoiceRequirements(ctx)){
			return;
		}
		ctx.getMusicManager().get(ctx.getGuildId()).pause();
		ctx.sendSuccess("Toggled pause");
	}

}
