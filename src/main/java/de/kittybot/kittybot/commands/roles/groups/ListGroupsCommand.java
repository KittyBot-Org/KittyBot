package de.kittybot.kittybot.commands.roles.groups;

import de.kittybot.kittybot.command.Args;
import de.kittybot.kittybot.command.Category;
import de.kittybot.kittybot.command.Command;
import de.kittybot.kittybot.command.context.CommandContext;
import de.kittybot.kittybot.modules.SettingsModule;
import de.kittybot.kittybot.objects.SelfAssignableRoleGroup;
import de.kittybot.kittybot.utils.TableBuilder;
import net.dv8tion.jda.api.EmbedBuilder;

@SuppressWarnings("unused")
public class ListGroupsCommand extends Command{

	public ListGroupsCommand(Command parent){
		super(parent, "list", "Used to manage your self assignable role groups", Category.ROLES);
		addAliases("ls");
	}

	@Override
	public void run(Args args, CommandContext ctx){
		var settings = ctx.get(SettingsModule.class).getSettings(ctx.getGuildId());
		var groups = settings.getSelfAssignableRoleGroups();
		if(groups.isEmpty()){
			ctx.sendError("There are not groups defined.\nYou can add them with `" + settings.getPrefix() + "groups add <group name> <@role>`");
			return;
		}
		var table = new TableBuilder<SelfAssignableRoleGroup>()
				.addColumn("group name", SelfAssignableRoleGroup::getName)
				.addColumn("max roles", SelfAssignableRoleGroup::getMaxRoles)
				.build(groups);

		ctx.sendSuccess(new EmbedBuilder().setTitle("Self-assignable role groups:").setDescription(table));
	}

}
