package de.kittybot.kittybot.commands.tags.tags;

import de.kittybot.kittybot.modules.TagsModule;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionString;
import de.kittybot.kittybot.slashcommands.application.options.GuildSubCommand;
import de.kittybot.kittybot.slashcommands.interaction.GuildInteraction;
import de.kittybot.kittybot.slashcommands.interaction.Options;
import net.dv8tion.jda.api.Permission;

public class DeleteCommand extends GuildSubCommand{

	public DeleteCommand(){
		super("delete", "Used to delete a tag");
		addOptions(
			new CommandOptionString("name", "Tag name").required()
		);
	}

	@Override
	public void run(Options options, GuildInteraction ia){
		var tagName = options.getString("name");
		var deleted = false;
		if(ia.getMember().hasPermission(Permission.ADMINISTRATOR)){
			deleted = ia.get(TagsModule.class).delete(tagName, ia.getGuildId());
		}
		else{
			deleted = ia.get(TagsModule.class).delete(tagName, ia.getGuildId(), ia.getUserId());
		}

		if(deleted){
			ia.reply("Deleted tag with name `" + tagName + "`");
			return;
		}
		ia.error("Tag `" + tagName + "` does not exist or is not owned by you");
	}

}
