package de.kittybot.kittybot.commands.tags.tags;

import de.kittybot.kittybot.modules.TagsModule;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionString;
import de.kittybot.kittybot.slashcommands.application.options.GuildSubCommand;
import de.kittybot.kittybot.slashcommands.interaction.GuildInteraction;
import de.kittybot.kittybot.slashcommands.interaction.Options;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.utils.MarkdownSanitizer;

public class UnpublishCommand extends GuildSubCommand{

	public UnpublishCommand(){
		super("unpublish", "Unpublishes a published tag from slash commands");
		addOptions(
			new CommandOptionString("name", "Tag name").required()
		);
		addPermissions(Permission.MANAGE_SERVER);
	}

	@Override
	public void run(Options options, GuildInteraction ia){
		var name = options.getString("name");
		var tagsModule = ia.get(TagsModule.class);
		var tag = tagsModule.get(name, ia.getGuildId());
		if(tag == null){
			ia.error("Tag with name `" + MarkdownSanitizer.escape(name) + "` not found");
			return;
		}
		if(tag.getCommandId() == -1L){
			ia.error("Tag with name `" + MarkdownSanitizer.escape(name) + "` is not published");
			return;
		}
		var res = tagsModule.removePublishedTag(ia.getGuildId(), tag.getCommandId());
		if(!res){
			ia.error("Something went wrong while removing your tag from slash commands");
			return;
		}
		ia.reply("Successfully removed tag from slash commands");
	}

}
