package de.kittybot.kittybot.commands.info;

import de.kittybot.kittybot.slashcommands.application.Category;
import de.kittybot.kittybot.slashcommands.application.Command;
import de.kittybot.kittybot.slashcommands.application.RunnableCommand;
import de.kittybot.kittybot.slashcommands.context.CommandContext;
import de.kittybot.kittybot.slashcommands.context.Options;
import de.kittybot.kittybot.utils.Colors;
import de.kittybot.kittybot.utils.Config;
import net.dv8tion.jda.api.EmbedBuilder;

@SuppressWarnings("unused")
public class PingCommand extends Command implements RunnableCommand{

	public PingCommand(){
		super("ping", "Shows the bots ping", Category.INFORMATION);
	}

	@Override
	public void run(Options options, CommandContext ctx){
		var jda = ctx.getJDA();
		jda.getRestPing().queue(ping ->
				ctx.reply(new EmbedBuilder()
						.setColor(Colors.KITTYBOT_BLUE)
						.setAuthor("KittyBot Ping", Config.ORIGIN_URL, jda.getSelfUser().getEffectiveAvatarUrl())

						.addField("Gateway Ping:", jda.getGatewayPing() + "ms", false)
						.addField("Rest Ping:", ping + "ms", false)
				)
		);
	}

}
