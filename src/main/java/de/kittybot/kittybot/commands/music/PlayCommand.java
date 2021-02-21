package de.kittybot.kittybot.commands.music;

import de.kittybot.kittybot.modules.MusicModule;
import de.kittybot.kittybot.objects.music.SearchProvider;
import de.kittybot.kittybot.slashcommands.application.Category;
import de.kittybot.kittybot.slashcommands.application.CommandOptionChoice;
import de.kittybot.kittybot.slashcommands.application.RunGuildCommand;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionString;
import de.kittybot.kittybot.slashcommands.interaction.GuildInteraction;
import de.kittybot.kittybot.slashcommands.interaction.Options;
import de.kittybot.kittybot.utils.MusicUtils;
import net.dv8tion.jda.api.Permission;

@SuppressWarnings("unused")
public class PlayCommand extends RunGuildCommand{

	public PlayCommand(){
		super("play", "Plays a link or searches on yt/sc", Category.MUSIC);
		addOptions(
			new CommandOptionString("query", "A link or search query to play from").required(),
			new CommandOptionString("search-provider", "Which search provider use")
				.addChoices(
					new CommandOptionChoice<>("youtube", "yt"),
					new CommandOptionChoice<>("soundcloud", "sc")/*,
					new CommandOptionChoice<>("spotify", "sp")*/
				)
		);
	}

	@Override
	public void run(Options options, GuildInteraction ia){
		if(!ia.getGuild().getSelfMember().hasPermission(ia.getChannel(), Permission.MESSAGE_WRITE, Permission.MESSAGE_ADD_REACTION, Permission.MESSAGE_EXT_EMOJI, Permission.MESSAGE_HISTORY, Permission.VIEW_CHANNEL)){
			ia.error("Please make sure I have following permissions in this channel: `Send Messages`, `Add Reactions`, `Use External Emoji`, `Read Message History`, `View Channel`");
			return;
		}
		if(!MusicUtils.checkMusicRequirements(ia)){
			return;
		}
		var musicModule = ia.get(MusicModule.class);
		var searchProvider = SearchProvider.YOUTUBE;
		if(options.has("search-provider")){
			searchProvider = SearchProvider.getByShortname(options.getString("search-provider"));
		}
		musicModule.play(ia, options.getString("query"), searchProvider);
	}

}
