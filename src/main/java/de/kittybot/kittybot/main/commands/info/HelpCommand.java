package de.kittybot.kittybot.main.commands.info;

import de.kittybot.kittybot.command.old.Args;
import de.kittybot.kittybot.command.Category;
import de.kittybot.kittybot.command.old.Command;
import de.kittybot.kittybot.command.old.CommandContext;
import de.kittybot.kittybot.modules.CommandModule;
import de.kittybot.kittybot.modules.PaginatorModule;
import de.kittybot.kittybot.modules.SettingsModule;
import de.kittybot.kittybot.utils.Colors;
import de.kittybot.kittybot.utils.Config;
import de.kittybot.kittybot.utils.MessageUtils;

import java.time.Instant;
import java.util.ArrayList;

@SuppressWarnings("unused")
public class HelpCommand extends Command{

	public HelpCommand(){
		super("help", "Shows all commands", Category.INFORMATION);
		addAliases("?", "hilfe", "cmds", "commands");
	}

	@Override
	public void run(Args args, CommandContext ctx){
		var prefix = ctx.get(SettingsModule.class).getPrefix(ctx.getGuildId());
		var commands = ctx.get(CommandModule.class).getCommands();

		var pages = new ArrayList<String>();
		for(var category : Category.values()){
			var page = new StringBuilder().append("**").append(category.getEmote()).append(" ").append(category.getName()).append("**");

			commands.stream()
					.filter(cmd -> cmd.getCategory() == category)
					.forEach(cmd -> page.append("\n• **").append(prefix).append(cmd.getName()).append("** - *").append(cmd.getDescription()).append("*"));
			pages.add(page.toString());
		}

		ctx.get(PaginatorModule.class).create(
			ctx.getChannel(),
			ctx.getUserId(),
			pages.size(),
			(page, embedBuilder) ->
				embedBuilder.setColor(Colors.KITTYBOT_BLUE)
						.setAuthor("Commands", Config.ORIGIN_URL + "/commands", ctx.getSelfUser().getEffectiveAvatarUrl())
						.setDescription(pages.get(page))
						.appendDescription("\n\n*Commands can also be found " + MessageUtils.maskLink("here", Config.ORIGIN_URL + "/commands") + "*")
						.setTimestamp(Instant.now())
		);
	}

}
