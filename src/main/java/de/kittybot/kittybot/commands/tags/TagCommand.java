package de.kittybot.kittybot.commands.tags;

import de.kittybot.kittybot.modules.TagsModule;
import de.kittybot.kittybot.slashcommands.application.Category;
import de.kittybot.kittybot.slashcommands.application.RunGuildCommand;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionString;
import de.kittybot.kittybot.slashcommands.interaction.GuildInteraction;
import de.kittybot.kittybot.slashcommands.interaction.Options;
import de.kittybot.kittybot.slashcommands.interaction.response.InteractionResponse;
import net.dv8tion.jda.api.entities.Message;

@SuppressWarnings("unused")
public class TagCommand extends RunGuildCommand{

	public TagCommand(){
		super("tag", "Displays a tag", Category.TAGS);
		addOptions(
			new CommandOptionString("name", "The tag name").required()
		);
	}

	@Override
	public void run(Options options, GuildInteraction ia){
		var tagName = options.getString("name");
		var tag = ia.get(TagsModule.class).get(tagName, ia.getGuildId());

		if(tag == null){
			ia.error("Tag with name `" + tagName + "` not found");
			return;
		}
		ia.reply(new InteractionResponse.Builder().setContent(tag.getContent())
			.setAllowedMentions(Message.MentionType.EMOTE, Message.MentionType.CHANNEL)
			.build()
		);
	}

}
