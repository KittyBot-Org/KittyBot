package de.kittybot.kittybot.commands.info;

import de.kittybot.kittybot.objects.Config;
import de.kittybot.kittybot.objects.command.ACommand;
import de.kittybot.kittybot.objects.command.Category;
import de.kittybot.kittybot.objects.command.CommandContext;
import de.kittybot.kittybot.objects.version.AppInfo;
import de.kittybot.kittybot.objects.version.GitInfo;
import de.kittybot.kittybot.utils.MessageUtils;
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
		var totalMemory = runtime.totalMemory() / 1000000;
		sendAnswer(ctx, new EmbedBuilder().setAuthor("KittyBot information", Config.ORIGIN_URL, jda.getSelfUser().getEffectiveAvatarUrl())
				.addField("Version:", AppInfo.getVersionBuild(), false)

				.addField("Build:", AppInfo.getBuildNumber(), true)
				.addField("Build time:", AppInfo.getBuildTime(), true)
				.addBlankField(true)

				.addField("Branch/Commit:", MessageUtils.maskLink(GitInfo.getBranch(), "https://github.com/KittyBot-Org/KittyBot/tree/" + GitInfo.getBranch()) + "/" + MessageUtils.maskLink(GitInfo.getShortCommitId(), "https://github.com/KittyBot-Org/KittyBot/commit/" + GitInfo.getCommitId()), true)
				.addField("Commit time:", GitInfo.getCommitTime(), true)
				.addBlankField(true)

				.addField("JVM version:", System.getProperty("java.version"), false)

				.addField("Total Guilds:", String.valueOf(jda.getGuildCache().size()), true)
				.addField("Total Users:", String.valueOf(jda.getUserCache().size()), true)
				.addBlankField(true)

				.addField("Shard Info:", jda.getShardInfo().getShardString(), false)

				.addField("Gateway Ping:", jda.getGatewayPing() + "ms", true)
				.addField("Rest Ping:", jda.getRestPing().complete() + "ms", true)
				.addBlankField(true)

				.addField("Memory Usage:", (totalMemory - (runtime.freeMemory() / 1000000)) + "mb / " + totalMemory + "mb", true)
				.addField("Thread count:", "" + ManagementFactory.getThreadMXBean().getThreadCount(), true)
				.addBlankField(true)
		);
	}

}
