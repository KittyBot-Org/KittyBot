package de.kittybot.kittybot.commands.tags;

import de.kittybot.kittybot.command.Args;
import de.kittybot.kittybot.command.Category;
import de.kittybot.kittybot.command.Command;
import de.kittybot.kittybot.command.context.CommandContext;
import de.kittybot.kittybot.exceptions.CommandException;
import de.kittybot.kittybot.modules.TagModule;
import de.kittybot.kittybot.utils.MessageUtils;
import de.kittybot.kittybot.utils.TimeUtils;
import net.dv8tion.jda.api.EmbedBuilder;

@SuppressWarnings("unused")
public class TagInfoCommand extends Command{

	public TagInfoCommand(Command parent){
		super(parent, "info", "Shows info for a specific tag", Category.TAGS);
		setUsage("<name>");
	}

	@Override
	protected void run(Args args, CommandContext ctx) throws CommandException{
		if(args.isEmpty()){
			ctx.sendUsage(this);
			return;
		}
		var tag = ctx.get(TagModule.class).get(args.get(0), ctx.getGuildId());
		ctx.sendSuccess(new EmbedBuilder().setAuthor("Tag Info", Category.TAGS.getUrl(), Category.TAGS.getEmoteUrl())
			.addField("ID", Long.toString(tag.getId()), true)
			.addField("Name", tag.getName(), true)
			.addBlankField(true)

			.addField("Created", TimeUtils.format(tag.getCreatedAt()), true)
			.addField("Last Updated", tag.getUpdatedAt() == null ? "not edited" : TimeUtils.format(tag.getUpdatedAt()), true)
			.addBlankField(true)

			.addField("Owner", MessageUtils.getUserMention(tag.getUserId()), true)

		);
	}

}
