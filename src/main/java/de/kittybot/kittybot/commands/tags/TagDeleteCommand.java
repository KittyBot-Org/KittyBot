package de.kittybot.kittybot.commands.tags;

import de.kittybot.kittybot.command.Args;
import de.kittybot.kittybot.command.Category;
import de.kittybot.kittybot.command.Command;
import de.kittybot.kittybot.command.context.CommandContext;
import de.kittybot.kittybot.exceptions.CommandException;
import de.kittybot.kittybot.modules.TagModule;

@SuppressWarnings("unused")
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
		ctx.get(TagModule.class).delete(args.get(0), ctx.getGuildId(), ctx.getUserId());
		ctx.sendSuccess("Tag deleted");
	}

}
