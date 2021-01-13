package de.kittybot.kittybot.commands.info;

import de.kittybot.kittybot.command.Args;
import de.kittybot.kittybot.command.Category;
import de.kittybot.kittybot.command.Command;
import de.kittybot.kittybot.command.context.CommandContext;
import de.kittybot.kittybot.utils.Config;
import de.kittybot.kittybot.utils.TimeUtils;
import net.dv8tion.jda.api.EmbedBuilder;

import java.lang.management.ManagementFactory;

@SuppressWarnings("unused")
public class UptimeCommand extends Command{

	public UptimeCommand(){
		super("uptime", "Shows the bots uptime", Category.INFORMATION);
	}

	@Override
	public void run(Args args, CommandContext ctx){
		var jda = ctx.getJDA();
		ctx.sendSuccess(new EmbedBuilder()
				.setAuthor("KittyBot Uptime", Config.ORIGIN_URL, jda.getSelfUser().getEffectiveAvatarUrl())

				.addField("Uptime:", TimeUtils.formatDurationDHMS(ManagementFactory.getRuntimeMXBean().getUptime()), false)
		);
	}


}
