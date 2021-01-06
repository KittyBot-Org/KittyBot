package de.kittybot.kittybot.commands.tags;

import de.kittybot.kittybot.command.Args;
import de.kittybot.kittybot.command.Category;
import de.kittybot.kittybot.command.Command;
import de.kittybot.kittybot.command.CommandContext;
import de.kittybot.kittybot.exceptions.CommandException;

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
		ctx.getTagModule().create(args.get(0), ctx.getRawMessage(1), ctx.getGuildId(), ctx.getUser().getIdLong());
		ctx.sendSuccess("Created tag");
	}

}
