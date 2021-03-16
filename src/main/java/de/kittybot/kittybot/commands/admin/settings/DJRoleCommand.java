package de.kittybot.kittybot.commands.admin.settings;

import de.kittybot.kittybot.modules.SettingsModule;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionRole;
import de.kittybot.kittybot.slashcommands.application.options.GuildSubCommand;
import de.kittybot.kittybot.slashcommands.interaction.GuildInteraction;
import de.kittybot.kittybot.slashcommands.interaction.Options;

public class DJRoleCommand extends GuildSubCommand{

	public DJRoleCommand(){
		super("djrole", "Sets the dj role");
		addOptions(
			new CommandOptionRole("role", "The new dj role").required()
		);
	}

	@Override
	public void run(Options options, GuildInteraction ia){
		var role = options.getRole("role");
		ia.get(SettingsModule.class).setDjRoleId(ia.getGuildId(), role.getIdLong());
		ia.reply("DJ Role set to: " + role.getAsMention());
	}

}
