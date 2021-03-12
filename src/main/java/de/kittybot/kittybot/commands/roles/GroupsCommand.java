package de.kittybot.kittybot.commands.roles;

import de.kittybot.kittybot.modules.SettingsModule;
import de.kittybot.kittybot.objects.settings.SelfAssignableRoleGroup;
import de.kittybot.kittybot.slashcommands.GuildCommandContext;
import de.kittybot.kittybot.slashcommands.Options;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionInteger;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionString;
import de.kittybot.kittybot.slashcommands.application.options.GuildSubCommand;
import de.kittybot.kittybot.slashcommands.application.options.SubCommandGroup;
import net.dv8tion.jda.api.Permission;

import java.util.Collections;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class GroupsCommand extends SubCommandGroup{

	public GroupsCommand(){
		super("groups", "Used to configure self assignable role groups");
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
		public void run(Options options, GuildCommandContext ctx){
			var name = options.getString("name");
			var maxRoles = options.getInt("max-roles", -1);
			ctx.get(SettingsModule.class).addSelfAssignableRoleGroups(ctx.getGuildId(), Collections.singleton(new SelfAssignableRoleGroup(-1, ctx.getGuildId(), name, maxRoles)));
			ctx.reply("New group added");
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

	private static class ListCommand extends GuildSubCommand{

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

}
