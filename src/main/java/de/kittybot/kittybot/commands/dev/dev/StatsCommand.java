package de.kittybot.kittybot.commands.dev.dev;

import de.kittybot.kittybot.modules.*;
import de.kittybot.kittybot.slashcommands.CommandContext;
import de.kittybot.kittybot.slashcommands.Options;
import de.kittybot.kittybot.slashcommands.application.options.SubCommand;
import de.kittybot.kittybot.utils.Config;

@SuppressWarnings("unused")
public class StatsCommand extends SubCommand{

	public StatsCommand(){
		super("stats", "Shows cache stats");
		devOnly();
	}

	@Override
	public void run(Options options, CommandContext ctx){
		ctx.reply(builder -> builder
			.setAuthor("KittyBot Internal Cache Stats", Config.ORIGIN_URL, ctx.getSelfUser().getEffectiveAvatarUrl())

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