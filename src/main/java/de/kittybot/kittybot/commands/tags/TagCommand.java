package de.kittybot.kittybot.commands.tags;

import de.kittybot.kittybot.command.Args;
import de.kittybot.kittybot.command.Category;
import de.kittybot.kittybot.command.Command;
import de.kittybot.kittybot.command.ctx.CommandContext;
import de.kittybot.kittybot.exceptions.CommandException;

import java.util.List;

public class TagCommand extends Command{

	public TagCommand(){
		super("tag", "Creates/Edits/Deletes/Lists/Searches tags", Category.TAGS);
		addChildren(
				new TagCreateCommand(this),
				new TagEditCommand(this),
				new TagDeleteCommand(this),
				new TagListCommand(this),
				new TagSearchCommand(this)
		);
		addAliases("t");
		setUsage("<create/edit/delete/list/search/tag-name> <tag-name> <content>");
	}

	@Override
	protected void run(Args args, CommandContext ctx) throws CommandException{
		if(args.isEmpty()){
			ctx.sendUsage(this.getUsage() + " " + this.getRawUsage());
			return;
		}
		ctx.sendBlankSuccess(ctx.getTagManager().get(args.get(0), ctx.getGuildId()).getContent());
	}

}
