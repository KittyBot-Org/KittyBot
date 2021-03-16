package de.kittybot.kittybot.commands.info;

import de.kittybot.kittybot.slashcommands.application.Category;
import de.kittybot.kittybot.slashcommands.application.RunCommand;
import de.kittybot.kittybot.slashcommands.application.options.SubCommand;
import de.kittybot.kittybot.slashcommands.interaction.Interaction;
import de.kittybot.kittybot.slashcommands.interaction.Options;
import de.kittybot.kittybot.utils.Config;
import de.kittybot.kittybot.utils.Utils;
import net.dv8tion.jda.api.JDAInfo;

import java.lang.management.ManagementFactory;

@SuppressWarnings("unused")
public class InfoCommand extends RunCommand{

	public InfoCommand(){
		super("info", "Shows some bot info", Category.INFORMATION);
	}

	@Override
	public void run(Options options, Interaction ia){
		var shardManager = ia.getModules().getShardManager();
		var runtime = Runtime.getRuntime();
		ia.reply(builder -> builder
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
