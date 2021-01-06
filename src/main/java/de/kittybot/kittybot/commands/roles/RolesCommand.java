package de.kittybot.kittybot.commands.roles;

import de.kittybot.kittybot.command.Args;
import de.kittybot.kittybot.command.Category;
import de.kittybot.kittybot.command.Command;
import de.kittybot.kittybot.command.CommandContext;
import de.kittybot.kittybot.exceptions.CommandException;
import de.kittybot.kittybot.objects.Emoji;
import de.kittybot.kittybot.objects.SelfAssignableRole;
import de.kittybot.kittybot.objects.SelfAssignableRoleGroup;
import de.kittybot.kittybot.utils.MessageUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Role;

import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

public class RolesCommand extends Command{

	public RolesCommand(){
		super("roles", "Used to manage self-assignable roles", Category.ROLES);
		setUsage("<group/add/remove/list>");
		addAliases("r", "rollen");
	}

	@Override
	public void run(Args args, CommandContext ctx) throws CommandException{
		var roles = ctx.getMessage().getMentionedRoles();
		var emotes = ctx.getMessage().getEmotes();
		if(args.isEmpty()){
			var groups = getSelfAssignableRoles(ctx);
			if(groups.isEmpty()){
				ctx.sendError("No self-assignable roles configured!\nIf you are an admin use `.roles add group name @role :emote: @role :emote:...` to add roles!");
				return;
			}
			var value = new StringBuilder();
			for(var group : groups.entrySet()){
				value.append("**").append(group.getKey().getName()).append("**").append(Emoji.BLANK).append("max roles: ").append(group.getKey().getMaxRoles()).append("\n");
				for(var role : group.getValue()){
					value.append(MessageUtils.getEmoteMention(role.getEmoteId())).append(Emoji.BLANK).append(Emoji.BLANK).append(MessageUtils.getRoleMention(role.getRoleId())).append("\n");
				}
				value.append("\n");
			}
			ctx.answer(new EmbedBuilder().setTitle("Self-assignable roles")
					.setDescription("To get/remove a role react to this message with the specific  emote\n\n")
					.setColor(Color.MAGENTA)
					.appendDescription(value)).queue(message -> {
				ctx.getReactiveMessageModule().addReactiveMessage(ctx, message.getIdLong(), ctx.getUser().getIdLong());
				for(var group : groups.entrySet()){
					for(var role : group.getValue()){
						message.addReaction(":i:" + role.getEmoteId()).queue();
					}
				}
				message.addReaction(Emoji.WASTEBASKET.getUnicode()).queue();
			});
		}
		else if(args.get(0).equalsIgnoreCase("list")){
			var sRoles = ctx.getGuildSettingsModule().getSelfAssignableRoles(ctx.getGuildId());
			if(sRoles == null || sRoles.isEmpty()){
				ctx.sendSuccess("There are no roles added");
			}
			else{
				ctx.sendSuccess("Roles: \n" + sRoles.stream().map(sarg -> MessageUtils.getRoleMention(sarg.getRoleId())).collect(Collectors.joining("\n")));
			}
		}
		else if(args.get(0).equalsIgnoreCase("add")){
			if(!ctx.getMember().hasPermission(Permission.ADMINISTRATOR)){
				ctx.sendError("You need to be an administrator to use this command");
				return;
			}
			if(args.size() < 4 || roles.isEmpty() || emotes.isEmpty()){
				ctx.sendError("Please be sure to mention a role & a custom discord emote");
				return;
			}
			var groups = getSelfAssignableRoleGroupsByName(ctx, args.get(1));
			if(groups == null || groups.isEmpty()){
				ctx.sendError("Role Group with name `" + args.get(1) + "` not found");
				return;
			}
			ctx.getGuildSettingsModule().addSelfAssignableRoles(ctx.getGuildId(), toSet(ctx.getGuildId(), groups.iterator().next().getId(), roles, emotes));
			ctx.sendSuccess("Roles added!");
		}
		else if(args.get(0).equalsIgnoreCase("remove")){
			if(!ctx.getMember().hasPermission(Permission.ADMINISTRATOR)){
				ctx.sendError("You need to be an administrator to use this command!");
				return;
			}
			if(args.size() < 2 || roles.isEmpty()){
				ctx.sendError("Please be sure to mention a role");
				return;
			}
			ctx.getGuildSettingsModule().removeSelfAssignableRoles(ctx.getGuildId(), roles.stream().map(Role::getIdLong).collect(Collectors.toSet()));
			ctx.sendSuccess("Roles removed!");
		}
		else if(args.get(0).equalsIgnoreCase("remove") && !roles.isEmpty()){
			ctx.getGuildSettingsModule().removeSelfAssignableRoles(ctx.getGuildId(), toSet(roles));
			ctx.sendSuccess("Roles removed!");
		}
		else{
			ctx.sendUsage(this);
		}
	}

