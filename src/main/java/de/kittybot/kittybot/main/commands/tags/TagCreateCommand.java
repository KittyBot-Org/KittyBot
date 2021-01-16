package de.kittybot.kittybot.main.commands.tags;

import de.kittybot.kittybot.command.old.Args;
import de.kittybot.kittybot.command.Category;
import de.kittybot.kittybot.command.old.Command;
import de.kittybot.kittybot.command.old.CommandContext;
import de.kittybot.kittybot.exceptions.CommandException;
import de.kittybot.kittybot.modules.TagsModule;

@SuppressWarnings("unused")
public class TagCreateCommand extends Command{

	public TagCreateCommand(Command parent){
		super(parent, "create", "Creates a tags", Category.TAGS);
		addAliases("new", "neu", "add");
		setUsage("<name> <content>");
	}

	@Override
	protected void run(Args args, CommandContext ctx) throws CommandException{
		if(args.size() < 2){
			ctx.sendUsage(this);
			return;
		}
		ctx.get(TagsModule.class).create(args.get(0), ctx.getRawMessage(1), ctx.getGuildId(), ctx.getUser().getIdLong());
		ctx.sendSuccess("Tag created");
	}

}
