package de.kittybot.kittybot.commands.tags.tags;

import de.kittybot.kittybot.modules.TagsModule;
import de.kittybot.kittybot.objects.settings.Tag;
import de.kittybot.kittybot.slashcommands.GuildCommandContext;
import de.kittybot.kittybot.slashcommands.Options;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionUser;
import de.kittybot.kittybot.slashcommands.application.options.GuildSubCommand;
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
	public void run(Options options, GuildCommandContext ctx){
		List<Tag> tags;
		if(options.has("user")){
			tags = ctx.get(TagsModule.class).get(ctx.getGuildId(), options.getLong("user"));
		}
		else{
			tags = ctx.get(TagsModule.class).get(ctx.getGuildId());
		}

		if(tags.isEmpty()){
			ctx.reply("No tags created yet");
			return;
		}
		// TODO add paginator
		ctx.reply("**Following tags exist:**\n" + tags.stream().map(tag -> "• `" + tag.getName() + "` (" + MessageUtils.getUserMention(tag.getUserId()) + ")").collect(Collectors.joining("\n")));
	}

}
