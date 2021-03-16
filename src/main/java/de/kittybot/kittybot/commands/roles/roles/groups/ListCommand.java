package de.kittybot.kittybot.commands.roles.roles.groups;

import de.kittybot.kittybot.modules.SettingsModule;
import de.kittybot.kittybot.slashcommands.application.options.GuildSubCommand;
import de.kittybot.kittybot.slashcommands.interaction.GuildInteraction;
import de.kittybot.kittybot.slashcommands.interaction.Options;

import java.util.stream.Collectors;

public class ListCommand extends GuildSubCommand{

	public ListCommand(){
		super("list", "Lists all self assignable role groups");
	}

	@Override
	public void run(Options options, GuildInteraction ia){
		var settings = ia.get(SettingsModule.class).getSettings(ia.getGuildId());
		var groups = settings.getSelfAssignableRoleGroups();
		if(groups.isEmpty()){
			ia.error("There are not groups defined.\nYou can add them with `/groups add <name> <max-roles>`");
			return;
		}
		ia.reply("**Self assignable role groups:**\n\n" + groups.stream().map(group -> "**Name:** `" + group.getName() + "` **Max Roles:** `" + group.getFormattedMaxRoles() + "`").collect(Collectors.joining("\n")));
	}

}
