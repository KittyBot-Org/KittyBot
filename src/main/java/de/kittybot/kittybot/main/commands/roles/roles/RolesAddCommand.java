package de.kittybot.kittybot.main.commands.roles.roles;

import de.kittybot.kittybot.command.Category;
import de.kittybot.kittybot.command.old.Args;
import de.kittybot.kittybot.command.old.Command;
import de.kittybot.kittybot.command.old.CommandContext;
import de.kittybot.kittybot.modules.SettingsModule;
import de.kittybot.kittybot.objects.SelfAssignableRole;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Role;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class RolesAddCommand extends Command{

	public RolesAddCommand(Command parent){
		super(parent, "add", "Used to add self-assignable roles", Category.ROLES);
		setUsage("<@Role> <Emote>");
		addPermissions(Permission.ADMINISTRATOR);
	}

	@Override
	protected void run(Args args, CommandContext ctx){
		if(!ctx.getMember().hasPermission(Permission.ADMINISTRATOR)){
			ctx.sendError("You need to be an administrator to use this command");
			return;
		}
		var roles = ctx.getMessage().getMentionedRoles();
		var emotes = ctx.getMessage().getEmotes();
		if(args.size() < 3 || roles.isEmpty() || emotes.isEmpty()){
			ctx.sendError("Please be sure to mention a role & a custom discord emote");
			return;
		}
		var groups = ctx.get(SettingsModule.class).getSelfAssignableRoleGroups(ctx.getGuildId()).stream()
				.filter(group -> group.getName().equalsIgnoreCase(args.get(0)))
				.collect(Collectors.toSet());

		if(groups.isEmpty()){
			ctx.sendError("Role Group with name `" + args.get(0) + "` not found");
			return;
		}
		ctx.get(SettingsModule.class).addSelfAssignableRoles(ctx.getGuildId(), toSet(ctx.getGuildId(), groups.iterator().next().getId(), roles, emotes));
		ctx.sendSuccess("Roles added!");
	}

	public Set<SelfAssignableRole> toSet(long guildId, long groupId, List<Role> roles, List<Emote> emotes){
		return roles.stream().map(role ->
				emotes.get(roles.indexOf(role)) == null ? null : new SelfAssignableRole(guildId, groupId, role.getIdLong(), emotes.get(roles.indexOf(role)).getIdLong())
		).filter(Objects::nonNull).collect(Collectors.toSet());
	}

}
