package de.kittybot.kittybot.commands.info;

import de.kittybot.kittybot.commands.info.info.*;
import de.kittybot.kittybot.slashcommands.application.Category;
import de.kittybot.kittybot.slashcommands.application.Command;
import de.kittybot.kittybot.slashcommands.application.RunnableCommand;
import de.kittybot.kittybot.slashcommands.context.CommandContext;
import de.kittybot.kittybot.slashcommands.context.Options;
import de.kittybot.kittybot.utils.Colors;
import de.kittybot.kittybot.utils.Config;
import de.kittybot.kittybot.utils.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDAInfo;

import java.lang.management.ManagementFactory;
import java.time.Instant;

@SuppressWarnings("unused")
public class InfoCommand extends Command{

	public InfoCommand(){
		super("info", "Shows bot info", Category.INFORMATION);
		addOptions(
			new BotCommand(),
			new UptimeCommand(),
			new AvatarCommand(),
			new GuildIconCommand(),
			new GuildBannerCommand()
		);
	}

}
