package de.kittybot.kittybot.commands.tags.tags;

import de.kittybot.kittybot.modules.TagsModule;
import de.kittybot.kittybot.slashcommands.GuildCommandContext;
import de.kittybot.kittybot.slashcommands.Options;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionString;
import de.kittybot.kittybot.slashcommands.application.options.GuildSubCommand;
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
	public void run(Options options, GuildCommandContext ctx){
		var tagName = options.getString("name");
		var tags = ctx.get(TagsModule.class).search(tagName, ctx.getGuildId(), ctx.getUserId());

		if(tags.isEmpty()){
			ctx.reply("No tags found for `" + tagName + "`");
			return;
		}
		// TODO add paginator
		ctx.reply("**Following tags were found for `" + tagName + "`:**\n" +
			tags.stream().map(tag -> "• `" + tag.getName() + "` (" + MessageUtils.getUserMention(tag.getUserId()) + ")").collect(Collectors.joining("\n"))
		);
	}

}
