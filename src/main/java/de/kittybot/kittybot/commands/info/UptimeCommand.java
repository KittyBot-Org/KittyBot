package de.kittybot.kittybot.commands.info;

import de.kittybot.kittybot.slashcommands.CommandContext;
import de.kittybot.kittybot.slashcommands.Options;
import de.kittybot.kittybot.slashcommands.application.options.SubCommand;
import de.kittybot.kittybot.utils.Config;
import de.kittybot.kittybot.utils.TimeUtils;

import java.lang.management.ManagementFactory;

@SuppressWarnings("unused")
public class UptimeCommand extends RunCommand{

	public UptimeCommand(){
		super("uptime", "Shows the bots uptime", Category.INFORMATION);
	}

	@Override
	public void run(Options options, CommandContext ctx){
		ctx.reply(builder -> builder
			.setAuthor("KittyBot Uptime", Config.ORIGIN_URL, ctx.getSelfUser().getEffectiveAvatarUrl())
			.addField("Uptime:", TimeUtils.formatDurationDHMS(ManagementFactory.getRuntimeMXBean().getUptime()), false)
		);
	}

}
