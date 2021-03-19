package de.kittybot.kittybot.commands.dev.dev;

import de.kittybot.kittybot.modules.CommandsModule;
import de.kittybot.kittybot.objects.enums.Environment;
import de.kittybot.kittybot.slashcommands.CommandContext;
import de.kittybot.kittybot.slashcommands.GuildCommandContext;
import de.kittybot.kittybot.slashcommands.Options;
import de.kittybot.kittybot.slashcommands.application.OptionChoice;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionInteger;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionLong;
import de.kittybot.kittybot.slashcommands.application.options.SubCommand;

public class RemoveCommand extends SubCommand{

	public RemoveCommand(){
		super("remove", "Removes slash commands from a specified environment");
		addOptions(
			new CommandOptionInteger("environment", "In which environment should the commands get removed").required()
				.addChoices(
					new OptionChoice("global", 0),
					new OptionChoice("guild", 1)
				),
			new CommandOptionLong("guild", "In which guild commands should get removed")
		);
		devOnly();
	}

	@Override
	public void run(Options options, CommandContext ctx){
		var environment = options.getInt("environment");
		if(environment == 0){
			if(Environment.getCurrent() == Environment.PRODUCTION){
				ctx.error("Removing commands globally in production is not allowed sorry :3");
				return;
			}
			ctx.get(CommandsModule.class).deleteAllCommands(-1L);
			ctx.reply(embed -> embed.setDescription("Removed slash commands globally").build());
			return;
		}
		var guildId = options.has("guild") ? options.getLong("guild") : ctx instanceof GuildCommandContext ? ((GuildCommandContext) ctx).getGuildId() : -1L;
		if(guildId == -1){
			ctx.error("Please provide a valid guild id");
			return;
		}
		ctx.get(CommandsModule.class).deleteAllCommands(guildId);
		ctx.reply(embed -> embed.setDescription("Removed slash commands for guild `" + guildId + "`"));

	}

}