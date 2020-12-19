package de.kittybot.kittybot.commands.info;

import de.kittybot.kittybot.command.Category;
import de.kittybot.kittybot.command.Command;
import de.kittybot.kittybot.command.ctx.CommandContext;
import de.kittybot.kittybot.utils.MessageUtils;
import net.dv8tion.jda.api.EmbedBuilder;

import java.util.List;

public class HelpCommand extends Command{

	public HelpCommand(){
		super("help", "Shows all commands", Category.INFORMATION);
		addAliases("?", "hilfe");
	}

	@Override
	public void run(List<String> args, CommandContext ctx){
		var response = new StringBuilder();
		var prefix = ctx.getGuildSettingsManager().getPrefix(ctx.getGuild().getIdLong());
		for(var category : Category.values()){
			response.append("\n**").append(category.getEmote()).append(" ").append(category.getName()).append("**");
			ctx.getCommandManager().getCommands().stream()
					.filter(cmd -> cmd.getCategory() == category)
					.forEach(cmd -> response.append("\n• **").append(prefix).append(cmd.getCommand()).append("** - *").append(cmd.getDescription()).append("*"));
		}
		ctx.sendSuccess(new EmbedBuilder()
				.setAuthor("Commands", ctx.getConfig().getString("origin_url") + "/commands", ctx.getSelfUser().getEffectiveAvatarUrl())
				.setDescription(response.toString())
				.appendDescription("\n\n*Commands can also be found " + MessageUtils.maskLink("here", ctx.getConfig().getString("origin_url") + "/commands") + "*")
		);
	}

}
