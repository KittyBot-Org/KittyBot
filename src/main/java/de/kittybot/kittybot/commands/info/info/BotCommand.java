package de.kittybot.kittybot.commands.info.info;

import de.kittybot.kittybot.slashcommands.application.Category;
import de.kittybot.kittybot.slashcommands.application.options.SubCommand;
import de.kittybot.kittybot.slashcommands.interaction.Interaction;
import de.kittybot.kittybot.slashcommands.interaction.Options;
import de.kittybot.kittybot.utils.Colors;
import de.kittybot.kittybot.utils.Config;
import de.kittybot.kittybot.utils.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDAInfo;

import java.lang.management.ManagementFactory;

@SuppressWarnings("unused")
public class BotCommand extends SubCommand{

	public BotCommand(){
		super("bot", "Shows bot info");
	}

	@Override
	public void run(Options options, Interaction ia){
		var shardManager = ia.getModules().getShardManager();
		var runtime = Runtime.getRuntime();
		ia.reply(new EmbedBuilder()
			.setColor(Colors.KITTYBOT_BLUE)
			.setAuthor("KittyBot Information", Config.ORIGIN_URL, Category.INFORMATION.getEmoteUrl())

			.addField("JVM Version:", System.getProperty("java.version"), true)
			.addField("JDA Version:", JDAInfo.VERSION, true)
			.addBlankField(true)

			.addField("Total Shards:", String.valueOf(shardManager.getShardsTotal()), true)
			.addField("Current Shard:", String.valueOf(ia.getJDA().getShardInfo().getShardId()), true)
			.addBlankField(true)

			.addField("Total Guilds:", String.valueOf(shardManager.getGuildCache().size()), true)
			.addField("Total Users:", String.valueOf(Utils.getUserCount(shardManager)), true)
			.addBlankField(true)

			.addField("Memory Usage:", ((runtime.totalMemory() - runtime.freeMemory()) >> 20) + "MB / " + (runtime.maxMemory() >> 20) + "MB", true)
			.addField("Thread Count:", String.valueOf(ManagementFactory.getThreadMXBean().getThreadCount()), true)
			.addBlankField(true)
		);
	}

}
