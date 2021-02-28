package de.kittybot.kittybot.commands.dev.dev;

import de.kittybot.kittybot.modules.*;
import de.kittybot.kittybot.slashcommands.application.options.SubCommand;
import de.kittybot.kittybot.slashcommands.interaction.Interaction;
import de.kittybot.kittybot.slashcommands.interaction.Options;
import de.kittybot.kittybot.utils.Config;

@SuppressWarnings("unused")
public class StatsCommand extends SubCommand{

	public StatsCommand(){
		super("stats", "Shows cache stats");
		devOnly();
	}

	@Override
	public void run(Options options, Interaction ia){
		ia.reply(builder -> builder
			.setAuthor("KittyBot Internal Cache Stats", Config.ORIGIN_URL, ia.getSelfUser().getEffectiveAvatarUrl())

			.addField("Command Response Cache:", ia.get(CommandResponseModule.class).getStats().toString(), false)
			.addField("Guild Settings Cache:", ia.get(SettingsModule.class).getStats().toString(), false)
			.addField("Reactive Messages Cache:", ia.get(ReactiveMessageModule.class).getStats().toString(), false)
			.addField("Dashboard Session Cache:", ia.get(DashboardSessionModule.class).getStats().toString(), false)
			.addField("Message Cache 1:", ia.get(MessageModule.class).getStats1().toString(), false)
			.addField("Message Cache 2:", ia.get(MessageModule.class).getStats2().toString(), false)
			.addField("Message Cache 3:", ia.get(MessageModule.class).getStats3().toString(), false)
			.addField("Message Cache 4:", ia.get(MessageModule.class).getStats4().toString(), false)
		);
	}

}