package de.kittybot.kittybot.commands.music;

import de.kittybot.kittybot.modules.MusicModule;
import de.kittybot.kittybot.objects.music.RepeatMode;
import de.kittybot.kittybot.slashcommands.application.Category;
import de.kittybot.kittybot.slashcommands.application.CommandOptionChoice;
import de.kittybot.kittybot.slashcommands.application.RunGuildCommand;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionString;
import de.kittybot.kittybot.slashcommands.interaction.GuildInteraction;
import de.kittybot.kittybot.slashcommands.interaction.Options;

@SuppressWarnings("unused")
public class RepeatCommand extends RunGuildCommand{

	public RepeatCommand(){
		super("repeat", "Sets the repeat mode", Category.MUSIC);
		addOptions(
			new CommandOptionString("type", "The repeat mode").required()
				.addChoices(
					new CommandOptionChoice<>("off", "OFF"),
					new CommandOptionChoice<>("song", "SONG"),
					new CommandOptionChoice<>("queue", "QUEUE")
				)
		);
	}

	@Override
	public void run(Options options, GuildInteraction ia){
		var scheduler = ia.get(MusicModule.class).getScheduler(ia.getGuildId());
		var repeatMode = RepeatMode.valueOf(options.getString("type"));

		scheduler.setRepeatMode(repeatMode);
		ia.reply("Set repeat mode  to `" + repeatMode.getName() + "`");
	}

}
