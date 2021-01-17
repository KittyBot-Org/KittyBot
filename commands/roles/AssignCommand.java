package de.kittybot.kittybot.main.commands.roles;

import de.kittybot.kittybot.slashcommands.application.Category;
import de.kittybot.kittybot.slashcommands.old.Args;
import de.kittybot.kittybot.slashcommands.old.Command;
import de.kittybot.kittybot.slashcommands.old.CommandContext;
import de.kittybot.kittybot.modules.SettingsModule;

@SuppressWarnings("unused")
public class AssignCommand extends Command{


	public AssignCommand(){
		super("assign", "Assigns yourself a role if it is self-assignable", Category.ROLES);
		addAliases("iam");
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
		var settings = ctx.get(SettingsModule.class).getSettings(ctx.getGuildId());
		var selfAssignableRoles = settings.getSelfAssignableRoles();
		if(selfAssignableRoles == null){
			ctx.sendError("No self assignable roles found");
			return;
		}
		var selfAssignableRole = selfAssignableRoles.stream().filter(r -> r.getRoleId() == role.getIdLong()).findFirst().orElse(null);
		if(selfAssignableRole == null){
			ctx.sendError("Role " + role.getAsMention() + " is not self assignable");
			return;
		}
		if(ctx.getMember().getRoles().stream().anyMatch(r -> r.getId().equals(role.getId()))){
			ctx.sendError("You already have the role " + role.getAsMention());
			return;
		}
		if(!ctx.getSelfMember().canInteract(role)){
			ctx.sendError("I can't interact with role " + role.getAsMention());
			return;
		}
		var group = settings.getSelfAssignableRoleGroups().stream().filter(g -> g.getId() == selfAssignableRole.getGroupId()).findFirst().orElse(null);
		if(group == null){
			ctx.sendError("Role " + role.getAsMention() + " has no self assignable role group anymore");
			return;
		}
		if(selfAssignableRoles.stream().filter(r -> r.getGroupId() == group.getId() && ctx.getMember().getRoles().stream().anyMatch(mr -> mr.getIdLong() == r.getRoleId())).count() >= group.getMaxRoles()){
			ctx.sendError("Can't assign you " + role.getAsMention() + ". You already have the max roles of this group");
			return;
		}
		ctx.getGuild().addRoleToMember(ctx.getMember(), role).reason("self-assigned with  message: " + ctx.getMessage().getId()).queue();
		ctx.sendSuccess("Assigned role `" + roleName + "` to you");
	}

}
