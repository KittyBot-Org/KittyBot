package de.kittybot.kittybot.commands.roles.roles;

import de.kittybot.kittybot.modules.SettingsModule;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionRole;
import de.kittybot.kittybot.slashcommands.application.options.GuildSubCommand;
import de.kittybot.kittybot.slashcommands.interaction.GuildInteraction;
import de.kittybot.kittybot.slashcommands.interaction.Options;
import net.dv8tion.jda.api.Permission;

import java.util.Collections;

public class RemoveCommand extends GuildSubCommand{

	public RemoveCommand(){
		super("remove", "Removes a self assignable role");
		addOptions(
			new CommandOptionRole("role", "The self assignable role to remove").required()
		);
		addPermissions(Permission.ADMINISTRATOR);
	}

	@Override
	public void run(Options options, GuildInteraction ia){
		var role = options.getRole("role");
		var settings = ia.get(SettingsModule.class);
		if(settings.getSelfAssignableRoles(ia.getGuildId()).stream().noneMatch(r -> r.getRoleId() == role.getIdLong())){
			ia.error("This role is not self assignable");
			return;
		}
		settings.removeSelfAssignableRoles(ia.getGuildId(), Collections.singleton(role.getIdLong()));
		ia.reply("Removed self assignable role");
	}

}
