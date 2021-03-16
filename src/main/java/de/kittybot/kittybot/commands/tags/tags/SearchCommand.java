package de.kittybot.kittybot.commands.tags.tags;

import de.kittybot.kittybot.modules.TagsModule;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionString;
import de.kittybot.kittybot.slashcommands.application.options.GuildSubCommand;
import de.kittybot.kittybot.slashcommands.interaction.GuildInteraction;
import de.kittybot.kittybot.slashcommands.interaction.Options;
import de.kittybot.kittybot.utils.MessageUtils;

import java.util.stream.Collectors;

public class SearchCommand extends GuildSubCommand{

	public SearchCommand(){
		super("search", "Used to search a tag");
		addOptions(
			new CommandOptionString("name", "Tag name").required()
		);
	}

	@Override
	public void run(Options options, GuildInteraction ia){
		var tagName = options.getString("name");
		var tags = ia.get(TagsModule.class).search(tagName, ia.getGuildId(), ia.getUserId());

		if(tags.isEmpty()){
			ia.reply("No tags found for `" + tagName + "`");
			return;
		}
		// TODO add paginator
		ia.reply("**Following tags were found for `" + tagName + "`:**\n" +
			tags.stream().map(tag -> "• `" + tag.getName() + "` (" + MessageUtils.getUserMention(tag.getUserId()) + ")").collect(Collectors.joining("\n"))
		);
	}

}
