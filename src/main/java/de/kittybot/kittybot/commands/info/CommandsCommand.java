package de.kittybot.kittybot.commands.info;

import de.kittybot.kittybot.modules.CommandsModule;
import de.kittybot.kittybot.modules.PaginatorModule;
import de.kittybot.kittybot.slashcommands.application.Category;
import de.kittybot.kittybot.slashcommands.application.Command;
import de.kittybot.kittybot.slashcommands.application.RunnableCommand;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionString;
import de.kittybot.kittybot.slashcommands.application.options.SubCommand;
import de.kittybot.kittybot.slashcommands.context.CommandContext;
import de.kittybot.kittybot.slashcommands.context.Options;
import de.kittybot.kittybot.slashcommands.interaction.response.InteractionResponse;
import de.kittybot.kittybot.slashcommands.interaction.response.InteractionResponseType;
import de.kittybot.kittybot.utils.Colors;
import de.kittybot.kittybot.utils.Config;
import de.kittybot.kittybot.utils.MessageUtils;
import net.dv8tion.jda.api.EmbedBuilder;

import java.time.Instant;
import java.util.ArrayList;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class CommandsCommand extends Command implements RunnableCommand{

	public CommandsCommand(){
		super("commands", "Shows all commands", Category.INFORMATION);
		addOptions(
			new CommandOptionString("command", "A specific command to display")
		);
	}

	@Override
	public void run(Options options, CommandContext ctx){
		var commands = ctx.get(CommandsModule.class).getCommands().values();
		if(options.has("command")){
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
					.setAuthor("Commands", Config.ORIGIN_URL + "/commands#" + cmd.getName(), ctx.getJDA().getSelfUser().getEffectiveAvatarUrl())
					.setDescription("`/" + cmd.getName() + "` - *" + cmd.getDescription() + "*\n\n" + cmd.getOptions().stream().filter(SubCommand.class::isInstance).map(c ->
							"`/" + cmd.getName() + " " + c.getName() + "` - *" + c.getDescription() + "*"
						).collect(Collectors.joining("\n"))
					)
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
