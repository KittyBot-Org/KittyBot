package de.kittybot.kittybot.commands.user.user.stats;

import de.kittybot.kittybot.modules.StatsModule;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionUser;
import de.kittybot.kittybot.slashcommands.application.options.GuildSubCommand;
import de.kittybot.kittybot.slashcommands.interaction.GuildInteraction;
import de.kittybot.kittybot.slashcommands.interaction.Options;
import de.kittybot.kittybot.utils.MessageUtils;
import de.kittybot.kittybot.utils.TimeUtils;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.Color;
import java.util.function.Consumer;

public class UserCommand extends GuildSubCommand{

	public UserCommand(){
		super("user", "Shows user stats");
		addOptions(
			new CommandOptionUser("user", "The user to display stats")
		);
	}

	@Override
	public void run(Options options, GuildInteraction ia){
		var userId = options.getOrDefault("user", ia.getUserId());
		var stats = ia.get(StatsModule.class).get(ia.getGuildId(), userId);
		ia.reply(builder ->
			builder.setDescription("User Stats: " + MessageUtils.getUserMention(userId) +
				"\n**Level:** " + stats.getLevel() +
				"\n**XP:** " + stats.getRestXp() + "/" + stats.getNeededXp() +
				"\n**Total Earned XP:** " + stats.getXp() +
				"\n**Commands Used:** " + stats.getCommandsUsed() +
				"\n**Total Voice Time:** " + TimeUtils.formatDurationDHMS(stats.getVoiceTime()) +
				"\n**Total Stream Time:** " + TimeUtils.formatDurationDHMS(stats.getStreamTime()) +
				"\n**Messages Sent:** " + stats.getMessagesSent() +
				"\n**Emotes Sent:** " + stats.getEmotesSent() +
				"\n**Stickers Sent:** " + stats.getStickersSent() +
				"\n**Last Time Active:** " + TimeUtils.format(stats.getLastActive())
			)
		);
	}

}