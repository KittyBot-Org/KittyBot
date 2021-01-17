package de.kittybot.kittybot.commands.tags;

import de.kittybot.kittybot.slashcommands.application.Category;
import de.kittybot.kittybot.slashcommands.application.Command;
import de.kittybot.kittybot.slashcommands.application.RunnableCommand;
import de.kittybot.kittybot.slashcommands.context.CommandContext;
import de.kittybot.kittybot.slashcommands.context.Options;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionString;
import de.kittybot.kittybot.slashcommands.interaction.response.InteractionResponse;
import de.kittybot.kittybot.modules.TagsModule;
import net.dv8tion.jda.api.entities.Message;

@SuppressWarnings("unused")
public class TagCommand extends Command implements RunnableCommand{

	public TagCommand(){
		super("tag", "Displays a tag", Category.TAGS);
		addOptions(
				new CommandOptionString("name", "The tag name").required()
		);
	}

	@Override
	public void run(Options options, CommandContext ctx){
		var tagName = options.getString("name");
		var tag = ctx.get(TagsModule.class).get(tagName, ctx.getGuildId());

		if(tag == null){
			ctx.error("Tag with name `" + tagName + "` not found");
			return;
		}
		ctx.reply(new InteractionResponse.Builder().setContent(tag.getContent())
				.setAllowedMentions(Message.MentionType.EMOTE, Message.MentionType.CHANNEL)
				.build()
		);
	}

}
