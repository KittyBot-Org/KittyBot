package de.kittybot.kittybot.commands.botowner;

import de.kittybot.kittybot.command.Category;
import de.kittybot.kittybot.command.Command;
import de.kittybot.kittybot.command.ctx.CommandContext;
import de.kittybot.kittybot.main.KittyBot;
import net.dv8tion.jda.api.EmbedBuilder;

import java.util.List;

public class StatsCommand extends Command{

	private final KittyBot main;

	public StatsCommand(KittyBot main){
		super("stats", "Shows some internal stats", Category.BOT_OWNER);
		setBotOwnerOnly();
		this.main = main;
	}

	@Override
	public void run(List<String> args, CommandContext ctx){
		var jda = ctx.getJDA();

		ctx.sendSuccess(new EmbedBuilder()
				.setAuthor("KittyBot Internal Cache Stats", this.main.getConfig().getString("origin_url"), jda.getSelfUser().getEffectiveAvatarUrl())

				.addField("Command Response Cache:", this.main.getCommandManager().getCommandResponseManager().getStats().toString(), false)
				.addField("Guild Settings Cache:", this.main.getCommandManager().getGuildSettingsManager().getStats().toString(), false)
				.addField("Reactive Messages Cache:", this.main.getCommandManager().getReactiveMessageManager().getStats().toString(), false)
				.addField("Dashboard Session Cache:", this.main.getDashboardSessionManager().getStats().toString(), false)
				.addField("Message Cache 1:", this.main.getMessageManager().getStats1().toString(), false)
				.addField("Message Cache 2:", this.main.getMessageManager().getStats2().toString(), false)
				.addField("Message Cache 3:", this.main.getMessageManager().getStats3().toString(), false)
				.addField("Message Cache 4:", this.main.getMessageManager().getStats4().toString(), false)
		);
	}

}
