package de.anteiku.kittybot.commands.roles;

import de.anteiku.kittybot.database.Database;
import de.anteiku.kittybot.objects.cache.SelfAssignableRoleCache;
import de.anteiku.kittybot.objects.command.ACommand;
import de.anteiku.kittybot.objects.command.Category;
import de.anteiku.kittybot.objects.command.CommandContext;

public class UnassignCommand extends ACommand{

	public static final String COMMAND = "unassign";
	public static final String USAGE = "unassign <Role>";
	public static final String DESCRIPTION = "Unassigns yourself a role if it is self-assignable";
	protected static final String[] ALIASES = {"iamn"};
	protected static final Category CATEGORY = Category.ROLES;

	public UnassignCommand(){
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
		if(!SelfAssignableRoleCache.isSelfAssignableRole(ctx.getGuild().getId(), role.getId())){
			sendError(ctx, "Role `" + roleName + "` is not self assignable");
			return;
		}
		if(ctx.getMember().getRoles().stream().noneMatch(r -> r.getId().equals(role.getId()))){
			sendError(ctx, "You don't have the role `" + roleName + "`yet");
			return;
		}
		if(!ctx.getSelfMember().canInteract(role)){
			sendError(ctx, "I can't interact with role `" + roleName + "`");
			return;
		}
		ctx.getGuild().removeRoleFromMember(ctx.getMember(), role).reason("self-unassigned with message: " + ctx.getMessage().getId()).queue();
		sendAnswer(ctx, "Unassigned role `" + roleName + "` from you");
	}

}
