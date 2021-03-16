package de.kittybot.kittybot.commands.roles.roles;

import de.kittybot.kittybot.modules.SettingsModule;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionRole;
import de.kittybot.kittybot.slashcommands.application.options.GuildSubCommand;
import de.kittybot.kittybot.slashcommands.GuildCommandContext;
import de.kittybot.kittybot.slashcommands.Options;
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
	public void run(Options options, GuildCommandContext ctx){
		var role = options.getRole("role");
		var settings = ctx.get(SettingsModule.class);
		if(settings.getSelfAssignableRoles(ctx.getGuildId()).stream().noneMatch(r -> r.getRoleId() == role.getIdLong())){
			ctx.error("This role is not self assignable");
			return;
		}
		settings.removeSelfAssignableRoles(ctx.getGuildId(), Collections.singleton(role.getIdLong()));
		ctx.reply("Removed self assignable role");
	}

}
