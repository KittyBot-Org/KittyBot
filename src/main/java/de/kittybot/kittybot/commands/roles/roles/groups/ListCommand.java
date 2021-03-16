package de.kittybot.kittybot.commands.roles.roles.groups;

import de.kittybot.kittybot.modules.SettingsModule;
import de.kittybot.kittybot.slashcommands.application.options.GuildSubCommand;
import de.kittybot.kittybot.slashcommands.GuildCommandContext;
import de.kittybot.kittybot.slashcommands.Options;

import java.util.stream.Collectors;

public class ListCommand extends GuildSubCommand{

	public ListCommand(){
		super("list", "Lists all self assignable role groups");
	}

	@Override
	public void run(Options options, GuildCommandContext ctx){
		var settings = ctx.get(SettingsModule.class).getSettings(ctx.getGuildId());
		var groups = settings.getSelfAssignableRoleGroups();
		if(groups.isEmpty()){
			ctx.error("There are not groups defined.\nYou can add them with `/groups add <name> <max-roles>`");
			return;
		}
		ctx.reply("**Self assignable role groups:**\n\n" + groups.stream().map(group -> "**Name:** `" + group.getName() + "` **Max Roles:** `" + group.getFormattedMaxRoles() + "`").collect(Collectors.joining("\n")));
	}

}
