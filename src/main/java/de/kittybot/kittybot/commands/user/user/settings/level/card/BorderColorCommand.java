package de.kittybot.kittybot.commands.user.user.settings.level.card;

import de.kittybot.kittybot.modules.UserSettingsModule;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionUrl;
import de.kittybot.kittybot.slashcommands.application.options.SubCommand;
import de.kittybot.kittybot.slashcommands.interaction.Interaction;
import de.kittybot.kittybot.slashcommands.interaction.Options;
import de.kittybot.kittybot.utils.ImageUtils;
import de.kittybot.kittybot.utils.Colors;
import net.dv8tion.jda.api.EmbedBuilder;

import static de.kittybot.kittybot.jooq.Tables.USER_SETTINGS;

public class BorderColorCommand extends SubCommand{

	public BorderColorCommand(){
		super("border-color", "Lets you set the border color of your level card");
		addOptions(
			new CommandOptionUrl("color", "The border color of level card")
		);
	}

	@Override
	public void run(Options options, Interaction ia){
		var color = options.getColor("color");
		ia.get(UserSettingsModule.class).setUserSetting(ia.getUserId(), USER_SETTINGS.LEVEL_CARD_BACKGROUND_COLOR, color.getRGB());
		ia.sendAcknowledge();
		ia.getChannel().sendMessage(ia.applyDefaultStyle(new EmbedBuilder()
				.setColor(Colors.KITTYBOT_BLUE)
				.setDescription("Set border color to")
				.setThumbnail("attachment://color.png")
			).build()
		).addFile(ImageUtils.generateColorImage(color), "color.png").queue();
	}

}