	private Map<SelfAssignableRoleGroup, Set<SelfAssignableRole>> getSelfAssignableRoles(CommandContext ctx){
		var guildId = ctx.getGuildId();
		var settings = ctx.getGuildSettingsModule().getSettings(guildId);
		var roles = settings.getSelfAssignableRoles();
		var groups = settings.getSelfAssignableRoleGroups();
		if(roles == null || roles.isEmpty() || groups == null || groups.isEmpty()){
			return Collections.emptyMap();
		}
		var map = new LinkedHashMap<SelfAssignableRoleGroup, Set<SelfAssignableRole>>();
		for(var role : roles){
			if(ctx.getGuild().getRoleById(role.getRoleId()) == null || (ctx.getGuild().getJDA().getEmoteById(role.getEmoteId()) == null)){
				settings.removeSelfAssignableRoles(Collections.singleton(role.getRoleId()));
				continue;
			}
			var group = groups.stream().filter(g -> g.getId() == role.getGroupId()).findFirst().orElse(null);
			if(group == null){
				continue;
			}
			map.putIfAbsent(group, new LinkedHashSet<>());
			map.get(group).add(role);
		}
		return map;
	}

	private Set<SelfAssignableRoleGroup> getSelfAssignableRoleGroupsByName(CommandContext ctx, String groupName){
		return ctx.getGuildSettingsModule().getSelfAssignableRoleGroups(ctx.getGuildId()).stream().filter(group -> group.getName().equalsIgnoreCase(groupName)).collect(Collectors.toSet());
	}

	public Set<SelfAssignableRole> toSet(long guildId, long groupId, List<Role> roles, List<Emote> emotes){
		return roles.stream().map(role ->
				emotes.get(roles.indexOf(role)) == null ? null : new SelfAssignableRole(guildId, groupId, role.getIdLong(), emotes.get(roles.indexOf(role)).getIdLong())
		).filter(Objects::nonNull).collect(Collectors.toSet());
	}

	public Set<Long> toSet(List<Role> roles){
		return roles.stream().map(Role::getIdLong).collect(Collectors.toSet());
	}

	
	/*public void reactionAdd(ReactiveMessage reactiveMessage, GuildMessageReactionAddEvent event){
		super.reactionAdd(reactiveMessage, event);
		event.getReaction().removeReaction(event.getUser()).queue();
		if(!event.getReactionEmote().isEmote()){
			return;
		}
		var roles = SelfAssignableRoleCache.getSelfAssignableRoles(event.getGuild().getId());
		var groups = SelfAssignableRoleGroupCache.getSelfAssignableRoleGroups(event.getGuild().getId());
		if(roles == null || roles.isEmpty() || groups == null || groups.isEmpty()){
			return;
		}
		var selfAssignableRole = roles.stream().filter(r -> r.getEmoteId().equals(event.getReactionEmote().getId())).findFirst().orElse(null);
		if(selfAssignableRole == null){
			return;
		}
		var role = event.getJDA().getRoleById(selfAssignableRole.getRoleId());
		if(role == null){
			return;
		}
		var memberRoles = event.getMember().getRoles();
		if(memberRoles.stream().anyMatch(r -> r.getId().equals(selfAssignableRole.getRoleId()))){
			event.getGuild().removeRoleFromMember(event.getMember(), role).queue();
		}
		else{
			var group = groups.stream().filter(g -> g.getId().equals(selfAssignableRole.getGroupId())).findFirst().orElse(null);
			if(group == null){
				return;
			}
			if(roles.stream().filter(r -> r.getGroupId().equals(group.getId()) && memberRoles.stream().anyMatch(mr -> mr.getId().equals(r.getRoleId()))).count() < group.getMaxRoles()){
				event.getGuild().addRoleToMember(event.getMember(), role).queue();
			}
		}
	}*/

}
