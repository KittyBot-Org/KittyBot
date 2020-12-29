package de.kittybot.kittybot.commands.tags;

import de.kittybot.kittybot.command.Category;
import de.kittybot.kittybot.command.Command;
import de.kittybot.kittybot.command.ctx.CommandContext;
import de.kittybot.kittybot.exceptions.CommandException;

import java.util.List;

public class TagEditCommand extends Command{

	public TagEditCommand(Command parent){
		super(parent, "edit", "Edits a tags", Category.TAGS);
		addAliases("e");
	}

	@Override
	protected void run(List<String> args, CommandContext ctx) throws CommandException{
		if(args.size() < 2){
			ctx.sendUsage(this);
			return;
		}
		ctx.getTagManager().edit(args.get(0), ctx.getRawMessage(1), ctx.getGuild().getIdLong(), ctx.getUser().getIdLong());
		ctx.sendSuccess("Edited tag");
	}

}
