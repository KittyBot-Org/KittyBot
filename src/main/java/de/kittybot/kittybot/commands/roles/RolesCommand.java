package de.kittybot.kittybot.commands.roles;

import de.kittybot.kittybot.objects.Emojis;
import de.kittybot.kittybot.objects.ReactiveMessage;
import de.kittybot.kittybot.objects.SelfAssignableRole;
import de.kittybot.kittybot.objects.SelfAssignableRoleGroup;
import de.kittybot.kittybot.cache.PrefixCache;
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
import java.util.List;
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
		if(ctx.getArgs().length > 0){
			if(!ctx.getMember().isOwner() && !ctx.getMember().hasPermission(Permission.ADMINISTRATOR)){
				sendError(ctx, "You need to be an administrator to use this command!");
				return;
			}
			if(ctx.getArgs()[0].equalsIgnoreCase("?") || ctx.getArgs()[0].equalsIgnoreCase("help")){
				sendUsage(ctx);
				return;
			}
			else if(ctx.getArgs()[0].equals("group") || ctx.getArgs()[0].equals("groups")){
				if(ctx.getArgs().length == 0){
					sendUsage(ctx);
				}
				else if(ctx.getArgs().length >= 1){
					if(ctx.getArgs()[1].equalsIgnoreCase("add")){
						SelfAssignableRoleGroupCache.addSelfAssignableRoleGroup(ctx.getGuild().getId(), new SelfAssignableRoleGroup(ctx.getGuild().getId(), null, ctx.getArgs()[2], Integer.parseInt(ctx.getArgs()[3])));
						sendAnswer(ctx, "Groups added!");
					}
					else if(ctx.getArgs()[1].equalsIgnoreCase("remove")){
						SelfAssignableRoleGroupCache.removeSelfAssignableRoleGroupByName(ctx.getGuild().getId(), ctx.getArgs()[2]);
						sendAnswer(ctx, "Groups removed!");
					}
					else if(ctx.getArgs()[1].equalsIgnoreCase("list")){
						var list = SelfAssignableRoleGroupCache.getSelfAssignableRoleGroups(ctx.getGuild().getId());
						if(list == null){
							sendError(ctx, "Error while getting role groups");
							return;
						}
						if(list.isEmpty()){
							sendAnswer(ctx, "There are not groups defined.\nYou can add them with " + PrefixCache.getCommandPrefix(ctx.getGuild().getId()) + "`roles groups add <group name> <only one role>`");
							return;
						}
						sendAnswer(ctx, "Role Groups:\n" + list.stream().map(SelfAssignableRoleGroup::getName).collect(Collectors.joining(", ")));
					}
				}
				else{
					sendUsage(ctx);
				}
				return;
			}
			var roles = ctx.getMessage().getMentionedRoles();
			var emotes = ctx.getMessage().getEmotes();
			if(ctx.getArgs()[0].equalsIgnoreCase("add") && !roles.isEmpty() && !emotes.isEmpty()){
				var group = SelfAssignableRoleGroupCache.getSelfAssignableRoleGroupByName(ctx.getGuild().getId(), ctx.getArgs()[1]);
				if(group == null || group.isEmpty()){
					sendError(ctx, "Role Group with name `" + ctx.getArgs()[1] + "` not found");
					return;
				}
				SelfAssignableRoleCache.addSelfAssignableRoles(ctx.getGuild().getId(), Utils.toSet(ctx.getGuild().getId(), group.get(0).getId(), roles, emotes));
				sendAnswer(ctx, "Roles added!");
			}
			else if(ctx.getArgs()[0].equalsIgnoreCase("remove") && !roles.isEmpty()){
				SelfAssignableRoleCache.removeSelfAssignableRoles(ctx.getGuild().getId(), roles.stream().map(Role::getId).collect(Collectors.toSet()));
				sendAnswer(ctx, "Roles removed!");
			}
			else if(ctx.getArgs()[0].equalsIgnoreCase("list")){
				var list = SelfAssignableRoleCache.getSelfAssignableRoles(ctx.getGuild().getId());
				if(list == null || list.isEmpty()){
					sendAnswer(ctx, "There are no roles added!");
				}
				else{
					sendAnswer(ctx, "Roles: " + list.stream().map(sarg -> MessageUtils.getRoleMention(sarg.getRoleId())).collect(Collectors.joining(", ")));
				}
			}
			else{
				sendError(ctx, "Please be sure to mention a role & a custom discord emote");
			}
		}
		else{
			var groups = getSelfAssignableRoles(ctx.getGuild());
			if(groups.isEmpty()){
				sendError(ctx, "No self-assignable roles configured!\nIf you are an admin use `.roles add @role :emote: @role :emote:...` to add roles!");
				return;
			}
			var value = new StringBuilder();
			for(var group : groups.entrySet()){
				value.append("**").append(group.getKey().getName()).append("**").append(Emojis.BLANK).append("max roles: ").append(group.getKey().getMaxRoles()).append("\n");
				for(var role : group.getValue()){
					value.append(MessageUtils.getEmoteMention(role.getEmoteId())).append(Emojis.BLANK).append(MessageUtils.getRoleMention(role.getRoleId())).append("\n");
				}
				value.append("\n");
			}
			answer(ctx, new EmbedBuilder().setTitle(title)
					.setDescription("To get/remove a role click reaction emote. " + Emojis.KITTY_BLINK + "\n\n")
					.setColor(Color.MAGENTA)
					.appendDescription(value)).queue(message -> {
				ReactiveMessageCache.addReactiveMessage(ctx, message, this, ctx.getUser().getId());
				for(var group : groups.entrySet()){
					for(var role : group.getValue()){
						message.addReaction(":i:" + role.getEmoteId()).queue();
					}
				}
				message.addReaction(Emojis.WASTEBASKET).queue();
				message.addReaction(Emojis.WASTEBASKET).queue();
			});
		}
	}

	private Map<SelfAssignableRoleGroup, List<SelfAssignableRole>> getSelfAssignableRoles(Guild guild){
		var roles = SelfAssignableRoleCache.getSelfAssignableRoles(guild.getId());
		var groups = SelfAssignableRoleGroupCache.getSelfAssignableRoleGroups(guild.getId());
		if(roles == null || roles.isEmpty() || groups == null || groups.isEmpty()){
			return Collections.emptyMap();
		}
		var map = new LinkedHashMap<SelfAssignableRoleGroup, List<SelfAssignableRole>>();
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
			map.putIfAbsent(group, new LinkedList<>());
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
