package de.kittybot.kittybot.commands.roles;

import de.kittybot.kittybot.modules.SettingsModule;
import de.kittybot.kittybot.slashcommands.application.Category;
import de.kittybot.kittybot.slashcommands.application.RunGuildCommand;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionRole;
import de.kittybot.kittybot.slashcommands.interaction.GuildInteraction;
import de.kittybot.kittybot.slashcommands.interaction.Options;

@SuppressWarnings("unused")
public class AssignCommand extends RunGuildCommand{

	public AssignCommand(){
		super("assign", "Assigns yourself a self assignable roles", Category.ROLES);
		addOptions(
			new CommandOptionRole("role", "The role to assign").required()
		);
	}

	@Override
	public void run(Options options, GuildInteraction ia){
		var role = ia.getGuild().getRoleById(options.getLong("role"));
		if(role == null){
			ia.error("Unknown role provided");
			return;
		}
		var settings = ia.get(SettingsModule.class).getSettings(ia.getGuildId());
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
			ia.error("I don't have the permissions to assign you this role");
			return;
		}

		var group = settings.getSelfAssignableRoleGroups().stream().filter(g -> g.getId() == selfAssignableRole.getGroupId()).findFirst().orElse(null);
		if(group == null){
			ia.error("This role somehow misses a self assignable role group");
			return;
		}
		if(group.getMaxRoles() != -1 && selfAssignableRoles.stream().filter(r -> r.getGroupId() == group.getId() && ia.getMember().getRoles().stream().anyMatch(mr -> mr.getIdLong() == r.getRoleId())).count() >= group.getMaxRoles()){
			ia.error("Can't assign you " + role.getAsMention() + ". You already have the max roles of this group");
			return;
		}
		ia.getGuild().addRoleToMember(ia.getMember(), role)
			.reason("self assigned with kittybot")
			.queue(unused -> ia.reply("Assigned " + role.getAsMention()));
	}

}
