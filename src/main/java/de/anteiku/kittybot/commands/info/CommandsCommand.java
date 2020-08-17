package de.anteiku.kittybot.commands.info;

import de.anteiku.kittybot.objects.command.ACommand;
import de.anteiku.kittybot.objects.command.Category;
import de.anteiku.kittybot.objects.command.CommandContext;
import de.anteiku.kittybot.objects.command.CommandManager;
import de.anteiku.kittybot.objects.paginator.Paginator;

import java.util.HashMap;

public class CommandsCommand extends ACommand{

	public static final String COMMAND = "commands";
	public static final String USAGE = "commands <page>";
	public static final String DESCRIPTION = "Lists all available commands";
	protected static final String[] ALIAS = {"cmds"};
	protected static final Category CATEGORY = Category.INFORMATIVE;

	public CommandsCommand(){
		super(COMMAND, USAGE, DESCRIPTION, ALIAS, CATEGORY);
	}

	@Override
	public void run(CommandContext ctx){
		final var authors = new HashMap<Integer, String>();
		final var contents = new HashMap<Integer, String>();

		final var commands = CommandManager.getCommands().values();
		final var categories = Category.values();
		var c = 0;
		for (final var category : categories){
			authors.put(c, category.getFriendlyName());

			final var contentsBuilder = new StringBuilder();
			commands.stream().distinct().filter(command -> command.getCategory() == category).forEach(cmd ->
					contentsBuilder.append("command ").append(cmd.getCommand()).append("\n").append(cmd.getDescription()).append("\n\n"));
			contents.put(c, contentsBuilder.toString());
			c++;
		}
		Paginator.createPaginator(ctx.getChannel(), ctx.getMessage(), contents, authors, categories.length);
	}
}
