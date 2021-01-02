package de.kittybot.kittybot.commands.tags;

import de.kittybot.kittybot.command.Args;
import de.kittybot.kittybot.command.Category;
import de.kittybot.kittybot.command.Command;
import de.kittybot.kittybot.command.ctx.CommandContext;
import de.kittybot.kittybot.exceptions.CommandException;

import java.util.List;

public class TagCreateCommand extends Command{

	public TagCreateCommand(Command parent){
		super(parent, "create", "Creates a tags", Category.TAGS);
		addAliases("new", "neu");
		setUsage("<name> <content>");
	}

	@Override
	protected void run(Args args, CommandContext ctx) throws CommandException{
		if(args.size() < 2){
			ctx.sendUsage(this);
			return;
		}
		ctx.getTagManager().create(args.get(0), ctx.getRawMessage(1), ctx.getGuildId(), ctx.getUser().getIdLong());
		ctx.sendSuccess("Created tag");
	}

}
