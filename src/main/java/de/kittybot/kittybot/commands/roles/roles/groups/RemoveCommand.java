package de.kittybot.kittybot.commands.roles.roles.groups;

import de.kittybot.kittybot.modules.SettingsModule;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionString;
import de.kittybot.kittybot.slashcommands.application.options.GuildSubCommand;
import de.kittybot.kittybot.slashcommands.GuildCommandContext;
import de.kittybot.kittybot.slashcommands.Options;
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
	public void run(Options options, GuildCommandContext ctx){
		var name = options.getString("name");
		var settings = ctx.get(SettingsModule.class);
		var group = settings.getSelfAssignableRoleGroups(ctx.getGuildId()).stream().filter(g -> g.getName().equalsIgnoreCase(name)).findFirst();
		if(group.isEmpty()){
			ctx.error("Group with name `" + name + "` not found");
			return;
		}
		settings.removeSelfAssignableRoleGroups(ctx.getGuildId(), Collections.singleton(group.get().getId()));
		ctx.reply("Removed group with name `" + name + "`");
	}

}
