package de.kittybot.kittybot.commands.info;

import de.kittybot.kittybot.objects.Emoji;
import de.kittybot.kittybot.slashcommands.application.Category;
import de.kittybot.kittybot.slashcommands.application.Command;
import de.kittybot.kittybot.slashcommands.application.RunnableCommand;
import de.kittybot.kittybot.slashcommands.context.CommandContext;
import de.kittybot.kittybot.slashcommands.context.Options;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionString;
import de.kittybot.kittybot.slashcommands.interaction.response.InteractionResponse;
import de.kittybot.kittybot.slashcommands.interaction.response.InteractionResponseType;
import de.kittybot.kittybot.modules.CommandsModule;
import de.kittybot.kittybot.modules.PaginatorModule;
import de.kittybot.kittybot.utils.Colors;
import de.kittybot.kittybot.utils.Config;
import de.kittybot.kittybot.utils.MessageUtils;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.Color;
import java.time.Instant;
import java.util.ArrayList;
import java.util.stream.Collectors;

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
								"KittyBot uses the new " + Emoji.SLASH.get() + "Slash Commands by Discord!" +
								"To see all available commands just type `/` or use `/commands`"
				)
		);
	}

}
