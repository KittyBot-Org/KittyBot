package de.kittybot.kittybot.commands.user.user.stats;

import de.kittybot.kittybot.modules.StatsModule;
import de.kittybot.kittybot.modules.UserSettingsModule;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionUser;
import de.kittybot.kittybot.slashcommands.application.options.GuildSubCommand;
import de.kittybot.kittybot.slashcommands.interaction.GuildInteraction;
import de.kittybot.kittybot.slashcommands.interaction.Options;
import de.kittybot.kittybot.utils.Colors;
import de.kittybot.kittybot.utils.ImageUtils;
import net.dv8tion.jda.api.EmbedBuilder;

public class CardCommand extends GuildSubCommand{


	public CardCommand(){
		super("card", "Sends your level card");
		addOptions(
			new CommandOptionUser("user", "The user to get the card from")
		);
	}

	@Override
	public void run(Options options, GuildInteraction ia){
		var userStats = ia.get(StatsModule.class).get(ia.getGuildId(), ia.getUserId());
		var userSettings = ia.get(UserSettingsModule.class).getUserSettings(ia.getUserId());
		var card = ImageUtils.generateLevelCard(userStats, userSettings, options.has("user") ? options.getUser("user") : ia.getUser());
		ia.acknowledge(true).queue(success ->
			ia.getChannel().sendFile(card, "card.png").queue()
		);
	}

}