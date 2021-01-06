package de.kittybot.kittybot.commands.tags;

import de.kittybot.kittybot.command.Args;
import de.kittybot.kittybot.command.Category;
import de.kittybot.kittybot.command.Command;
import de.kittybot.kittybot.command.CommandContext;
import de.kittybot.kittybot.exceptions.CommandException;

public class TagDeleteCommand extends Command{

	public TagDeleteCommand(Command parent){
		super(parent, "delete", "Deletes a tags", Category.TAGS);
		addAliases("remove", "del");
		setUsage("<name>");
	}

	@Override
	protected void run(Args args, CommandContext ctx) throws CommandException{
		if(args.isEmpty()){
			ctx.sendUsage(this);
			return;
		}
		ctx.getTagModule().delete(args.get(0), ctx.getUser().getIdLong(), ctx.getGuildId());
		ctx.sendSuccess("Deleted tag");
	}

}
