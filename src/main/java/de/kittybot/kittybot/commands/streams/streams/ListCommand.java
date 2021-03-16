package de.kittybot.kittybot.commands.streams.streams;

import de.kittybot.kittybot.modules.StreamModule;
import de.kittybot.kittybot.objects.streams.StreamType;
import de.kittybot.kittybot.slashcommands.application.options.GuildSubCommand;
import de.kittybot.kittybot.slashcommands.interaction.GuildInteraction;
import de.kittybot.kittybot.slashcommands.interaction.Options;
import de.kittybot.kittybot.utils.MessageUtils;

import java.util.stream.Collectors;

public class ListCommand extends GuildSubCommand{

	public ListCommand(){
		super("list", "Lists stream announcements");
	}

	@Override
	public void run(Options options, GuildInteraction ia){
		var streamAnnouncements = ia.get(StreamModule.class).get(ia.getGuildId());
		if(streamAnnouncements.isEmpty()){
			ia.error("No stream announcements found. Create them with `/settings streamannouncements add <service> <username>`");
			return;
		}
		ia.reply("**Stream Announcements:**\n" + streamAnnouncements.stream().map(sa -> MessageUtils.maskLink(sa.getUserName(), "https://twitch.tv/" + sa.getUserName()) + " on " + StreamType.byId(sa.getStreamType()).getName()).collect(Collectors.joining("\n")));
	}

}
