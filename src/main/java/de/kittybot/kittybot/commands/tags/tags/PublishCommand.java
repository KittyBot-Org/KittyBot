package de.kittybot.kittybot.commands.tags.tags;

import de.kittybot.kittybot.modules.TagsModule;
import de.kittybot.kittybot.slashcommands.GuildCommandContext;
import de.kittybot.kittybot.slashcommands.Options;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionString;
import de.kittybot.kittybot.slashcommands.application.options.GuildSubCommand;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.utils.MarkdownSanitizer;

import java.util.regex.Pattern;

public class PublishCommand extends GuildSubCommand{

	private static final Pattern COMMAND_NAME_PATTERN = Pattern.compile("^[\\w-]{1,32}$");

	public PublishCommand(){
		super("publish", "Publishes a tag as slash command for this guild");
		addOptions(
			new CommandOptionString("name", "Tag name").required(),
			new CommandOptionString("description", "Description of the tag").required()
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
		if(!COMMAND_NAME_PATTERN.matcher(name).matches()){
			ctx.error("Please make sure your tag name only contains letters, numbers & -");
			return;
		}

		if(!tagsModule.canPublishTag(ctx.getGuildId())){
			ctx.error("You reached the maximum of 50 guild commands due to discords limitations");
			return;
		}
		var res = tagsModule.publishTag(name, options.getString("description"), ctx.getGuildId());
		if(!res){
			ctx.error("Something went wrong while publishing your tag to slash commands");
			return;
		}
		ctx.reply("Successfully published your tag to slash commands");
	}

}
