package de.kittybot.kittybot.commands.statistics;

import de.kittybot.kittybot.modules.StatsModule;
import de.kittybot.kittybot.modules.UserSettingsModule;
import de.kittybot.kittybot.objects.enums.StatisticType;
import de.kittybot.kittybot.slashcommands.application.Category;
import de.kittybot.kittybot.slashcommands.application.Command;
import de.kittybot.kittybot.slashcommands.application.CommandOptionChoice;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionString;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionUser;
import de.kittybot.kittybot.slashcommands.application.options.GuildSubCommand;
import de.kittybot.kittybot.slashcommands.interaction.GuildInteraction;
import de.kittybot.kittybot.slashcommands.interaction.Options;
import de.kittybot.kittybot.utils.MessageUtils;
import de.kittybot.kittybot.utils.TimeUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import org.jooq.SortOrder;

import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class StatisticsCommand extends Command{

	public StatisticsCommand(){
		super("stats", "Shows Stats", Category.STATISTICS);
		addOptions(
			new UserCommand(),
			new TopCommand(),
			new XPCommand(),
			new CardCommand()
		);
	}

	private static class TopCommand extends GuildSubCommand{

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
		public void run(Options options, GuildInteraction ia){
			var type = StatisticType.valueOf(options.getOrDefault("stat", StatisticType.XP.name()));
			var sortOrder = SortOrder.valueOf(options.getOrDefault("sort-order", SortOrder.DESC.name()));
			var statistics = ia.get(StatsModule.class).get(ia.getGuildId(), type, sortOrder, 10);
			ia.reply(new EmbedBuilder()
				.setTitle("Top 10 Statistics for `" + type + "`")
				.setDescription(statistics.stream().map(statistic -> MessageUtils.getUserMention(statistic.getUserId()) + " - `" + statistic.get(type) + "`").collect(Collectors.joining("\n")))
			);
		}

	}

	private static class UserCommand extends GuildSubCommand{

		public UserCommand(){
			super("user", "Shows user stats");
			addOptions(
				new CommandOptionUser("user", "The user to display stats")
			);
		}

		@Override
		public void run(Options options, GuildInteraction ia){
			var userId = options.getOrDefault("user", ia.getUserId());
			var statistics = ia.get(StatsModule.class).get(ia.getGuildId(), userId);
			ia.reply(new EmbedBuilder()
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

	private static class CardCommand extends GuildSubCommand{


		public CardCommand(){
			super("card", "Sends your level card");
			addOptions(
				new CommandOptionUser("user", "The user to get the card from")
			);
		}

		@Override
		public void run(Options options, GuildInteraction ia){
			var statsModule = ia.get(StatsModule.class);
			var userStats = statsModule.get(ia.getGuildId(), ia.getUserId());
			var userSettings = ia.get(UserSettingsModule.class).getUserSettings(ia.getUserId());
			var card = statsModule.generateLevelCard(userStats, userSettings, options.has("user") ? options.getUser("user") : ia.getUser());
			ia.sendAcknowledge();
			ia.getChannel().sendFile(card, "card.png").queue();
		}

	}

	private static class XPCommand extends GuildSubCommand{

		public XPCommand(){
			super("xp", "Shows user stats");
		}

		@Override
		public void run(Options options, GuildInteraction ia){
			var statistics = ia.get(StatsModule.class).get(ia.getGuildId(), ia.getUserId());
			var level = statistics.getLevel();
			ia.reply(new EmbedBuilder()
				.setDescription("**Level:** " + statistics.getLevel() +
					"\n**XP:** `" + statistics.getXp() + "/" + statistics.getRequiredXp(level + 1) + "`"
				)
			);
		}

	}

}
