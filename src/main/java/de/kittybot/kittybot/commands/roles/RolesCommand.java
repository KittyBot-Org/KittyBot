package de.kittybot.kittybot.commands.roles;

import de.kittybot.kittybot.objects.Emojis;
import de.kittybot.kittybot.objects.ReactiveMessage;
import de.kittybot.kittybot.objects.SelfAssignableRole;
import de.kittybot.kittybot.objects.SelfAssignableRoleGroup;
import de.kittybot.kittybot.cache.ReactiveMessageCache;
import de.kittybot.kittybot.cache.SelfAssignableRoleCache;
import de.kittybot.kittybot.cache.SelfAssignableRoleGroupCache;
import de.kittybot.kittybot.objects.command.ACommand;
import de.kittybot.kittybot.objects.command.Category;
import de.kittybot.kittybot.objects.command.CommandContext;
import de.kittybot.kittybot.utils.MessageUtils;
import de.kittybot.kittybot.utils.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;

import java.awt.*;
import java.util.*;
import java.util.stream.Collectors;

public class RolesCommand extends ACommand{

	public static final String COMMAND = "roles";
	public static final String USAGE = "roles <group/add/remove/list>";
	public static final String DESCRIPTION = "Used to manage your roles";
	protected static final String[] ALIASES = {"r", "rollen"};
	protected static final Category CATEGORY = Category.ROLES;
	private static final String title = "Self-assignable roles:";

	public RolesCommand(){
		super(COMMAND, USAGE, DESCRIPTION, ALIASES, CATEGORY);
	}

	@Override
	public void run(CommandContext ctx){
		var args = ctx.getArgs();
		if(args.length > 0){
			if(!ctx.getMember().isOwner() && !ctx.getMember().hasPermission(Permission.ADMINISTRATOR)){
				sendError(ctx, "You need to be an administrator to use this command!");
				return;
			}
			if(args[0].equalsIgnoreCase("?") || args[0].equalsIgnoreCase("help")){
				sendUsage(ctx);
				return;
			}
			var roles = ctx.getMessage().getMentionedRoles();
			var emotes = ctx.getMessage().getEmotes();
			if(args[0].equalsIgnoreCase("add") && !roles.isEmpty() && !emotes.isEmpty()){
				var groups = SelfAssignableRoleGroupCache.getSelfAssignableRoleGroupByName(ctx.getGuild().getId(), args[1]);
				if(groups == null || groups.isEmpty()){
					sendError(ctx, "Role Group with name `" + args[1] + "` not found");
					return;
				}
				SelfAssignableRoleCache.addSelfAssignableRoles(ctx.getGuild().getId(), Utils.toSet(ctx.getGuild().getId(), groups.iterator().next().getId(), roles, emotes));
				sendAnswer(ctx, "Roles added!");
			}
			else if(args[0].equalsIgnoreCase("remove") && !roles.isEmpty()){
				SelfAssignableRoleCache.removeSelfAssignableRoles(ctx.getGuild().getId(), roles.stream().map(Role::getId).collect(Collectors.toSet()));
				sendAnswer(ctx, "Roles removed!");
			}
			else if(args[0].equalsIgnoreCase("list")){
				var sRoles = SelfAssignableRoleCache.getSelfAssignableRoles(ctx.getGuild().getId());
				if(sRoles == null || sRoles.isEmpty()){
					sendAnswer(ctx, "There are no roles added!");
				}
				else{
					sendAnswer(ctx, "Roles: " + sRoles.stream().map(sarg -> MessageUtils.getRoleMention(sarg.getRoleId())).collect(Collectors.joining(", ")));
				}
			}
			else{
				sendError(ctx, "Please be sure to mention a role & a custom discord emote");
			}
			return;
		}
		var groups = getSelfAssignableRoles(ctx.getGuild());
		if(groups.isEmpty()){
			sendError(ctx, "No self-assignable roles configured!\nIf you are an admin use `.roles add group name @role :emote: @role :emote:...` to add roles!");
			return;
		}
		var value = new StringBuilder();
		for(var group : groups.entrySet()){
			value.append("**").append(group.getKey().getName()).append("**").append(Emojis.BLANK).append("max roles: ").append(group.getKey().getMaxRoles()).append("\n");
			for(var role : group.getValue()){
				value.append(MessageUtils.getEmoteMention(role.getEmoteId())).append(Emojis.BLANK).append(Emojis.BLANK).append(MessageUtils.getRoleMention(role.getRoleId())).append("\n");
			}
			value.append("\n");
		}
		answer(ctx, new EmbedBuilder().setTitle(title)
				.setDescription("To get/remove a role react to this message with the specific  emote\n\n")
				.setColor(Color.MAGENTA)
				.appendDescription(value)).queue(message -> {
			ReactiveMessageCache.addReactiveMessage(ctx, message, this, ctx.getUser().getId());
			for(var group : groups.entrySet()){
				for(var role : group.getValue()){
					message.addReaction(":i:" + role.getEmoteId()).queue();
				}
			}
			message.addReaction(Emojis.WASTEBASKET).queue();
		});
	}

	private Map<SelfAssignableRoleGroup, Set<SelfAssignableRole>> getSelfAssignableRoles(Guild guild){
		var roles = SelfAssignableRoleCache.getSelfAssignableRoles(guild.getId());
		var groups = SelfAssignableRoleGroupCache.getSelfAssignableRoleGroups(guild.getId());
		if(roles == null || roles.isEmpty() || groups == null || groups.isEmpty()){
			return Collections.emptyMap();
		}
		var map = new LinkedHashMap<SelfAssignableRoleGroup, Set<SelfAssignableRole>>();
		for(var role : roles){
			if(guild.getRoleById(role.getRoleId()) == null){
				SelfAssignableRoleCache.removeSelfAssignableRole(guild.getId(), role.getRoleId());
				continue;
			}
			if(guild.getJDA().getEmoteById(role.getEmoteId()) == null){
				SelfAssignableRoleCache.removeSelfAssignableRole(guild.getId(), role.getRoleId());
				continue;
			}
			var group = groups.stream().filter(g -> g.getId().equals(role.getGroupId())).findFirst().orElse(null);
			if(group == null){
				continue;
			}
			map.putIfAbsent(group, new LinkedHashSet<>());
			map.get(group).add(role);
		}
		return map;
	}

	@Override
	public void reactionAdd(ReactiveMessage reactiveMessage, GuildMessageReactionAddEvent event){
		super.reactionAdd(reactiveMessage, event);
		var roles = SelfAssignableRoleCache.getSelfAssignableRoles(event.getGuild().getId());
		var groups = SelfAssignableRoleGroupCache.getSelfAssignableRoleGroups(event.getGuild().getId());
		if(roles == null || roles.isEmpty() || groups == null || groups.isEmpty()){
			return;
		}
		for(var role : roles){
			if(event.getReactionEmote().isEmote() && event.getReactionEmote().getId().equals(role.getEmoteId())){
				var r = event.getJDA().getRoleById(role.getRoleId());
				if(r != null){
					if(event.getMember().getRoles().stream().anyMatch(ro -> ro.getId().equals(role.getRoleId()))){
						event.getGuild().removeRoleFromMember(event.getMember(), r).queue();
					}
					else{
						event.getGuild().addRoleToMember(event.getMember(), r).queue();
					}
				}
				event.getReaction().removeReaction(event.getUser()).queue();
			}
		}
	}

}
