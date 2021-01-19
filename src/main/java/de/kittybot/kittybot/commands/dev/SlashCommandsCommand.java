package de.kittybot.kittybot.commands.dev;

import de.kittybot.kittybot.modules.CommandsModule;
import de.kittybot.kittybot.slashcommands.application.Category;
import de.kittybot.kittybot.slashcommands.application.Command;
import de.kittybot.kittybot.slashcommands.application.CommandOptionChoice;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionInteger;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionString;
import de.kittybot.kittybot.slashcommands.application.options.SubCommand;
import de.kittybot.kittybot.slashcommands.application.options.SubCommandGroup;
import de.kittybot.kittybot.slashcommands.context.CommandContext;
import de.kittybot.kittybot.slashcommands.context.Options;
import de.kittybot.kittybot.slashcommands.interaction.response.FollowupMessage;
import de.kittybot.kittybot.slashcommands.interaction.response.InteractionResponse;
import de.kittybot.kittybot.utils.Colors;
import net.dv8tion.jda.api.EmbedBuilder;

import java.util.concurrent.TimeUnit;

@SuppressWarnings("unused")
public class SlashCommandsCommand extends SubCommandGroup{

	public SlashCommandsCommand(){
		super("slashcommands", "Deploys/Removes slash commands for a specified environment");
		addOptions(
			new DeployCommand(),
			new RemoveCommand()
		);
		devOnly();
	}

	private static class DeployCommand extends SubCommand{

		public DeployCommand(){
			super("deploy", "Deploys slash commands to the specified environment");
			addOptions(
				new CommandOptionInteger("environment", "In which environment should the commands get deployed").required()
					.addChoices(
						new CommandOptionChoice<>("global", 0),
						new CommandOptionChoice<>("guild", 1)
					),
				new CommandOptionString("guild", "In which guild commands should get deployed")
			);
		}

		@Override
		public void run(Options options, CommandContext ctx){
			var environment = options.getInt("environment");
			if(environment == 0){
				ctx.reply(new InteractionResponse.Builder().ephemeral().setContent("processing...").build());
				ctx.getModules().getScheduler().schedule(() -> {
					var commandsModule = ctx.get(CommandsModule.class);
					commandsModule.deleteAllCommands(-1L);
					commandsModule.deployAllCommands(-1L);
					ctx.followup(new FollowupMessage.Builder().setEmbeds(new EmbedBuilder().setColor(Colors.KITTYBOT_BLUE).setDescription("Deployed slash commands globally").build()).build());
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
				commandsModule.deployAllCommands(guildId);
				ctx.followup(new FollowupMessage.Builder().setEmbeds(new EmbedBuilder().setColor(Colors.KITTYBOT_BLUE).setDescription("Deployed slash commands for guild `" + guildId + "`").build()).build());
			}, 0, TimeUnit.SECONDS);
		}

	}

	private static class RemoveCommand extends SubCommand{

		public RemoveCommand(){
			super("remove", "Removes slash commands from a specified environment");
			addOptions(
				new CommandOptionInteger("environment", "In which environment should the commands get omitted").required()
					.addChoices(
						new CommandOptionChoice<>("global", 0),
						new CommandOptionChoice<>("guild", 1)
					),
				new CommandOptionString("guild", "In which guild commands should get omitted")
			);
		}

		@Override
		public void run(Options options, CommandContext ctx){
			var environment = options.getInt("environment");
			if(environment == 0){
				ctx.reply(new InteractionResponse.Builder().ephemeral().setContent("processing...").build());
				ctx.getModules().getScheduler().schedule(() -> {
					var commandsModule = ctx.get(CommandsModule.class);
					commandsModule.deleteAllCommands(-1L);
					ctx.followup(new FollowupMessage.Builder().setEmbeds(new EmbedBuilder().setColor(Colors.KITTYBOT_BLUE).setDescription("Omitted slash commands globally").build()).build());
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
				ctx.followup(new FollowupMessage.Builder().setEmbeds(new EmbedBuilder().setColor(Colors.KITTYBOT_BLUE).setDescription("Omitted slash commands for guild `" + guildId + "`").build()).build());
			}, 0, TimeUnit.SECONDS);
		}

	}

}