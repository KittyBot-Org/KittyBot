package de.kittybot.kittybot.commands.user.user.stats;

import de.kittybot.kittybot.modules.StatsModule;
import de.kittybot.kittybot.objects.enums.StatisticType;
import de.kittybot.kittybot.slashcommands.application.CommandOptionChoice;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionString;
import de.kittybot.kittybot.slashcommands.application.options.GuildSubCommand;
import de.kittybot.kittybot.slashcommands.interaction.GuildInteraction;
import de.kittybot.kittybot.slashcommands.interaction.Options;
import de.kittybot.kittybot.utils.MessageUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import org.jooq.SortOrder;

import java.util.stream.Collectors;

public class TopCommand extends GuildSubCommand{

	public TopCommand(){
		super("top", "Shows top stats");
		addOptions(
			new CommandOptionString("stat", "The stats to sort for xp by default")
				.addChoices(
					new CommandOptionChoice<>("xp", StatisticType.XP),
					new CommandOptionChoice<>("bot calls", StatisticType.COMMANDS_USED),
					new CommandOptionChoice<>("voice time", StatisticType.VOICE_TIME),
					new CommandOptionChoice<>("messages", StatisticType.MESSAGES_SENT),
					new CommandOptionChoice<>("emotes", StatisticType.EMOTES_SENT),
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
		ia.reply(builder -> builder
			.setTitle("Top 10 Statistics for `" + type + "`")
			.setDescription(statistics.stream().map(statistic -> MessageUtils.getUserMention(statistic.getUserId()) + " - `" + statistic.get(type) + "`").collect(Collectors.joining("\n")))
		);
	}

}
