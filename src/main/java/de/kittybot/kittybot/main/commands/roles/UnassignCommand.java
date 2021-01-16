package de.kittybot.kittybot.main.commands.roles;

import de.kittybot.kittybot.command.old.Args;
import de.kittybot.kittybot.command.Category;
import de.kittybot.kittybot.command.old.Command;
import de.kittybot.kittybot.command.old.CommandContext;
import de.kittybot.kittybot.modules.SettingsModule;

@SuppressWarnings("unused")
public class UnassignCommand extends Command{


	public UnassignCommand(){
		super("unassign", "Unassigns yourself a role if it is self-assignable", Category.ROLES);
		addAliases("iamnot", "iamn");
		setUsage("<@role/role-name/role-id>");
	}

	@Override
	public void run(Args args, CommandContext ctx){
		if(args.isEmpty()){
			ctx.sendError("Please specify a role");
			return;
		}
		var roleName = ctx.getRawMessage();
		var roles = ctx.getJDA().getRolesByName(roleName, true);
		if(roles.isEmpty()){
			ctx.sendError("No role with the name `" + roleName + "` found");
			return;
		}
		var role = roles.get(0);
		var selfAssignableRoles = ctx.get(SettingsModule.class).getSelfAssignableRoles(ctx.getGuildId());
		if(selfAssignableRoles.stream().noneMatch(r -> r.getRoleId() == role.getIdLong())){
			ctx.sendError("Role `" + roleName + "` is not self assignable");
			return;
		}
		if(ctx.getMember().getRoles().stream().noneMatch(r -> r.getIdLong() == role.getIdLong())){
			ctx.sendError("You don't have the role `" + roleName + "`yet");
			return;
		}
		if(!ctx.getSelfMember().canInteract(role)){
			ctx.sendError("I can't interact with role `" + roleName + "`");
			return;
		}
		ctx.getGuild().removeRoleFromMember(ctx.getMember(), role).reason("self-unassigned with message: " + ctx.getMessage().getId()).queue();
		ctx.sendSuccess("Unassigned role `" + roleName + "` from you");
	}

}
