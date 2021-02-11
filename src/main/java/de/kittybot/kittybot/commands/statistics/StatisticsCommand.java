package de.kittybot.kittybot.commands.statistics;

import de.kittybot.kittybot.modules.StatsModule;
import de.kittybot.kittybot.objects.enums.StatisticType;
import de.kittybot.kittybot.slashcommands.application.Category;
import de.kittybot.kittybot.slashcommands.application.Command;
import de.kittybot.kittybot.slashcommands.application.CommandOptionChoice;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionString;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionUser;
import de.kittybot.kittybot.slashcommands.application.options.SubCommand;
import de.kittybot.kittybot.slashcommands.context.CommandContext;
import de.kittybot.kittybot.slashcommands.context.Options;
import de.kittybot.kittybot.utils.MessageUtils;
import de.kittybot.kittybot.utils.TimeUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import org.jooq.SortOrder;

import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class StatisticsCommand extends Command{

	public StatisticsCommand(){
		super("stats", "Shows Stats", Category.STATISTICS);
		addOptions(
			new UserCommand(),
			new TopCommand(),
			new XPCommand()
		);
	}

	private static class TopCommand extends SubCommand{

		public TopCommand(){
			super("top", "Shows top stats");
			addOptions(
				new CommandOptionString("stat", "The stats to sort for xp by default")
					.addChoices(
						new CommandOptionChoice<>("xp", StatisticType.XP),
						new CommandOptionChoice<>("bot calls", StatisticType.BOT_CALLS),
						new CommandOptionChoice<>("voice time", StatisticType.VOICE_TIME),
						new CommandOptionChoice<>("messages", StatisticType.MESSAGE_COUNT),
						new CommandOptionChoice<>("emotes", StatisticType.EMOTE_COUNT),
						new CommandOptionChoice<>("last active", StatisticType.LAST_ACTIVE)
					),
				new CommandOptionString("sort-order", "The sort order descending by default")
					.addChoices(
						new CommandOptionChoice<>("ascending", SortOrder.ASC),
						new CommandOptionChoice<>("descending", SortOrder.DESC)
					)
			);
		}

		@Override
		public void run(Options options, CommandContext ctx){
			var type = StatisticType.valueOf(options.getOrDefault("stat", StatisticType.XP.name()));
			var sortOrder = SortOrder.valueOf(options.getOrDefault("sort-order", SortOrder.DESC.name()));
			var statistics = ctx.get(StatsModule.class).get(ctx.getGuildId(), type, sortOrder, 10);
			ctx.reply(new EmbedBuilder()
				.setTitle("Top 10 Statistics for `" + type + "`")
				.setDescription(statistics.stream().map(statistic -> MessageUtils.getUserMention(statistic.getUserId()) + " - `" + statistic.get(type) + "`").collect(Collectors.joining("\n")))
			);
		}

	}

	private static class UserCommand extends SubCommand{

		public UserCommand(){
			super("user", "Shows user stats");
			addOptions(
				new CommandOptionUser("user", "The user to display stats")
			);
		}

		@Override
		public void run(Options options, CommandContext ctx){
			var userId = options.getOrDefault("user", ctx.getUserId());
			var statistics = ctx.get(StatsModule.class).get(ctx.getGuildId(), userId);
			ctx.reply(new EmbedBuilder()
				.setDescription("User Statistics: " + MessageUtils.getUserMention(userId) +
					"\n**Level:** " + statistics.getLevel() +
					"\n**XP:** " + statistics.getRestXp() +
					"\n**Bot Calls:** " + statistics.getBotCalls() +
					"\n**Total Voice Time:** " + TimeUtils.formatDurationDHMS(statistics.getVoiceTime()) +
					"\n**Messages Sent:** " + statistics.getMessageCount() +
					"\n**Emotes Sent:** " + statistics.getEmoteCount() +
					"\n**Last Time Active:** " + TimeUtils.format(statistics.getLastActive())
				)
			);
		}

	}

	private static class XPCommand extends SubCommand{

		public XPCommand(){
			super("xp", "Shows user stats");
		}

		@Override
		public void run(Options options, CommandContext ctx){
			var statistics = ctx.get(StatsModule.class).get(ctx.getGuildId(), ctx.getUserId());
			var level = statistics.getLevel();
			ctx.reply(new EmbedBuilder()
				.setDescription("**Level:** " + statistics.getLevel() +
					"\n**XP:** `" + statistics.getXp() + "/" + statistics.getRequiredXp(level + 1) + "`"
				)
			);
		}

	}

}
