package de.kittybot.kittybot.main.commands.botowner;

import de.kittybot.kittybot.command.old.Args;
import de.kittybot.kittybot.command.Category;
import de.kittybot.kittybot.command.old.Command;
import de.kittybot.kittybot.command.old.CommandContext;
import de.kittybot.kittybot.modules.*;
import de.kittybot.kittybot.utils.Config;
import net.dv8tion.jda.api.EmbedBuilder;

@SuppressWarnings("unused")
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

				.addField("Command Response Cache:", ctx.get(CommandResponseModule.class).getStats().toString(), false)
				.addField("Guild Settings Cache:", ctx.get(SettingsModule.class).getStats().toString(), false)
				.addField("Reactive Messages Cache:", ctx.get(ReactiveMessageModule.class).getStats().toString(), false)
				.addField("Dashboard Session Cache:", ctx.get(DashboardSessionModule.class).getStats().toString(), false)
				.addField("Message Cache 1:", ctx.get(MessageModule.class).getStats1().toString(), false)
				.addField("Message Cache 2:", ctx.get(MessageModule.class).getStats2().toString(), false)
				.addField("Message Cache 3:", ctx.get(MessageModule.class).getStats3().toString(), false)
				.addField("Message Cache 4:", ctx.get(MessageModule.class).getStats4().toString(), false)
		);
	}

}
