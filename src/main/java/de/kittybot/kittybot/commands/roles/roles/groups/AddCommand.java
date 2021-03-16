package de.kittybot.kittybot.commands.roles.roles.groups;

import de.kittybot.kittybot.modules.SettingsModule;
import de.kittybot.kittybot.objects.settings.SelfAssignableRoleGroup;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionInteger;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionString;
import de.kittybot.kittybot.slashcommands.application.options.GuildSubCommand;
import de.kittybot.kittybot.slashcommands.interaction.GuildInteraction;
import de.kittybot.kittybot.slashcommands.interaction.Options;
import net.dv8tion.jda.api.Permission;

import java.util.Collections;

public class AddCommand extends GuildSubCommand{

	public AddCommand(){
		super("add", "Adds a new self assignable role group");
		addOptions(
			new CommandOptionString("name", "The self assignable role to add").required(),
			new CommandOptionInteger("max-roles", "The amount of max roles you can get from this group")
		);
		addPermissions(Permission.ADMINISTRATOR);
	}

	@Override
	public void run(Options options, GuildInteraction ia){
		var name = options.getString("name");
		var maxRoles = options.has("max-roles") ? options.getInt("max-roles") : -1;
		ia.get(SettingsModule.class).addSelfAssignableRoleGroups(ia.getGuildId(), Collections.singleton(new SelfAssignableRoleGroup(-1, ia.getGuildId(), name, maxRoles)));
		ia.reply("New group added");
	}

}
