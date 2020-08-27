package de.anteiku.kittybot.commands.info;

import de.anteiku.kittybot.objects.TitleInfo;
import de.anteiku.kittybot.objects.cache.PrefixCache;
import de.anteiku.kittybot.objects.command.ACommand;
import de.anteiku.kittybot.objects.command.Category;
import de.anteiku.kittybot.objects.command.CommandContext;
import de.anteiku.kittybot.objects.command.CommandManager;
import de.anteiku.kittybot.objects.paginator.Paginator;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.util.ArrayList;
import java.util.HashMap;

public class CommandsCommand extends ACommand{

	public static final String COMMAND = "commands";
	public static final String USAGE = "commands <page>";
	public static final String DESCRIPTION = "Lists all available commands";
	protected static final String[] ALIASES = {"cmds"};
	protected static final Category CATEGORY = Category.INFORMATIVE;

	public CommandsCommand(){
		super(COMMAND, USAGE, DESCRIPTION, ALIASES, CATEGORY);
	}

	@Override
	public void run(CommandContext ctx){
		final var titles = new HashMap<Integer, TitleInfo>();
		final var contents = new HashMap<Integer, ArrayList<MessageEmbed.Field>>();

		final var prefix = PrefixCache.getCommandPrefix(ctx.getGuild().getId());
		final var commands = CommandManager.getDistinctCommands().values();
		final var categories = Category.values();
		var c = 0;
		for(final var category : categories){
			titles.put(c, new TitleInfo(category.getEmote() + " " + category.getFriendlyName(), category.getUrl()));

			final var fields = new ArrayList<MessageEmbed.Field>();
			commands.stream()
					.filter(command -> command.getCategory() == category)
					.forEach(cmd -> fields.add(new MessageEmbed.Field("**" + prefix + cmd.getCommand() + ":** ", " :small_blue_diamond:" + cmd.getDescription(), false)));
			contents.put(c, fields);
			c++;
		}
		Paginator.createCommandsPaginator(ctx, categories.length, titles, contents);
	}

}
