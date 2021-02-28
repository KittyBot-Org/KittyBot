package de.kittybot.kittybot.commands.info.info;

import de.kittybot.kittybot.slashcommands.application.options.SubCommand;
import de.kittybot.kittybot.slashcommands.interaction.Interaction;
import de.kittybot.kittybot.slashcommands.interaction.Options;
import de.kittybot.kittybot.utils.Config;

@SuppressWarnings("unused")
public class PingCommand extends SubCommand{

	public PingCommand(){
		super("ping", "Shows the bots ping");
	}

	@Override
	public void run(Options options, Interaction ia){
		var jda = ia.getJDA();
		jda.getRestPing().queue(ping ->
			ia.reply(builder -> builder
				.setAuthor("KittyBot Ping", Config.ORIGIN_URL, jda.getSelfUser().getEffectiveAvatarUrl())

				.addField("Gateway Ping:", jda.getGatewayPing() + "ms", false)
				.addField("Rest Ping:", ping + "ms", false)
			)
		);
	}

}
