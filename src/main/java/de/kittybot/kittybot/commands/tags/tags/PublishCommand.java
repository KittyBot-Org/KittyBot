package de.kittybot.kittybot.commands.tags.tags;

import de.kittybot.kittybot.modules.TagsModule;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionString;
import de.kittybot.kittybot.slashcommands.application.options.GuildSubCommand;
import de.kittybot.kittybot.slashcommands.interaction.GuildInteraction;
import de.kittybot.kittybot.slashcommands.interaction.Options;
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
	public void run(Options options, GuildInteraction ia){
		var name = options.getString("name");
		var tagsModule = ia.get(TagsModule.class);
		var tag = tagsModule.get(name, ia.getGuildId());
		if(tag == null){
			ia.error("Tag with name `" + MarkdownSanitizer.escape(name) + "` not found");
			return;
		}
		if(!COMMAND_NAME_PATTERN.matcher(name).matches()){
			ia.error("Please make sure your tag name only contains letters, numbers & -");
			return;
		}

		if(!tagsModule.canPublishTag(ia.getGuildId())){
			ia.error("You reached the maximum of 100 guild commands due to discords limitations");
			return;
		}
		var res = tagsModule.publishTag(name, options.getString("description"), ia.getGuildId());
		if(!res){
			ia.error("Something went wrong while publishing your tag to slash commands");
			return;
		}
		ia.reply("Successfully published your tag to slash commands");
	}

}
