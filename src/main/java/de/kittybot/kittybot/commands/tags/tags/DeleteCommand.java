package de.kittybot.kittybot.commands.tags.tags;

import de.kittybot.kittybot.modules.TagsModule;
import de.kittybot.kittybot.slashcommands.GuildCommandContext;
import de.kittybot.kittybot.slashcommands.Options;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionString;
import de.kittybot.kittybot.slashcommands.application.options.GuildSubCommand;
import net.dv8tion.jda.api.Permission;

public class DeleteCommand extends GuildSubCommand{

	public DeleteCommand(){
		super("delete", "Used to delete a tag");
		addOptions(
			new CommandOptionString("name", "Tag name").required()
		);
	}

	@Override
	public void run(Options options, GuildCommandContext ctx){
		var tagName = options.getString("name");
		var deleted = false;
		if(ctx.getMember().hasPermission(Permission.ADMINISTRATOR)){
			deleted = ctx.get(TagsModule.class).delete(tagName, ctx.getGuildId());
		}
		else{
			deleted = ctx.get(TagsModule.class).delete(tagName, ctx.getGuildId(), ctx.getUserId());
		}

		if(deleted){
			ctx.reply("Deleted tag with name `" + tagName + "`");
			return;
		}
		ctx.error("Tag `" + tagName + "` does not exist or is not owned by you");
	}

}
