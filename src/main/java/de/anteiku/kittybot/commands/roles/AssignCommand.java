package de.anteiku.kittybot.commands.roles;

import de.anteiku.kittybot.database.Database;
import de.anteiku.kittybot.objects.command.ACommand;
import de.anteiku.kittybot.objects.command.Category;
import de.anteiku.kittybot.objects.command.CommandContext;

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
		if(!Database.isSelfAssignableRole(ctx.getGuild().getId(), role.getId())){
			sendError(ctx, "Role `" + roleName + "` is not self assignable");
			return;
		}
		if(ctx.getMember().getRoles().stream().anyMatch(r -> r.getId().equals(role.getId()))){
			sendError(ctx, "You already have the role `" + roleName + "`");
			return;
		}
		if(!ctx.getSelfMember().canInteract(role)){
			sendError(ctx, "I can't interact with role `" + roleName + "`");
			return;
		}
		ctx.getGuild().addRoleToMember(ctx.getMember(), role).reason("self-assigned with  message: " + ctx.getMessage().getId()).queue();
		sendAnswer(ctx, "Assigned role `" + roleName + "` to you");
	}

}
