package de.kittybot.kittybot.commands.tags.tags;

import de.kittybot.kittybot.modules.TagsModule;
import de.kittybot.kittybot.slashcommands.GuildCommandContext;
import de.kittybot.kittybot.slashcommands.Options;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionString;
import de.kittybot.kittybot.slashcommands.application.options.GuildSubCommand;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.utils.MarkdownSanitizer;

public class RemoveCommand extends GuildSubCommand{

	public RemoveCommand(){
		super("remove", "Removes a published tag from slash commands");
		addOptions(
			new CommandOptionString("name", "Tag name").required()
		);
		addPermissions(Permission.MANAGE_SERVER);
	}

	@Override
	public void run(Options options, GuildCommandContext ctx){
		var name = options.getString("name");
		var tagsModule = ctx.get(TagsModule.class);
		var tag = tagsModule.get(name, ctx.getGuildId());
		if(tag == null){
			ctx.error("Tag with name `" + MarkdownSanitizer.escape(name) + "` not found");
			return;
		}
		if(tag.getCommandId() == -1L){
			ctx.error("Tag with name `" + MarkdownSanitizer.escape(name) + "` is not published");
			return;
		}
		var res = tagsModule.removePublishedTag(ctx.getGuildId(), tag.getCommandId());
		if(!res){
			ctx.error("Something went wrong while removing your tag from slash commands");
			return;
		}
		ctx.reply("Successfully removed tag from slash commands");
	}

}
