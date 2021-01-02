package de.kittybot.kittybot.commands.roles;

import de.kittybot.kittybot.command.Args;
import de.kittybot.kittybot.command.Category;
import de.kittybot.kittybot.command.Command;
import de.kittybot.kittybot.command.ctx.CommandContext;
import de.kittybot.kittybot.objects.SelfAssignableRoleGroup;
import de.kittybot.kittybot.utils.MessageUtils;
import de.kittybot.kittybot.utils.TableBuilder;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class GroupsCommand extends Command{

	public GroupsCommand(){
		super("groups", "Used to manage your self assignable role groups", Category.ROLES);
		setUsage("<add/remove/list>");
		addAliases("g", "gruppen");
		addPermissions(Permission.ADMINISTRATOR);
	}

	@Override
	public void run(Args args, CommandContext ctx){
		var settings = ctx.getGuildSettingsManager().getSettings(ctx.getGuildId());
		if(args.isEmpty() || args.get(0).equalsIgnoreCase("list")){
			var groups = settings.getSelfAssignableRoleGroups();
			if(groups == null){
				ctx.sendError("Error while getting self assignable role groups");
				return;
			}
			if(groups.isEmpty()){
				ctx.sendAnswer("There are not groups defined.\nYou can add them with " + settings.getCommandPrefix() + "`roles groups add <group name> <only one role>`");
				return;
			}
			var table = new TableBuilder<SelfAssignableRoleGroup>()
					.addColumn("group name", SelfAssignableRoleGroup::getName)
					.addColumn("max roles", SelfAssignableRoleGroup::getMaxRoles)
					.build(groups);

			ctx.sendAnswer(new EmbedBuilder().setTitle("Self-assignable role groups:").setDescription(table));
		}
		else if(args.get(0).equalsIgnoreCase("add")){
			if(args.size() < 3){
				ctx.sendUsage("groups add <name> <max roles>");
				return;
			}
			try{
				settings.addSelfAssignableRoleGroups(Collections.singleton(new SelfAssignableRoleGroup(ctx.getGuildId(), -1L, args.get(1), Integer.parseInt(args.get(2)))));
				ctx.sendAnswer("Group added!");
			}
			catch(NumberFormatException e){
				ctx.sendError("Please provide a number as your second argument");
			}
		}
		else if(args.get(0).equalsIgnoreCase("remove")){
			if(args.size() < 2){
				ctx.sendUsage("groups remove <group name>...");
				return;
			}
			var groups = removeSelfAssignableRoleGroupsByName(ctx, args.subList(1, args.size()));
			ctx.sendAnswer(MessageUtils.pluralize("Removed group", groups) + " " + groups.stream().map(SelfAssignableRoleGroup::getName));
		}
		else{
			ctx.sendUsage(this);
		}
	}

	public Set<SelfAssignableRoleGroup> removeSelfAssignableRoleGroupsByName(CommandContext ctx, List<String> set){
		return null;
	}

}
