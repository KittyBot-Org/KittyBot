package de.kittybot.kittybot.commands.dev;

import de.kittybot.kittybot.modules.CommandsModule;
import de.kittybot.kittybot.slashcommands.application.Category;
import de.kittybot.kittybot.slashcommands.application.Command;
import de.kittybot.kittybot.slashcommands.application.CommandOptionChoice;
import de.kittybot.kittybot.slashcommands.application.RunnableCommand;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionInteger;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionString;
import de.kittybot.kittybot.slashcommands.context.CommandContext;
import de.kittybot.kittybot.slashcommands.context.Options;
import de.kittybot.kittybot.slashcommands.interaction.response.FollowupMessage;
import de.kittybot.kittybot.slashcommands.interaction.response.InteractionResponse;
import net.dv8tion.jda.api.EmbedBuilder;

import java.util.concurrent.TimeUnit;

@SuppressWarnings("unused")
public class DeployCommandsCommand extends Command implements RunnableCommand{

	public DeployCommandsCommand(){
		super("deploycommands", "Deploys slash commands in the specified environment", Category.DEV);
		addOptions(
				new CommandOptionInteger("environment", "In which environment should the commands get deployed").required()
						.addChoices(
								new CommandOptionChoice<>("global", 0),
								new CommandOptionChoice<>("guild", 1)
						),
				new CommandOptionString("guild", "In which guild commands should get deployed")
		);
		devOnly();
	}

	@Override
	public void run(Options options, CommandContext ctx){
		var environment = options.getInt("environment");
		if(environment == 0){
			ctx.reply(new InteractionResponse.Builder().ephemeral().setContent("processing...").build());
			ctx.getModules().getScheduler().schedule(() -> {
				var commandsModule = ctx.get(CommandsModule.class);
				commandsModule.deleteAllCommands(-1L);
				commandsModule.registerAllCommands(-1L);
				ctx.followup(new FollowupMessage.Builder().setEmbeds(new EmbedBuilder().setDescription("Deployed slash commands globally").build()).build());
			}, 0, TimeUnit.SECONDS);
			return;
		}
		var guildId = options.has("guild") ? options.getLong("guild") : ctx.getGuildId();
		if(guildId == -1){
			ctx.error("Please provide a valid guild id");
			return;
		}
		ctx.reply(new InteractionResponse.Builder().ephemeral().setContent("processing...").build());
		ctx.getModules().getScheduler().schedule(() -> {
			var commandsModule = ctx.get(CommandsModule.class);
			commandsModule.deleteAllCommands(guildId);
			commandsModule.registerAllCommands(guildId);
			ctx.followup(new FollowupMessage.Builder().setEmbeds(new EmbedBuilder().setDescription("Deployed slash commands for guild `" + guildId + "`").build()).build());
		}, 0, TimeUnit.SECONDS);
	}

}