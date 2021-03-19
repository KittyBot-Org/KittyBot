package de.kittybot.kittybot.commands.tags.tags;

import de.kittybot.kittybot.modules.TagsModule;
import de.kittybot.kittybot.slashcommands.GuildCommandContext;
import de.kittybot.kittybot.slashcommands.Options;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionString;
import de.kittybot.kittybot.slashcommands.application.options.GuildSubCommand;
import de.kittybot.kittybot.utils.MessageUtils;
import de.kittybot.kittybot.utils.TimeUtils;

public class InfoCommand extends GuildSubCommand{

	public InfoCommand(){
		super("info", "Used to get info about a tag");
		addOptions(
			new CommandOptionString("name", "Tag name").required()
		);
	}

	@Override
	public void run(Options options, GuildCommandContext ctx){
		var tagName = options.getString("name");
		var tag = ctx.get(TagsModule.class).get(tagName, ctx.getGuildId());

		if(tag == null){
			ctx.error("Tag with name `" + tagName + "` not found");
			return;
		}
		ctx.reply(builder -> builder
			.setTitle("Tag `" + tagName + "`")
			.addField("Owner", MessageUtils.getUserMention(tag.getUserId()), false)
			.addField("ID", Long.toString(tag.getId()), false)
			.addField("Created at", TimeUtils.format(tag.getCreatedAt()), false)
			.addField("Updated at", (tag.getUpdatedAt() == null ? "not edited" : TimeUtils.format(tag.getUpdatedAt())), false)
		);
	}

}
