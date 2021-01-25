package de.kittybot.kittybot.commands.roles;

import de.kittybot.kittybot.modules.SettingsModule;
import de.kittybot.kittybot.slashcommands.application.Category;
import de.kittybot.kittybot.slashcommands.application.Command;
import de.kittybot.kittybot.slashcommands.application.RunnableCommand;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionRole;
import de.kittybot.kittybot.slashcommands.context.CommandContext;
import de.kittybot.kittybot.slashcommands.context.Options;

@SuppressWarnings("unused")
public class AssignCommand extends Command implements RunnableCommand{

	public AssignCommand(){
		super("assign", "Assigns yourself a self assignable roles", Category.ROLES);
		addOptions(
			new CommandOptionRole("role", "The role to assign").required()
		);
	}

	@Override
	public void run(Options options, CommandContext ctx){
		var role = ctx.getGuild().getRoleById(options.getLong("role"));
		if(role == null){
			ctx.error("Unknown role provided");
			return;
		}
		var settings = ctx.get(SettingsModule.class).getSettings(ctx.getGuildId());
		var selfAssignableRoles = settings.getSelfAssignableRoles();
		if(selfAssignableRoles == null || selfAssignableRoles.isEmpty()){
			ctx.error("No self assignable roles configured");
			return;
		}

		var selfAssignableRole = selfAssignableRoles.stream().filter(r -> role.getIdLong() == r.getRoleId()).findFirst().orElse(null);
		if(selfAssignableRole == null){
			ctx.error("This role is not self assignable");
			return;
		}

		if(!ctx.getSelfMember().canInteract(role)){
			ctx.error("I don't have the permissions to assign you this role");
			return;
		}

		var group = settings.getSelfAssignableRoleGroups().stream().filter(g -> g.getId() == selfAssignableRole.getGroupId()).findFirst().orElse(null);
		if(group == null){
			ctx.error("This role somehow misses a self assignable role group");
			return;
		}
		if(group.getMaxRoles() != -1 && selfAssignableRoles.stream().filter(r -> r.getGroupId() == group.getId() && ctx.getMember().getRoles().stream().anyMatch(mr -> mr.getIdLong() == r.getRoleId())).count() >= group.getMaxRoles()){
			ctx.error("Can't assign you " + role.getAsMention() + ". You already have the max roles of this group");
			return;
		}
		ctx.getGuild().addRoleToMember(ctx.getMember(), role)
			.reason("self assigned with kittybot")
			.queue(unused -> ctx.reply("Assigned " + role.getAsMention()));
	}

}
