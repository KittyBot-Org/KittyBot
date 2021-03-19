package de.kittybot.kittybot.commands.roles;

import de.kittybot.kittybot.modules.SettingsModule;
import de.kittybot.kittybot.slashcommands.GuildCommandContext;
import de.kittybot.kittybot.slashcommands.Options;
import de.kittybot.kittybot.slashcommands.application.Category;
import de.kittybot.kittybot.slashcommands.application.RunGuildCommand;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionRole;

@SuppressWarnings("unused")
public class UnassignCommand extends RunGuildCommand{

	public UnassignCommand(){
		super("unassign", "Unassigns yourself a self assignable roles", Category.ROLES);
		addOptions(
			new CommandOptionRole("role", "The role to unassign").required()
		);
	}

	@Override
	public void run(Options options, GuildCommandContext ctx){
		var role = options.getRole("role");
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
			ctx.error("I don't have the permissions to unassign you this role");
			return;
		}
		ctx.getGuild().removeRoleFromMember(ctx.getMember(), role)
			.reason("self unassigned with kittybot")
			.queue(unused -> ctx.reply("Unassigned " + role.getAsMention()));
	}

}
