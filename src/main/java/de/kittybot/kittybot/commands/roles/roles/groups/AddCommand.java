package de.kittybot.kittybot.commands.roles.roles.groups;

import de.kittybot.kittybot.modules.SettingsModule;
import de.kittybot.kittybot.objects.settings.SelfAssignableRoleGroup;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionInteger;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionString;
import de.kittybot.kittybot.slashcommands.application.options.GuildSubCommand;
import de.kittybot.kittybot.slashcommands.GuildCommandContext;
import de.kittybot.kittybot.slashcommands.Options;
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
	public void run(Options options, GuildCommandContext ctx){
		var name = options.getString("name");
		var maxRoles = options.has("max-roles") ? options.getInt("max-roles") : -1;
		ctx.get(SettingsModule.class).addSelfAssignableRoleGroups(ctx.getGuildId(), Collections.singleton(new SelfAssignableRoleGroup(-1, ctx.getGuildId(), name, maxRoles)));
		ctx.reply("New group added");
	}

}
