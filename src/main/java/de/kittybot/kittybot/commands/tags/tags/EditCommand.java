package de.kittybot.kittybot.commands.tags.tags;

import de.kittybot.kittybot.modules.MessageModule;
import de.kittybot.kittybot.modules.TagsModule;
import de.kittybot.kittybot.slashcommands.GuildCommandContext;
import de.kittybot.kittybot.slashcommands.Options;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionLong;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionString;
import de.kittybot.kittybot.slashcommands.application.options.GuildSubCommand;

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
	public void run(Options options, GuildCommandContext ctx){
		var tagName = options.getString("name");
		if(!options.has("content") && !options.has("message-id") && !options.has("new-name")){
			ctx.error("Please provide either content, message-id or new-name");
			return;
		}
		String content;
		if(options.has("content")){
			content = options.getString("content");
		}
		else if(options.has("message-id")){
			var message = ctx.get(MessageModule.class).getMessageById(options.getLong("message-id"));
			if(message == null){
				ctx.error("Please provide a recent message id");
				return;
			}
			content = message.getContent();
		}
		else{
			content = null;
		}

		var edited = ctx.get(TagsModule.class).edit(tagName, content, ctx.getGuildId(), ctx.getUserId(), options.has("new-name") ? options.getString("new-name") : null);
		if(edited){
			ctx.reply("Edited tag with name `" + tagName + "`");
			return;
		}
		ctx.error("Tag `" + tagName + "` does not exist or is not owned by you");
	}

}
