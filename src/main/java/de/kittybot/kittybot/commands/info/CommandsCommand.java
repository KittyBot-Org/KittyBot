package de.kittybot.kittybot.commands.info;

import de.kittybot.kittybot.cache.GuildSettingsCache;
import de.kittybot.kittybot.objects.Emojis;
import de.kittybot.kittybot.objects.TitleInfo;
import de.kittybot.kittybot.objects.command.ACommand;
import de.kittybot.kittybot.objects.command.Category;
import de.kittybot.kittybot.objects.command.CommandContext;
import de.kittybot.kittybot.objects.command.CommandManager;
import de.kittybot.kittybot.objects.paginator.Paginator;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.*;
import java.time.Instant;
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
		final var channel = ctx.getChannel();
		final var message = ctx.getMessage();
		final var selfMember = channel.getGuild().getSelfMember();
		if(!channel.canTalk()){
			if(selfMember.hasPermission(channel, Permission.MESSAGE_ADD_REACTION, Permission.MESSAGE_HISTORY)){
				message.addReaction(Emojis.X).queue();
			}
			return;
		}
		if(!selfMember.hasPermission(channel, Permission.MESSAGE_HISTORY) || !selfMember.hasPermission(channel, Permission.MESSAGE_ADD_REACTION) || !selfMember.hasPermission(channel, Permission.MESSAGE_MANAGE)){
			channel.sendMessage(new EmbedBuilder().setColor(Color.RED)
					.addField("Error:", "I'm missing required permissions for paginator to work. Ensure that i can read the message history, add reactions and manage messages in this channel.", true)
					.setFooter(ctx.getMember().getEffectiveName(), ctx.getUser().getEffectiveAvatarUrl())
					.setTimestamp(Instant.now())
					.build()).queue(); // TODO improve checks
			return;
		}
		final var titles = new HashMap<Integer, TitleInfo>();
		final var contents = new HashMap<Integer, ArrayList<MessageEmbed.Field>>();

		final var prefix = GuildSettingsCache.getCommandPrefix(ctx.getGuild().getId());
		final var commands = CommandManager.getDistinctCommands().values();
		final var categories = Category.values();
		var c = 0;
		for(final var category : categories){
			titles.put(c, new TitleInfo(category.getEmote() + " " + category.getFriendlyName(), category.getUrl()));

			final var fields = new ArrayList<MessageEmbed.Field>();
			commands.stream()
					.filter(command -> command.getCategory() == category)
					.forEach(cmd -> fields.add(new MessageEmbed.Field("**" + prefix + cmd.getCommand() + ":** ", "• " + cmd.getDescription(), true)));
			contents.put(c, fields);
			c++;
		}
		Paginator.createCommandsPaginator(message, categories.length, titles, contents);
	}

}
