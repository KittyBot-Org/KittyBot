package de.kittybot.kittybot.commands.tags;

import de.kittybot.kittybot.modules.TagsModule;
import de.kittybot.kittybot.slashcommands.GuildCommandContext;
import de.kittybot.kittybot.slashcommands.Options;
import de.kittybot.kittybot.slashcommands.application.Category;
import de.kittybot.kittybot.slashcommands.application.RunGuildCommand;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionString;
import net.dv8tion.jda.api.entities.Message;

import java.util.Set;

@SuppressWarnings("unused")
public class TagCommand extends RunGuildCommand{

	public TagCommand(){
		super("tag", "Displays a tag", Category.TAGS);
		addOptions(
			new CommandOptionString("name", "The tag name").required()
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
		ctx.getEvent().reply(tag.getContent()).setAllowedMentions(Set.of(Message.MentionType.EMOTE, Message.MentionType.CHANNEL)).queue();
	}

}
