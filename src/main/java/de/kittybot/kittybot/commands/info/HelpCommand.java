package de.kittybot.kittybot.commands.info;

import de.kittybot.kittybot.command.Args;
import de.kittybot.kittybot.command.Category;
import de.kittybot.kittybot.command.Command;
import de.kittybot.kittybot.command.context.CommandContext;
import de.kittybot.kittybot.modules.CommandModule;
import de.kittybot.kittybot.modules.SettingsModule;
import de.kittybot.kittybot.utils.Config;
import de.kittybot.kittybot.utils.MessageUtils;
import net.dv8tion.jda.api.EmbedBuilder;

@SuppressWarnings("unused")
public class HelpCommand extends Command{

	public HelpCommand(){
		super("help", "Shows all commands", Category.INFORMATION);
		addAliases("?", "hilfe", "cmds", "commands");
	}

	@Override
	public void run(Args args, CommandContext ctx){
		var response = new StringBuilder();
		var prefix = ctx.get(SettingsModule.class).getPrefix(ctx.getGuildId());
		for(var category : Category.values()){
			response.append("\n**").append(category.getEmote()).append(" ").append(category.getName()).append("**");
			ctx.get(CommandModule.class).getCommands().stream()
					.filter(cmd -> cmd.getCategory() == category)
					//.forEach(cmd -> response.append("\n• **").append(prefix).append(cmd.getCommand()).append("** - *").append(cmd.getDescription()).append("*"));
					.forEach(cmd -> response.append("\n• ").append(prefix).append(cmd.getCommand()));
		}
		ctx.sendSuccess(new EmbedBuilder()
				.setAuthor("Commands", Config.ORIGIN_URL + "/commands", ctx.getSelfUser().getEffectiveAvatarUrl())
				.setDescription(response.toString())
				.appendDescription("\n\n*Commands can also be found " + MessageUtils.maskLink("here", Config.ORIGIN_URL + "/commands") + "*")
		);
	}

}
