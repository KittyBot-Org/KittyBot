package de.kittybot.kittybot.commands.roles.roles.groups;

import de.kittybot.kittybot.modules.SettingsModule;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionString;
import de.kittybot.kittybot.slashcommands.application.options.GuildSubCommand;
import de.kittybot.kittybot.slashcommands.interaction.GuildInteraction;
import de.kittybot.kittybot.slashcommands.interaction.Options;
import net.dv8tion.jda.api.Permission;

import java.util.Collections;

public class RemoveCommand extends GuildSubCommand{

	public RemoveCommand(){
		super("remove", "Removes a self assignable role group & its roles");
		addOptions(
			new CommandOptionString("name", "The self assignable role to remove").required()
		);
		addPermissions(Permission.ADMINISTRATOR);
	}

	@Override
	public void run(Options options, GuildInteraction ia){
		var name = options.getString("name");
		var settings = ia.get(SettingsModule.class);
		var group = settings.getSelfAssignableRoleGroups(ia.getGuildId()).stream().filter(g -> g.getName().equalsIgnoreCase(name)).findFirst();
		if(group.isEmpty()){
			ia.error("Group with name `" + name + "` not found");
			return;
		}
		settings.removeSelfAssignableRoleGroups(ia.getGuildId(), Collections.singleton(group.get().getId()));
		ia.reply("Removed group with name `" + name + "`");
	}

}
