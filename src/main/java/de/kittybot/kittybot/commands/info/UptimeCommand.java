package de.kittybot.kittybot.commands.info;

import de.kittybot.kittybot.slashcommands.application.Category;
import de.kittybot.kittybot.slashcommands.application.RunCommand;
import de.kittybot.kittybot.slashcommands.interaction.Interaction;
import de.kittybot.kittybot.slashcommands.interaction.Options;
import de.kittybot.kittybot.utils.Config;
import de.kittybot.kittybot.utils.TimeUtils;

import java.lang.management.ManagementFactory;

@SuppressWarnings("unused")
public class UptimeCommand extends RunCommand{

	public UptimeCommand(){
		super("uptime", "Shows the bots uptime", Category.INFORMATION);
	}

	@Override
	public void run(Options options, Interaction ia){
		ia.reply(builder -> builder
			.setAuthor("KittyBot Uptime", Config.ORIGIN_URL, ia.getSelfUser().getEffectiveAvatarUrl())
			.addField("Uptime:", TimeUtils.formatDurationDHMS(ManagementFactory.getRuntimeMXBean().getUptime()), false)
		);
	}

}
