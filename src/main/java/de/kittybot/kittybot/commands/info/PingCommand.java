package de.kittybot.kittybot.commands.info;

import de.kittybot.kittybot.command.Args;
import de.kittybot.kittybot.command.Category;
import de.kittybot.kittybot.command.Command;
import de.kittybot.kittybot.command.context.CommandContext;
import de.kittybot.kittybot.utils.Config;
import net.dv8tion.jda.api.EmbedBuilder;

@SuppressWarnings("unused")
public class PingCommand extends Command{

	public PingCommand(){
		super("ping", "Shows the bots ping", Category.INFORMATION);
	}

	@Override
	public void run(Args args, CommandContext ctx){
		var jda = ctx.getJDA();
		jda.getRestPing().queue(ping ->
				ctx.sendSuccess(new EmbedBuilder()
						.setAuthor("KittyBot Ping", Config.ORIGIN_URL, jda.getSelfUser().getEffectiveAvatarUrl())

						.addField("Gateway Ping:", jda.getGatewayPing() + "ms", false)
						.addField("Rest Ping:", ping + "ms", false)
				)
		);
	}

}
