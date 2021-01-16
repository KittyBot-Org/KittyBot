package de.kittybot.kittybot.main.commands.tags;

import de.kittybot.kittybot.command.old.Args;
import de.kittybot.kittybot.command.Category;
import de.kittybot.kittybot.command.old.Command;
import de.kittybot.kittybot.command.old.CommandContext;
import de.kittybot.kittybot.exceptions.CommandException;
import de.kittybot.kittybot.modules.TagsModule;
import de.kittybot.kittybot.objects.Tag;

import java.util.stream.Collectors;

@SuppressWarnings("unused")
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
		var tags = ctx.get(TagsModule.class).search(args.get(0), ctx.getGuildId());
		if(tags.isEmpty()){
			ctx.error("No tags found for '" + args.get(0) + "'");
			return;
		}
		ctx.sendSuccess("**Tags found:**\n" + tags.stream().map(Tag::getName).collect(Collectors.joining("\n")));
	}

}