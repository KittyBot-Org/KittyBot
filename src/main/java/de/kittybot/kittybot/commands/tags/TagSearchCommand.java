package de.kittybot.kittybot.commands.tags;

import de.kittybot.kittybot.command.Args;
import de.kittybot.kittybot.command.Category;
import de.kittybot.kittybot.command.Command;
import de.kittybot.kittybot.command.ctx.CommandContext;
import de.kittybot.kittybot.exceptions.CommandException;
import de.kittybot.kittybot.objects.Tag;

import java.util.List;
import java.util.stream.Collectors;

public class TagSearchCommand extends Command{

	public TagSearchCommand(Command parent){
		super(parent, "search", "Searches for tags", Category.TAGS);
		addAliases("s");
		setUsage("<name>");
	}

	@Override
	protected void run(Args args, CommandContext ctx) throws CommandException{
		if(args.isEmpty()){
			ctx.sendUsage(this);
			return;
		}
		var tags = ctx.getTagManager().search(args.get(0), ctx.getGuildId());
		ctx.sendSuccess("Tags found:\n" + tags.stream().map(Tag::getName).collect(Collectors.joining("\n")));
	}

}
