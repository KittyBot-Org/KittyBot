package de.kittybot.kittybot.commands.info;

import de.kittybot.kittybot.command.Category;
import de.kittybot.kittybot.command.application.Command;
import de.kittybot.kittybot.command.application.RunnableCommand;
import de.kittybot.kittybot.command.context.CommandContext;
import de.kittybot.kittybot.command.interaction.Options;
import de.kittybot.kittybot.command.options.CommandOptionString;
import de.kittybot.kittybot.command.response.InteractionResponse;
import de.kittybot.kittybot.command.response.InteractionResponseType;
import de.kittybot.kittybot.modules.CommandsModule;
import de.kittybot.kittybot.modules.PaginatorModule;
import de.kittybot.kittybot.utils.Colors;
import de.kittybot.kittybot.utils.Config;
import de.kittybot.kittybot.utils.MessageUtils;
import net.dv8tion.jda.api.EmbedBuilder;

import java.time.Instant;
import java.util.ArrayList;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class HelpCommand extends Command implements RunnableCommand{

	public HelpCommand(){
		super("help", "Shows all commands", Category.INFORMATION);
		addOptions(
				new CommandOptionString("command", "The command you want to display help")
		);
	}

	@Override
	public void run(Options options, CommandContext ctx){
		var commands = ctx.get(CommandsModule.class).getCommands();
		if(options.has("command")){
			// TODO display sub-commands
			var cmdName = options.getString("command");
			var optCmd = commands.stream().filter(cmd -> cmd.getName().equalsIgnoreCase(cmdName)).findFirst();
			if(optCmd.isEmpty()){
				ctx.reply(new InteractionResponse.Builder()
						.setType(InteractionResponseType.CHANNEL_MESSAGE)
						.ephemeral()
						.setContent("Command `" + cmdName + "` not found")
						.build()
				);
				return;
			}
			var cmd = optCmd.get();
			ctx.reply(new InteractionResponse.Builder()
					.addEmbeds(new EmbedBuilder()
							.setColor(Colors.KITTYBOT_BLUE)
							.setAuthor("Command", Config.ORIGIN_URL + "/commands#" + cmd.getName(), ctx.getJDA().getSelfUser().getEffectiveAvatarUrl())
							.setDescription("`/" + cmd.getName() + "`\n*" + cmd.getDescription() + "*")
							.setFooter(ctx.getMember().getEffectiveName(), ctx.getUser().getEffectiveAvatarUrl())
							.setTimestamp(Instant.now())
							.build()
					)
					.build()
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

		ctx.get(PaginatorModule.class).create(
				ctx,
				pages.size(),
				(page, embedBuilder) -> embedBuilder.setColor(Colors.KITTYBOT_BLUE)
						.setAuthor("Commands", Config.ORIGIN_URL + "/commands", ctx.getJDA().getSelfUser().getEffectiveAvatarUrl())
						.setDescription(pages.get(page))
						.appendDescription("\n\n*Commands can also be found " + MessageUtils.maskLink("here", Config.ORIGIN_URL + "/commands") + "*")
						.setTimestamp(Instant.now())
		);
	}

}
