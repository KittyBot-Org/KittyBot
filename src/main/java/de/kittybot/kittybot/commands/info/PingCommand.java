package de.kittybot.kittybot.commands.info;

import de.kittybot.kittybot.objects.Config;
import de.kittybot.kittybot.objects.command.ACommand;
import de.kittybot.kittybot.objects.command.Category;
import de.kittybot.kittybot.objects.command.CommandContext;
import net.dv8tion.jda.api.EmbedBuilder;

public class PingCommand extends ACommand{

	public static final String COMMAND = "ping";
	public static final String USAGE = "ping";
	public static final String DESCRIPTION = "Shows the bots ping";
	protected static final String[] ALIASES = {};
	protected static final Category CATEGORY = Category.INFORMATIVE;

	public PingCommand(){
		super(COMMAND, USAGE, DESCRIPTION, ALIASES, CATEGORY);
	}

	@Override
	public void run(CommandContext ctx){
		var jda = ctx.getJDA();
		sendAnswer(ctx, new EmbedBuilder()
				.setAuthor("KittyBot Ping", Config.ORIGIN_URL, jda.getSelfUser().getEffectiveAvatarUrl())

				.addField("Gateway Ping:", jda.getGatewayPing() + "ms", false)
				.addField("Rest Ping:", jda.getRestPing().complete() + "ms", false)
		);
	}

}
