package de.kittybot.kittybot.commands.info;

import de.kittybot.kittybot.objects.Config;
import de.kittybot.kittybot.objects.command.ACommand;
import de.kittybot.kittybot.objects.command.Category;
import de.kittybot.kittybot.objects.command.CommandContext;
import de.kittybot.kittybot.utils.Utils;
import net.dv8tion.jda.api.EmbedBuilder;

import java.lang.management.ManagementFactory;

public class InfoCommand extends ACommand{

	public static final String COMMAND = "info";
	public static final String USAGE = "info";
	public static final String DESCRIPTION = "Shows some bot info";
	protected static final String[] ALIASES = {};
	protected static final Category CATEGORY = Category.INFORMATIVE;

	public InfoCommand(){
		super(COMMAND, USAGE, DESCRIPTION, ALIASES, CATEGORY);
	}

	@Override
	public void run(CommandContext ctx){
		var jda = ctx.getJDA();
		var runtime = Runtime.getRuntime();
		sendAnswer(ctx, new EmbedBuilder()
				.setAuthor("KittyBot information", Config.ORIGIN_URL, jda.getSelfUser().getEffectiveAvatarUrl())

				.addField("JVM version:", System.getProperty("java.version"), false)

				.addField("Total Guilds:", String.valueOf(jda.getGuildCache().size()), true)
				.addField("Total Users:", String.valueOf(Utils.getUserCount(jda)), true)

				.addField("Memory Usage:", ((runtime.totalMemory() - runtime.freeMemory()) >> 20) + "MB / " + (runtime.maxMemory() >> 20) + "MB", false)
				.addField("Thread count:", String.valueOf(ManagementFactory.getThreadMXBean().getThreadCount()), true)
		);
	}

}
