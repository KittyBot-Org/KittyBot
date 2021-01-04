package de.kittybot.kittybot.commands.botowner;

import de.kittybot.kittybot.command.Args;
import de.kittybot.kittybot.command.Category;
import de.kittybot.kittybot.command.Command;
import de.kittybot.kittybot.command.CommandContext;
import de.kittybot.kittybot.utils.Config;
import net.dv8tion.jda.api.EmbedBuilder;

public class StatsCommand extends Command{


	public StatsCommand(){
		super("stats", "Shows some internal stats", Category.BOT_OWNER);
		setBotOwnerOnly();
	}

	@Override
	public void run(Args args, CommandContext ctx){
		var jda = ctx.getJDA();

		ctx.sendSuccess(new EmbedBuilder()
				.setAuthor("KittyBot Internal Cache Stats", Config.ORIGIN_URL, jda.getSelfUser().getEffectiveAvatarUrl())

				.addField("Command Response Cache:", ctx.getCommandResponseManager().getStats().toString(), false)
				.addField("Guild Settings Cache:", ctx.getGuildSettingsManager().getStats().toString(), false)
				.addField("Reactive Messages Cache:", ctx.getReactiveMessageManager().getStats().toString(), false)
				.addField("Dashboard Session Cache:", ctx.getDashboardSessionManager().getStats().toString(), false)
				.addField("Message Cache 1:", ctx.getMessageManager().getStats1().toString(), false)
				.addField("Message Cache 2:", ctx.getMessageManager().getStats2().toString(), false)
				.addField("Message Cache 3:", ctx.getMessageManager().getStats3().toString(), false)
				.addField("Message Cache 4:", ctx.getMessageManager().getStats4().toString(), false)
		);
	}

}
