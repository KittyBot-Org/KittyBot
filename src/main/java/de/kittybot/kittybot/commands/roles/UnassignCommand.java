package de.kittybot.kittybot.commands.roles;

import de.kittybot.kittybot.modules.GuildSettingsModule;
import de.kittybot.kittybot.slashcommands.application.Category;
import de.kittybot.kittybot.slashcommands.application.RunGuildCommand;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionRole;
import de.kittybot.kittybot.slashcommands.interaction.GuildInteraction;
import de.kittybot.kittybot.slashcommands.interaction.Options;

@SuppressWarnings("unused")
public class UnassignCommand extends RunGuildCommand{

	public UnassignCommand(){
		super("unassign", "Unassigns yourself a self assignable roles", Category.ROLES);
		addOptions(
			new CommandOptionRole("role", "The role to unassign").required()
		);
	}

	@Override
	public void run(Options options, GuildInteraction ia){
		var role = options.getRole("role");
		if(role == null){
			ia.error("Unknown role provided");
			return;
		}
		var settings = ia.get(GuildSettingsModule.class).getSettings(ia.getGuildId());
		var selfAssignableRoles = settings.getSelfAssignableRoles();
		if(selfAssignableRoles == null || selfAssignableRoles.isEmpty()){
			ia.error("No self assignable roles configured");
			return;
		}

		var selfAssignableRole = selfAssignableRoles.stream().filter(r -> role.getIdLong() == r.getRoleId()).findFirst().orElse(null);
		if(selfAssignableRole == null){
			ia.error("This role is not self assignable");
			return;
		}

		if(!ia.getSelfMember().canInteract(role)){
			ia.error("I don't have the permissions to unassign you this role");
			return;
		}
		ia.getGuild().removeRoleFromMember(ia.getMember(), role)
			.reason("self unassigned with kittybot")
			.queue(unused -> ia.reply("Unassigned " + role.getAsMention()));
	}

}
