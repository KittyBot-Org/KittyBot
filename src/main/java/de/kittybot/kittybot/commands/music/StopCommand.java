package de.kittybot.kittybot.commands.music;

import de.kittybot.kittybot.command.Category;
import de.kittybot.kittybot.command.application.Command;
import de.kittybot.kittybot.command.application.CommandOptionChoice;
import de.kittybot.kittybot.command.application.RunnableCommand;
import de.kittybot.kittybot.command.context.CommandContext;
import de.kittybot.kittybot.command.interaction.Options;
import de.kittybot.kittybot.command.options.CommandOptionString;
import de.kittybot.kittybot.modules.MusicModule;
import de.kittybot.kittybot.objects.SearchProvider;
import de.kittybot.kittybot.utils.MusicUtils;

@SuppressWarnings("unused")
public class StopCommand extends Command implements RunnableCommand{

	public StopCommand(){
		super("stop", "Stops playing music", Category.MUSIC);
	}

	@Override
	public void run(Options options, CommandContext ctx){
		var player = ctx.get(MusicModule.class).get(ctx.getGuildId());
		if(!MusicUtils.checkCommandRequirements(ctx, player)){
			return;
		}
		if(!MusicUtils.checkMusicPermissions(ctx, player)){
			return;
		}
		ctx.get(MusicModule.class).destroy(ctx.getGuildId());
		ctx.reply("Bye bye");
	}

}
