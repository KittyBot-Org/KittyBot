package de.kittybot.kittybot.commands.info;

import de.kittybot.kittybot.objects.enums.Emoji;
import de.kittybot.kittybot.slashcommands.application.Category;
import de.kittybot.kittybot.slashcommands.application.Command;
import de.kittybot.kittybot.slashcommands.application.RunnableCommand;
import de.kittybot.kittybot.slashcommands.context.CommandContext;
import de.kittybot.kittybot.slashcommands.context.Options;
import de.kittybot.kittybot.utils.Colors;
import de.kittybot.kittybot.utils.Config;
import net.dv8tion.jda.api.EmbedBuilder;

@SuppressWarnings("unused")
public class HelpCommand extends Command implements RunnableCommand{

	public HelpCommand(){
		super("help", "Shows help", Category.INFORMATION);
	}

	@Override
	public void run(Options options, CommandContext ctx){
		ctx.reply(new EmbedBuilder()
			.setColor(Colors.KITTYBOT_BLUE)
			.setAuthor("Help", Config.ORIGIN_URL, ctx.getSelfUser().getEffectiveAvatarUrl())
			.setDescription(
				"Hello " + ctx.getMember().getAsMention() + "\n" +
					"KittyBot uses the new " + Emoji.SLASH.get() + " Slash Commands by Discord!\n" +
					"To see all available commands just type `/` or use `/commands`"
			)
		);
	}

}
