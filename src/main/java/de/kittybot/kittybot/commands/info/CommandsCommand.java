package de.kittybot.kittybot.commands.info;

import de.kittybot.kittybot.modules.CommandsModule;
import de.kittybot.kittybot.modules.PaginatorModule;
import de.kittybot.kittybot.slashcommands.application.Category;
import de.kittybot.kittybot.slashcommands.application.Command;
import de.kittybot.kittybot.slashcommands.application.RunCommand;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionString;
import de.kittybot.kittybot.slashcommands.application.options.GuildSubCommand;
import de.kittybot.kittybot.slashcommands.interaction.Interaction;
import de.kittybot.kittybot.slashcommands.interaction.Options;
import de.kittybot.kittybot.utils.Colors;
import de.kittybot.kittybot.utils.Config;
import de.kittybot.kittybot.utils.MessageUtils;

import java.time.Instant;
import java.util.ArrayList;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class CommandsCommand extends RunCommand{

	public CommandsCommand(){
		super("commands", "Shows all commands", Category.INFORMATION);
		addOptions(
			new CommandOptionString("command", "A specific command to display")
		);
	}

	@Override
	public void run(Options options, Interaction ia){
		var commands = ia.get(CommandsModule.class).getCommands().values();
		if(options.has("command")){
			var cmdName = options.getString("command");
			var optCmd = commands.stream().filter(cmd -> cmd.getName().equalsIgnoreCase(cmdName)).findFirst();
			if(optCmd.isEmpty()){
				ia.error("Command `" + cmdName + "` not found");
				return;
			}
			var cmd = optCmd.get();
			ia.reply(builder -> builder
				.setAuthor("Commands", Config.ORIGIN_URL + "/commands#" + cmd.getName(), ia.getSelfUser().getEffectiveAvatarUrl())
				.setDescription("`/" + cmd.getName() + "` - *" + cmd.getDescription() + "*\n\n" + cmd.getOptions().stream()
					.filter(GuildSubCommand.class::isInstance)
					.map(c -> "`/" + cmd.getName() + " " + c.getName() + "` - *" + c.getDescription() + "*")
					.collect(Collectors.joining("\n"))
				)
			);
			return;
		}

		var pages = new ArrayList<String>();
		commands.stream().collect(Collectors.groupingBy(Command::getCategory)).forEach((category, cmds) -> {
			if(cmds.isEmpty()){
				return;
			}
			var page = new StringBuilder().append("**").append(category.getEmote()).append(" ").append(category.getName()).append("**");

			cmds.forEach(cmd -> page.append("\n• `/").append(cmd.getName()).append("` - *").append(cmd.getDescription()).append("*"));

			pages.add(page.toString());
		});

		ia.get(PaginatorModule.class).create(
			ia,
			pages.size(),
			(page, embedBuilder) -> embedBuilder.setColor(Colors.KITTYBOT_BLUE)
				.setAuthor("Commands", Config.ORIGIN_URL + "/commands", ia.getJDA().getSelfUser().getEffectiveAvatarUrl())
				.setDescription(pages.get(page))
				.appendDescription("\n\n*Commands can also be found " + MessageUtils.maskLink("here", Config.ORIGIN_URL + "/commands") + "*")
				.setTimestamp(Instant.now())
		);
	}

}
