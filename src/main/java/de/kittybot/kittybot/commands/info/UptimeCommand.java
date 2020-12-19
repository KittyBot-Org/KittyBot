package de.kittybot.kittybot.commands.info;

import de.kittybot.kittybot.command.Category;
import de.kittybot.kittybot.command.Command;
import de.kittybot.kittybot.command.ctx.CommandContext;
import de.kittybot.kittybot.utils.MessageUtils;
import net.dv8tion.jda.api.EmbedBuilder;

import java.lang.management.ManagementFactory;
import java.util.List;

public class UptimeCommand extends Command{

	public UptimeCommand(){
		super("uptime", "Shows the bots uptime", Category.INFORMATION);
	}

	@Override
	public void run(List<String> args, CommandContext ctx){
		var jda = ctx.getJDA();
		ctx.sendSuccess(new EmbedBuilder()
				.setAuthor("KittyBot Uptime", ctx.getConfig().getString("origin_url"), jda.getSelfUser().getEffectiveAvatarUrl())

				.addField("Uptime:", MessageUtils.formatDurationDHMS(ManagementFactory.getRuntimeMXBean().getUptime()), false)
		);
	}


}
