package de.kittybot.kittybot.commands.info;

import de.kittybot.kittybot.objects.enums.Emoji;
import de.kittybot.kittybot.slashcommands.application.Category;
import de.kittybot.kittybot.slashcommands.application.RunCommand;
import de.kittybot.kittybot.slashcommands.interaction.Interaction;
import de.kittybot.kittybot.slashcommands.interaction.Options;
import de.kittybot.kittybot.utils.Colors;
import de.kittybot.kittybot.utils.Config;
import net.dv8tion.jda.api.EmbedBuilder;

@SuppressWarnings("unused")
public class HelpCommand extends RunCommand{

	public HelpCommand(){
		super("help", "Shows help", Category.INFORMATION);
	}

	@Override
	public void run(Options options, Interaction ia){
		ia.reply(new EmbedBuilder()
			.setColor(Colors.KITTYBOT_BLUE)
			.setAuthor("Help", Config.ORIGIN_URL, ia.getSelfUser().getEffectiveAvatarUrl())
			.setDescription(
				"Hello " + ia.getUser().getAsMention() + "\n" +
					"KittyBot uses the new " + Emoji.SLASH.get() + " Slash Commands by Discord!\n" +
					"To see all available commands just type `/` or use `/commands`"
			)
		);
	}

}
