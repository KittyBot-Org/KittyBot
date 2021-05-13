package de.kittybot.kittybot.commands.music;

import de.kittybot.kittybot.modules.MusicModule;
import de.kittybot.kittybot.modules.GuildSettingsModule;
import de.kittybot.kittybot.slashcommands.application.Category;
import de.kittybot.kittybot.slashcommands.application.RunGuildCommand;
import de.kittybot.kittybot.slashcommands.interaction.GuildInteraction;
import de.kittybot.kittybot.slashcommands.interaction.Options;
import de.kittybot.kittybot.utils.MusicUtils;

@SuppressWarnings("unused")
public class ShuffleCommand extends RunGuildCommand{

	public ShuffleCommand(){
		super("shuffle", "Shuffles all queued tracks", Category.MUSIC);
	}

	@Override
	public void run(Options options, GuildInteraction ia){
		var scheduler = ia.get(MusicModule.class).getScheduler(ia.getGuildId());
		if(!MusicUtils.checkCommandRequirements(ia, scheduler)){
			return;
		}
		if(!ia.get(GuildSettingsModule.class).hasDJRole(ia.getMember())){
			ia.error("Only DJs are allowed shuffle");
			return;
		}
		if(scheduler.shuffle()){
			ia.reply("Queue shuffled");
			return;
		}
		ia.error("Queue is empty. Nothing to shuffle");
	}

}
