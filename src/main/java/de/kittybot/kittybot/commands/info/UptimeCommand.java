package de.kittybot.kittybot.commands.info;

import de.kittybot.kittybot.objects.Config;
import de.kittybot.kittybot.objects.command.ACommand;
import de.kittybot.kittybot.objects.command.Category;
import de.kittybot.kittybot.objects.command.CommandContext;
import de.kittybot.kittybot.utils.Utils;
import net.dv8tion.jda.api.EmbedBuilder;

import java.lang.management.ManagementFactory;

public class UptimeCommand extends ACommand{

	public static final String COMMAND = "uptime";
	public static final String USAGE = "uptime";
	public static final String DESCRIPTION = "Shows the bots uptime";
	protected static final String[] ALIASES = {};
	protected static final Category CATEGORY = Category.INFORMATIVE;

	public UptimeCommand(){
		super(COMMAND, USAGE, DESCRIPTION, ALIASES, CATEGORY);
	}

	@Override
	public void run(CommandContext ctx){
		var jda = ctx.getJDA();
		sendAnswer(ctx, new EmbedBuilder()
			                .setAuthor("KittyBot Uptime", Config.ORIGIN_URL, jda.getSelfUser().getEffectiveAvatarUrl())

			                .addField("Uptime:", Utils.formatDurationDHMS(ManagementFactory.getRuntimeMXBean().getUptime()), false)
		);
	}

}
