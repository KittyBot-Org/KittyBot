package de.kittybot.kittybot.commands.dev.dev;

import de.kittybot.kittybot.modules.CommandsModule;
import de.kittybot.kittybot.objects.enums.Environment;
import de.kittybot.kittybot.slashcommands.application.CommandOptionChoice;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionInteger;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionLong;
import de.kittybot.kittybot.slashcommands.application.options.SubCommand;
import de.kittybot.kittybot.slashcommands.interaction.GuildInteraction;
import de.kittybot.kittybot.slashcommands.interaction.Interaction;
import de.kittybot.kittybot.slashcommands.interaction.Options;
import de.kittybot.kittybot.slashcommands.interaction.response.FollowupMessage;
import de.kittybot.kittybot.slashcommands.interaction.response.InteractionResponse;
import de.kittybot.kittybot.utils.Colors;
import net.dv8tion.jda.api.EmbedBuilder;

import java.util.concurrent.TimeUnit;

public class RemoveCommand extends SubCommand{

	public RemoveCommand(){
		super("remove", "Removes slash commands from a specified environment");
		addOptions(
			new CommandOptionInteger("environment", "In which environment should the commands get removed").required()
				.addChoices(
					new CommandOptionChoice<>("global", 0),
					new CommandOptionChoice<>("guild", 1)
				),
			new CommandOptionLong("guild", "In which guild commands should get removed")
		);
		devOnly();
	}

	@Override
	public void run(Options options, Interaction ia){
		var environment = options.getInt("environment");
		if(environment == 0){
			if(Environment.getCurrent() == Environment.PRODUCTION){
				ia.reply(new InteractionResponse.Builder().ephemeral().setContent("Removing commands globally in production is not allowed sorry :3").build());
				return;
			}
			ia.reply(new InteractionResponse.Builder().ephemeral().setContent("processing...").build());
			ia.getModules().schedule(() -> {
				var commandsModule = ia.get(CommandsModule.class);
				commandsModule.deleteAllCommands(-1L);
				ia.followup(new FollowupMessage.Builder().setEmbeds(new EmbedBuilder().setColor(Colors.KITTYBOT_BLUE).setDescription("Removed slash commands globally").build()).build());
			}, 0, TimeUnit.SECONDS);
			return;
		}
		var guildId = options.has("guild") ? options.getLong("guild") : ia instanceof GuildInteraction ? ((GuildInteraction) ia).getGuildId() : -1L;
		if(guildId == -1){
			ia.error("Please provide a valid guild id");
			return;
		}
		ia.reply(new InteractionResponse.Builder().ephemeral().setContent("processing...").build());
		ia.getModules().schedule(() -> {
			var commandsModule = ia.get(CommandsModule.class);
			commandsModule.deleteAllCommands(guildId);
			ia.followup(new FollowupMessage.Builder().setEmbeds(new EmbedBuilder().setColor(Colors.KITTYBOT_BLUE).setDescription("Removed slash commands for guild `" + guildId + "`").build()).build());
		}, 0, TimeUnit.SECONDS);
	}

}