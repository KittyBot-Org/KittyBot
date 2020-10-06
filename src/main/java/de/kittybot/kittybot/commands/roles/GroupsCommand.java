package de.kittybot.kittybot.commands.roles;

import de.kittybot.kittybot.cache.PrefixCache;
import de.kittybot.kittybot.cache.SelfAssignableRoleGroupCache;
import de.kittybot.kittybot.objects.SelfAssignableRoleGroup;
import de.kittybot.kittybot.objects.command.ACommand;
import de.kittybot.kittybot.objects.command.Category;
import de.kittybot.kittybot.objects.command.CommandContext;
import de.kittybot.kittybot.utils.MessageUtils;
import de.kittybot.kittybot.utils.TableBuilder;
import de.kittybot.kittybot.utils.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;

import java.util.Arrays;
import java.util.Set;

public class GroupsCommand extends ACommand{

	public static final String COMMAND = "groups";
	public static final String USAGE = "groups <add/remove/list>";
	public static final String DESCRIPTION = "Used to manage your self assignable role groups";
	protected static final String[] ALIASES = {"g", "gruppen"};
	protected static final Category CATEGORY = Category.ROLES;

	public GroupsCommand(){
		super(COMMAND, USAGE, DESCRIPTION, ALIASES, CATEGORY);
	}

	@Override
	public void run(CommandContext ctx){
		var args = ctx.getArgs();
		if(!ctx.getMember().hasPermission(Permission.ADMINISTRATOR)){
			sendError(ctx, "You need to be an administrator to use this command!");
			return;
		}
		if(args.length == 0 || args[0].equalsIgnoreCase("list")){
			var groups = SelfAssignableRoleGroupCache.getSelfAssignableRoleGroups(ctx.getGuild().getId());
			if(groups == null){
				sendError(ctx, "Error while getting self assignable role groups");
				return;
			}
			if(groups.isEmpty()){
				sendAnswer(ctx, "There are not groups defined.\nYou can add them with " + PrefixCache.getCommandPrefix(ctx.getGuild().getId()) + "`roles groups add <group name> <only one role>`");
				return;
			}
			var table = new TableBuilder<SelfAssignableRoleGroup>()
					.addColumn("group name", SelfAssignableRoleGroup::getName)
					.addColumn("max roles", SelfAssignableRoleGroup::getMaxRoles)
					.build(groups);

			sendAnswer(ctx, new EmbedBuilder().setTitle("Self-assignable role groups:").setDescription(table));
		}
		else if(args[0].equalsIgnoreCase("add")){
			if(args.length < 3){
				sendUsage(ctx, "groups add <name> <max roles>");
				return;
			}
			try{
				SelfAssignableRoleGroupCache.addSelfAssignableRoleGroup(ctx.getGuild().getId(), new SelfAssignableRoleGroup(ctx.getGuild().getId(), null, args[1], Integer.parseInt(args[2])));
				sendAnswer(ctx, "Group added!");
			}
			catch(NumberFormatException e){
				sendError(ctx, "Please provide a number as your second argument");
			}
		}
		else if(args[0].equalsIgnoreCase("remove")){
			if(args.length < 2){
				sendUsage(ctx, "groups remove <group name>...");
				return;
			}
			var groups = SelfAssignableRoleGroupCache.removeSelfAssignableRoleGroupsByName(ctx.getGuild().getId(), Set.of(Arrays.copyOfRange(args, 1, args.length)));
			sendAnswer(ctx, Utils.pluralize("Removed group", groups) + " " + MessageUtils.join(groups, SelfAssignableRoleGroup::getName));
		}
		else{
			sendUsage(ctx);
		}
	}

}
