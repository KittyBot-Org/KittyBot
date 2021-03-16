package de.kittybot.kittybot.commands.dev.dev;

import de.kittybot.kittybot.modules.CommandsModule;
import de.kittybot.kittybot.slashcommands.CommandContext;
import de.kittybot.kittybot.slashcommands.GuildCommandContext;
import de.kittybot.kittybot.slashcommands.Options;
import de.kittybot.kittybot.slashcommands.application.OptionChoice;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionInteger;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionLong;
import de.kittybot.kittybot.slashcommands.application.options.SubCommand;

public class DeployCommand extends SubCommand{

	public DeployCommand(){
		super("deploy", "Deploys slash commands to the specified environment");
		addOptions(
			new CommandOptionInteger("environment", "In which environment should the commands get deployed").required()
				.addChoices(
					new OptionChoice("global", 0),
					new OptionChoice("guild", 1)
				),
			new CommandOptionLong("guild", "In which guild commands should get deployed")
		);
		devOnly();
	}

	@Override
	public void run(Options options, CommandContext ctx){
		var environment = options.getInt("environment");
		if(environment == 0){
			ctx.get(CommandsModule.class).deployAllCommands(-1L);
			ctx.reply(embed -> embed.setDescription("Deployed slash commands globally"));
			return;
		}
		var guildId = options.has("guild") ? options.getLong("guild") : ctx instanceof GuildCommandContext ? ((GuildCommandContext) ctx).getGuildId() : -1L;
		if(guildId == -1L){
			ctx.error("Please provide a valid guild id");
			return;
		}
		ctx.get(CommandsModule.class).deployAllCommands(guildId);
		ctx.reply(embed -> embed.setDescription("Deployed slash commands for guild `" + guildId + "`"));
	}

}