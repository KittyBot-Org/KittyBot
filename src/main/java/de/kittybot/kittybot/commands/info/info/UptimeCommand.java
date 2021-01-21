package de.kittybot.kittybot.commands.info.info;

import de.kittybot.kittybot.slashcommands.application.Category;
import de.kittybot.kittybot.slashcommands.application.Command;
import de.kittybot.kittybot.slashcommands.application.RunnableCommand;
import de.kittybot.kittybot.slashcommands.application.options.SubCommand;
import de.kittybot.kittybot.slashcommands.context.CommandContext;
import de.kittybot.kittybot.slashcommands.context.Options;
import de.kittybot.kittybot.utils.Colors;
import de.kittybot.kittybot.utils.Config;
import de.kittybot.kittybot.utils.TimeUtils;
import net.dv8tion.jda.api.EmbedBuilder;

import java.lang.management.ManagementFactory;

@SuppressWarnings("unused")
public class UptimeCommand extends SubCommand{

	public UptimeCommand(){
		super("uptime", "Shows the bots uptime");
	}

	@Override
	public void run(Options options, CommandContext ctx){
		var jda = ctx.getJDA();
		ctx.reply(new EmbedBuilder()
			.setColor(Colors.KITTYBOT_BLUE)
			.setAuthor("KittyBot Uptime", Config.ORIGIN_URL, jda.getSelfUser().getEffectiveAvatarUrl())

			.addField("Uptime:", TimeUtils.formatDurationDHMS(ManagementFactory.getRuntimeMXBean().getUptime()), false)
		);
	}

}
