package de.kittybot.kittybot.commands.tags.tags;

import de.kittybot.kittybot.modules.TagsModule;
import de.kittybot.kittybot.objects.settings.Tag;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionUser;
import de.kittybot.kittybot.slashcommands.application.options.GuildSubCommand;
import de.kittybot.kittybot.slashcommands.interaction.GuildInteraction;
import de.kittybot.kittybot.slashcommands.interaction.Options;
import de.kittybot.kittybot.utils.MessageUtils;

import java.util.List;
import java.util.stream.Collectors;

public class ListCommand extends GuildSubCommand{

	public ListCommand(){
		super("list", "Used to list tags");
		addOptions(
			new CommandOptionUser("user", "Filter by user")
		);
	}

	@Override
	public void run(Options options, GuildInteraction ia){
		List<Tag> tags;
		if(options.has("user")){
			tags = ia.get(TagsModule.class).get(ia.getGuildId(), options.getLong("user"));
		}
		else{
			tags = ia.get(TagsModule.class).get(ia.getGuildId());
		}

		if(tags.isEmpty()){
			ia.reply("No tags created yet");
			return;
		}
		// TODO add paginator
		ia.reply("**Following tags exist:**\n" + tags.stream().map(tag -> "• `" + tag.getName() + "` (" + MessageUtils.getUserMention(tag.getUserId()) + ")").collect(Collectors.joining("\n")));
	}

}
