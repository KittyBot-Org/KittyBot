package de.kittybot.kittybot.commands.tags;

import de.kittybot.kittybot.command.Category;
import de.kittybot.kittybot.command.application.Command;
import de.kittybot.kittybot.command.application.RunnableCommand;
import de.kittybot.kittybot.command.context.CommandContext;
import de.kittybot.kittybot.command.interaction.Options;
import de.kittybot.kittybot.command.options.CommandOptionString;
import de.kittybot.kittybot.command.response.Response;
import de.kittybot.kittybot.modules.TagsModule;
import net.dv8tion.jda.api.entities.Message;

@SuppressWarnings("unused")
public class TagCommand extends Command implements RunnableCommand{

	public TagCommand(){
		super("tag", "Displays a tag", Category.TAGS);
		addOptions(
				new CommandOptionString("name", "The tag name").setRequired()
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
		ctx.reply(new Response.Builder().setContent(tag.getContent())
				.setAllowedMentions(Message.MentionType.EMOTE, Message.MentionType.CHANNEL)
				.build()
		);
	}

}
