package de.kittybot.kittybot.commands.tags.tags;

import de.kittybot.kittybot.modules.TagsModule;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionLong;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionString;
import de.kittybot.kittybot.slashcommands.application.options.GuildSubCommand;
import de.kittybot.kittybot.slashcommands.interaction.GuildInteraction;
import de.kittybot.kittybot.slashcommands.interaction.Options;

public class CreateCommand extends GuildSubCommand{

	public CreateCommand(){
		super("create", "Used to create a tag");
		addOptions(
			new CommandOptionString("name", "Tag name").required(),
			new CommandOptionString("content", "Tag content"),
			new CommandOptionLong("message-id", "The message id to create a tag from")
		);
	}

	@Override
	public void run(Options options, GuildInteraction ia){
		var tagName = options.getString("name");
		if(tagName.length() > 64){
			ia.error("Tag names must be 64 or less characters");
			return;
		}
		if(!options.has("content") && !options.has("message-id")){
			ia.reply("Please provide either content or message-id");
			return;
		}
		var content = "";
		if(options.has("content")){
			content = options.getString("content");
		}
		else{
			var message = ia.getChannel().retrieveMessageById(options.getLong("message-id")).complete();
			if(message == null){
				ia.error("Please provide a recent message id");
				return;
			}
			content = message.getContentRaw();
		}

		var created = ia.get(TagsModule.class).create(tagName, content, ia.getGuildId(), ia.getUserId());

		if(created){
			ia.reply("Created tag with name `" + tagName + "`");
			return;
		}
		ia.error("A tag with the name `" + tagName + "` already exists");
	}

}
