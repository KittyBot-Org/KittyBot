package de.kittybot.kittybot.commands.info;

import de.kittybot.kittybot.command.Category;
import de.kittybot.kittybot.command.Command;
import de.kittybot.kittybot.command.ctx.CommandContext;
import de.kittybot.kittybot.main.KittyBot;
import net.dv8tion.jda.api.EmbedBuilder;

import java.util.List;

public class PingCommand extends Command{

	private final KittyBot main;

	public PingCommand(KittyBot main){
		super("ping", "Shows the bots ping", Category.INFORMATION);
		this.main = main;
	}

	@Override
	public void run(List<String> args, CommandContext ctx){
		var jda = ctx.getJDA();
		jda.getRestPing().queue(ping ->
				ctx.sendSuccess(new EmbedBuilder()
						.setAuthor("KittyBot Ping", this.main.getConfig().getString("origin_url"), jda.getSelfUser().getEffectiveAvatarUrl())

						.addField("Gateway Ping:", jda.getGatewayPing() + "ms", false)
						.addField("Rest Ping:", ping + "ms", false)
				)
		);
	}

}
