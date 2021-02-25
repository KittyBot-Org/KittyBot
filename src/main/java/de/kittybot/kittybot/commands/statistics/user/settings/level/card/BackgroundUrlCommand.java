package de.kittybot.kittybot.commands.statistics.user.settings.level.card;

import de.kittybot.kittybot.modules.UserSettingsModule;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionUrl;
import de.kittybot.kittybot.slashcommands.application.options.SubCommand;
import de.kittybot.kittybot.slashcommands.interaction.Interaction;
import de.kittybot.kittybot.slashcommands.interaction.Options;
import de.kittybot.kittybot.utils.Colors;
import net.dv8tion.jda.api.EmbedBuilder;

import static de.kittybot.kittybot.jooq.Tables.USER_SETTINGS;

public class BackgroundUrlCommand extends SubCommand{

	public BackgroundUrlCommand(){
		super("background-image", "Lets you set the background image of your level card");
		addOptions(
			new CommandOptionUrl("url", "The url to your level card background")
		);
	}

	@Override
	public void run(Options options, Interaction ia){
		var url = options.getString("url");
		ia.get(UserSettingsModule.class).setUserSetting(ia.getUserId(), USER_SETTINGS.LEVEL_CARD_BACKGROUND_URL, url);
		ia.reply(new EmbedBuilder()
			.setColor(Colors.KITTYBOT_BLUE)
			.setDescription("Set background url to")
			.setImage(url));
	}

}
