package de.kittybot.kittybot.commands.info;

import de.kittybot.kittybot.slashcommands.application.Category;
import de.kittybot.kittybot.slashcommands.application.RunCommand;
import de.kittybot.kittybot.slashcommands.interaction.Interaction;
import de.kittybot.kittybot.slashcommands.interaction.Options;
import de.kittybot.kittybot.utils.Colors;
import de.kittybot.kittybot.utils.Config;
import net.dv8tion.jda.api.EmbedBuilder;

@SuppressWarnings("unused")
public class PingCommand extends RunCommand{

	public PingCommand(){
		super("ping", "Shows the bots ping", Category.INFORMATION);
	}

	@Override
	public void run(Options options, Interaction ia){
		var jda = ia.getJDA();
		jda.getRestPing().queue(ping ->
			ia.reply(new EmbedBuilder()
				.setColor(Colors.KITTYBOT_BLUE)
				.setAuthor("KittyBot Ping", Config.ORIGIN_URL, jda.getSelfUser().getEffectiveAvatarUrl())

				.addField("Gateway Ping:", jda.getGatewayPing() + "ms", false)
				.addField("Rest Ping:", ping + "ms", false)
			)
		);
	}

}
