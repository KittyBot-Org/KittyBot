package de.kittybot.kittybot.main.commands.tags;

import de.kittybot.kittybot.command.old.Args;
import de.kittybot.kittybot.command.Category;
import de.kittybot.kittybot.command.old.Command;
import de.kittybot.kittybot.command.old.CommandContext;
import de.kittybot.kittybot.exceptions.CommandException;
import de.kittybot.kittybot.modules.TagsModule;

@SuppressWarnings("unused")
public class TagEditCommand extends Command{

	public TagEditCommand(Command parent){
		super(parent, "edit", "Edits a tags", Category.TAGS);
		addAliases("e");
	}

	@Override
	protected void run(Args args, CommandContext ctx) throws CommandException{
		if(args.size() < 2){
			ctx.sendUsage(this);
			return;
		}
		ctx.get(TagsModule.class).edit(args.get(0), ctx.getRawMessage(1), ctx.getGuildId(), ctx.getUserId());
		ctx.sendSuccess("Tag edited");
	}

}
