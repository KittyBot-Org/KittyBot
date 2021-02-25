package de.kittybot.kittybot.commands.statistics.user.settings.level.card;

import de.kittybot.kittybot.modules.UserSettingsModule;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionColor;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionUrl;
import de.kittybot.kittybot.slashcommands.application.options.SubCommand;
import de.kittybot.kittybot.slashcommands.interaction.Interaction;
import de.kittybot.kittybot.slashcommands.interaction.Options;
import de.kittybot.kittybot.slashcommands.interaction.response.InteractionResponseData;
import de.kittybot.kittybot.utils.ColorUtils;
import de.kittybot.kittybot.utils.Colors;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;

import static de.kittybot.kittybot.jooq.Tables.USER_SETTINGS;

public class BackgroundColorCommand extends SubCommand{

	public BackgroundColorCommand(){
		super("background-color", "Lets you set the background color of your level card");
		addOptions(
			new CommandOptionColor("color", "The background color of level card background")
		);
	}

	@Override
	public void run(Options options, Interaction ia){
		var color = options.getColor("color");
		ia.get(UserSettingsModule.class).setUserSetting(ia.getUserId(), USER_SETTINGS.LEVEL_CARD_BACKGROUND_COLOR, color.getRGB());
		ia.sendAcknowledge();
		ia.getChannel().sendMessage(ia.applyDefaultStyle(new EmbedBuilder()
				.setColor(Colors.KITTYBOT_BLUE)
				.setDescription("Set background color to")
				.setThumbnail("attachment://color.png")
			).build()
		).addFile(ColorUtils.generateColorImage(color), "color.png").queue();
	}

}
