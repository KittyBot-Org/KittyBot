package de.kittybot.kittybot.commands.tags.tags;

import de.kittybot.kittybot.modules.MessageModule;
import de.kittybot.kittybot.modules.TagsModule;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionLong;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionString;
import de.kittybot.kittybot.slashcommands.application.options.GuildSubCommand;
import de.kittybot.kittybot.slashcommands.interaction.GuildInteraction;
import de.kittybot.kittybot.slashcommands.interaction.Options;

public class EditCommand extends GuildSubCommand{

	public EditCommand(){
		super("edit", "Used to edit a tag");
		addOptions(
			new CommandOptionString("name", "Tag name").required(),
			new CommandOptionString("content", "Tag content"),
			new CommandOptionLong("message-id", "The message id to create a tag from"),
			new CommandOptionString("new-name", "The new tag name")
		);
	}

	@Override
	public void run(Options options, GuildInteraction ia){
		var tagName = options.getString("name");
		if(!options.has("content") && !options.has("message-id") && !options.has("new-name")){
			ia.error("Please provide either content, message-id or new-name");
			return;
		}
		String content;
		if(options.has("content")){
			content = options.getString("content");
		}
		else if(options.has("message-id")){
			var message = ia.get(MessageModule.class).getMessageById(options.getLong("message-id"));
			if(message == null){
				ia.error("Please provide a recent message id");
				return;
			}
			content = message.getContent();
		}
		else{
			content = null;
		}

		var edited = ia.get(TagsModule.class).edit(tagName, content, ia.getGuildId(), ia.getUserId(), options.has("new-name") ? options.getString("new-name") : null);
		if(edited){
			ia.reply("Edited tag with name `" + tagName + "`");
			return;
		}
		ia.error("Tag `" + tagName + "` does not exist or is not owned by you");
	}

}
