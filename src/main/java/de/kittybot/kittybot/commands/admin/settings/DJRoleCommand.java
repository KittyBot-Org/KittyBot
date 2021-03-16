package de.kittybot.kittybot.commands.admin.settings;

import de.kittybot.kittybot.modules.SettingsModule;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionRole;
import de.kittybot.kittybot.slashcommands.application.options.GuildSubCommand;
import de.kittybot.kittybot.slashcommands.GuildCommandContext;
import de.kittybot.kittybot.slashcommands.Options;

public class DJRoleCommand extends GuildSubCommand{

	public DJRoleCommand(){
		super("djrole", "Sets the dj role");
		addOptions(
			new CommandOptionRole("role", "The new dj role").required()
		);
	}

	@Override
	public void run(Options options, GuildCommandContext ctx){
		var role = options.getRole("role");
		ctx.get(SettingsModule.class).setDjRoleId(ctx.getGuildId(), role.getIdLong());
		ctx.reply("DJ Role set to: " + role.getAsMention());
	}

}
