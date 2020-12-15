package de.kittybot.kittybot.commands.info;

import de.kittybot.kittybot.command.Category;
import de.kittybot.kittybot.command.Command;
import de.kittybot.kittybot.command.ctx.CommandContext;
import de.kittybot.kittybot.main.KittyBot;
import de.kittybot.kittybot.utils.MessageUtils;
import de.kittybot.kittybot.utils.Utils;
import net.dv8tion.jda.api.EmbedBuilder;

import java.lang.management.ManagementFactory;
import java.util.List;

public class UptimeCommand extends Command{

	private final KittyBot main;

	public UptimeCommand(KittyBot main){
		super("uptime", "Shows the bots uptime", Category.INFORMATION);
		this.main = main;
	}

	@Override
	public void run(List<String> args, CommandContext ctx){
		var jda = ctx.getJDA();
		ctx.sendSuccess(new EmbedBuilder()
				.setAuthor("KittyBot Uptime", this.main.getConfig().getString("origin_url"), jda.getSelfUser().getEffectiveAvatarUrl())

				.addField("Uptime:", MessageUtils.formatDurationDHMS(ManagementFactory.getRuntimeMXBean().getUptime()), false)
		);
	}


}
