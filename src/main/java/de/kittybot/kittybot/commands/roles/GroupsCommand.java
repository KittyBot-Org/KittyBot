package de.kittybot.kittybot.commands.roles;

import de.kittybot.kittybot.modules.GuildSettingsModule;
import de.kittybot.kittybot.objects.settings.SelfAssignableRoleGroup;
import de.kittybot.kittybot.slashcommands.application.Category;
import de.kittybot.kittybot.slashcommands.application.Command;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionInteger;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionString;
import de.kittybot.kittybot.slashcommands.application.options.GuildSubCommand;
import de.kittybot.kittybot.slashcommands.interaction.GuildInteraction;
import de.kittybot.kittybot.slashcommands.interaction.Options;
import net.dv8tion.jda.api.Permission;

import java.util.Collections;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class GroupsCommand extends Command{

	public GroupsCommand(){
		super("groups", "Used to configure self assignable role groups", Category.ROLES);
		addOptions(
			new AddCommand(),
			new RemoveCommand(),
			new ListCommand()
		);
	}

	private static class AddCommand extends GuildSubCommand{

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
			ia.get(GuildSettingsModule.class).addSelfAssignableRoleGroups(ia.getGuildId(), Collections.singleton(new SelfAssignableRoleGroup(-1, ia.getGuildId(), name, maxRoles)));
			ia.reply("New group added");
		}

	}

	private static class RemoveCommand extends GuildSubCommand{

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
			var settings = ia.get(GuildSettingsModule.class);
			var group = settings.getSelfAssignableRoleGroups(ia.getGuildId()).stream().filter(g -> g.getName().equalsIgnoreCase(name)).findFirst();
			if(group.isEmpty()){
				ia.error("Group with name `" + name + "` not found");
				return;
			}
			settings.removeSelfAssignableRoleGroups(ia.getGuildId(), Collections.singleton(group.get().getId()));
			ia.reply("Removed group with name `" + name + "`");
		}

	}

	private static class ListCommand extends GuildSubCommand{

		public ListCommand(){
			super("list", "Lists all self assignable role groups");
		}

		@Override
		public void run(Options options, GuildInteraction ia){
			var settings = ia.get(GuildSettingsModule.class).getSettings(ia.getGuildId());
			var groups = settings.getSelfAssignableRoleGroups();
			if(groups.isEmpty()){
				ia.error("There are not groups defined.\nYou can add them with `/groups add <name> <max-roles>`");
				return;
			}
			ia.reply("**Self assignable role groups:**\n\n" + groups.stream().map(group -> "**Name:** `" + group.getName() + "` **Max Roles:** `" + group.getFormattedMaxRoles() + "`").collect(Collectors.joining("\n")));
		}

	}

}
