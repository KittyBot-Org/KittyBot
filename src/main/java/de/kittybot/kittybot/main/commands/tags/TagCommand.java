package de.kittybot.kittybot.main.commands.tags;

import de.kittybot.kittybot.command.old.Args;
import de.kittybot.kittybot.command.Category;
import de.kittybot.kittybot.command.old.Command;
import de.kittybot.kittybot.command.old.CommandContext;
import de.kittybot.kittybot.exceptions.CommandException;
import de.kittybot.kittybot.modules.TagsModule;
import net.dv8tion.jda.api.entities.Message;

import java.util.List;

@SuppressWarnings("unused")
public class TagCommand extends Command{

	public TagCommand(){
		super("tag", "Creates/Edits/Deletes/Lists/Searches tags", Category.TAGS);
		addChildren(
				new TagCreateCommand(this),
				new TagEditCommand(this),
				new TagDeleteCommand(this),
				new TagListCommand(this),
				new TagSearchCommand(this),
				new TagInfoCommand(this)
		);
		addAliases("t");
		setUsage("<create/edit/delete/list/search/info/tag-name> <tag-name> <content>");
	}

	@Override
	protected void run(Args args, CommandContext ctx) throws CommandException{
		if(args.isEmpty()){
			ctx.sendUsage(this.getUsage() + " " + this.getRawUsage());
			return;
		}
		var content = ctx.get(TagsModule.class).get(args.get(0), ctx.getGuildId()).getContent();
		if(content == null){
			ctx.sendError("Tag '" + args.get(0) + "' not found");
			return;
		}
		ctx.sendSuccess(ctx.getChannel().sendMessage(content).allowedMentions(List.of(Message.MentionType.CHANNEL, Message.MentionType.EMOTE)));
	}

}
