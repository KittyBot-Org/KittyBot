package de.kittybot.kittybot.commands.music;

import de.kittybot.kittybot.modules.MusicModule;
import de.kittybot.kittybot.slashcommands.application.Category;
import de.kittybot.kittybot.slashcommands.application.RunGuildCommand;
import de.kittybot.kittybot.slashcommands.interaction.GuildInteraction;
import de.kittybot.kittybot.slashcommands.interaction.Options;
import de.kittybot.kittybot.utils.Colors;
import de.kittybot.kittybot.utils.MessageUtils;
import de.kittybot.kittybot.utils.MusicUtils;
import net.dv8tion.jda.api.EmbedBuilder;

@SuppressWarnings("unused")
public class HistoryCommand extends RunGuildCommand{

	public HistoryCommand(){
		super("history", "Displays the last played tracks", Category.MUSIC);
	}

	@Override
	public void run(Options options, GuildInteraction ia){
		var scheduler = ia.get(MusicModule.class).getScheduler(ia.getGuildId());
		if(!MusicUtils.checkCommandRequirements(ia, scheduler)){
			return;
		}
		var tracks = scheduler.getHistory();
		if(tracks.isEmpty()){
			ia.reply(new EmbedBuilder()
				.setColor(Colors.KITTYBOT_BLUE)
				.setDescription("The history is empty. Play some tracks to fill it-")
			);
			return;
		}
		MusicUtils.sendTracks(tracks, ia.getModules(), ia.getChannel(), ia.getUserId(), "Currently " + tracks.size() + " " + MessageUtils.pluralize("track", tracks) + " are in the history");
	}

}
