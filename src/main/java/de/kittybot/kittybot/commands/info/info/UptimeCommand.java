package de.kittybot.kittybot.commands.info.info;

import de.kittybot.kittybot.slashcommands.application.options.SubCommand;
import de.kittybot.kittybot.slashcommands.interaction.Interaction;
import de.kittybot.kittybot.slashcommands.interaction.Options;
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
	public void run(Options options, Interaction ia){
		ia.reply(builder -> builder
			.setAuthor("KittyBot Uptime", Config.ORIGIN_URL, ia.getSelfUser().getEffectiveAvatarUrl())
			.addField("Uptime:", TimeUtils.formatDurationDHMS(ManagementFactory.getRuntimeMXBean().getUptime()), false)
		);
	}

}
