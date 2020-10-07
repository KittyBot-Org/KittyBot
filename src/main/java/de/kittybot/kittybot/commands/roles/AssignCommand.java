package de.kittybot.kittybot.commands.roles;

import de.kittybot.kittybot.cache.SelfAssignableRoleCache;
import de.kittybot.kittybot.cache.SelfAssignableRoleGroupCache;
import de.kittybot.kittybot.objects.command.ACommand;
import de.kittybot.kittybot.objects.command.Category;
import de.kittybot.kittybot.objects.command.CommandContext;

public class AssignCommand extends ACommand{

	public static final String COMMAND = "assign";
	public static final String USAGE = "assign <Role>";
	public static final String DESCRIPTION = "Assigns yourself a role if it is self-assignable";
	protected static final String[] ALIASES = {"iam"};
	protected static final Category CATEGORY = Category.ROLES;

	public AssignCommand(){
		super(COMMAND, USAGE, DESCRIPTION, ALIASES, CATEGORY);
	}

	@Override
	public void run(CommandContext ctx){
		if(ctx.getArgs().length == 0){
			sendError(ctx, "Please specify a role");
			return;
		}
		var roleName = String.join(" ", ctx.getArgs());
		var roles = ctx.getJDA().getRolesByName(roleName, true);
		if(roles.isEmpty()){
			sendError(ctx, "No role with the name `" + roleName + "` found");
			return;
		}
		var role = roles.get(0);
		var selfAssignableRoles = SelfAssignableRoleCache.getSelfAssignableRoles(ctx.getGuild().getId());
		if(selfAssignableRoles == null){
			sendError(ctx, "No self assignable roles found");
			return;
		}
		var selfAssignableRole = selfAssignableRoles.stream().filter(r -> r.getRoleId().equals(role.getId())).findFirst().orElse(null);
		if(selfAssignableRole == null){
			sendError(ctx, "Role " + role.getAsMention() + " is not self assignable");
			return;
		}
		if(ctx.getMember().getRoles().stream().anyMatch(r -> r.getId().equals(role.getId()))){
			sendError(ctx, "You already have the role " + role.getAsMention());
			return;
		}
		if(!ctx.getSelfMember().canInteract(role)){
			sendError(ctx, "I can't interact with role " + role.getAsMention());
			return;
		}
		var group = SelfAssignableRoleGroupCache.getSelfAssignableRoleGroup(ctx.getGuild().getId(), selfAssignableRole.getGroupId());
		if(group == null){
			sendError(ctx, "Role " + role.getAsMention() + " has no self assignable role group anymore");
			return;
		}
		if(selfAssignableRoles.stream().filter(r -> r.getGroupId().equals(group.getId()) && ctx.getMember().getRoles().stream().anyMatch(mr -> mr.getId().equals(r.getRoleId()))).count() >= group.getMaxRoles()){
			sendError(ctx, "Can't assign you " + role.getAsMention() + ". You already have the max roles of this group");
			return;
		}
		ctx.getGuild().addRoleToMember(ctx.getMember(), role).reason("self-assigned with  message: " + ctx.getMessage().getId()).queue();
		sendAnswer(ctx, "Assigned " + role.getAsMention() + " to you");
	}

}
