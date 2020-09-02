package de.anteiku.kittybot.commands.roles;

import de.anteiku.kittybot.objects.Emojis;
import de.anteiku.kittybot.objects.ReactiveMessage;
import de.anteiku.kittybot.objects.SelfAssignableRoleGroup;
import de.anteiku.kittybot.objects.cache.ReactiveMessageCache;
import de.anteiku.kittybot.objects.cache.SelfAssignableRoleCache;
import de.anteiku.kittybot.objects.command.ACommand;
import de.anteiku.kittybot.objects.command.Category;
import de.anteiku.kittybot.objects.command.CommandContext;
import de.anteiku.kittybot.utils.MessageUtils;
import de.anteiku.kittybot.utils.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;

import java.awt.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
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
						SelfAssignableRoleCache.addSelfAssignableRoleGroups(ctx.getGuild().getId(), Arrays.stream(ctx.getArgs()).skip(2).collect(Collectors.toList()));
						sendAnswer(ctx, "Groups added!");
					}
					else if(ctx.getArgs()[1].equalsIgnoreCase("remove")){
						SelfAssignableRoleCache.removeSelfAssignableRoleGroupsByName(ctx.getGuild().getId(), Arrays.stream(ctx.getArgs()).skip(2).collect(Collectors.toList()));
						sendAnswer(ctx, "Groups removed!");
					}
					else if(ctx.getArgs()[1].equalsIgnoreCase("list")){
						var list = SelfAssignableRoleCache.getSelfAssignableRoleGroups(ctx.getGuild().getId());
						if(list == null){
							sendError(ctx, "Error while getting role groups");
							return;
						}
						sendAnswer(ctx, "Role Groups: " + list.stream().map(SelfAssignableRoleGroup::getGroupName).collect(Collectors.joining(", ")));
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
				SelfAssignableRoleCache.addSelfAssignableRoles(ctx.getGuild().getId(), Utils.toList(ctx.getGuild().getId(), roles, emotes));
				sendAnswer(ctx, "Roles added!");
			}
			else if(ctx.getArgs()[0].equalsIgnoreCase("remove") && !roles.isEmpty()){
				SelfAssignableRoleCache.removeSelfAssignableRolesById(ctx.getGuild().getId(), roles.stream().map(Role::getId).collect(Collectors.toList()));
				sendAnswer(ctx, "Roles removed!");
			}
			else if(ctx.getArgs()[0].equalsIgnoreCase("list")){
				var map = getRoleEmoteMap(ctx.getGuild());
				if(map.size() == 0){
					sendAnswer(ctx, "There are no roles added!");
				}
				else{
					sendAnswer(ctx, "Roles: " + map.keySet().stream().map(MessageUtils::getUserMention).collect(Collectors.joining(", ")));
				}
			}
			else{
				sendError(ctx, "Please be sure to mention a role & a custom discord emote");
			}
		}
		else{
			var roles = getRoleEmoteMap(ctx.getGuild());
			if(roles.size() == 0){
				sendError(ctx, "No self-assignable roles configured!\nIf you are an admin use `.roles add @role :emote: @role :emote:...` to add roles!");
				return;
			}
			var value = new StringBuilder();
			for(var k : roles.entrySet()){
				value.append(MessageUtils.getEmoteMention(k.getValue())).append(Emojis.BLANK).append(Emojis.BLANK).append(MessageUtils.getRoleMention(k.getKey())).append("\n");
			}
			answer(ctx, new EmbedBuilder().setTitle(title)
					.setDescription("To get/remove a role click reaction emote. " + Emojis.KITTY_BLINK + "\n\n")
					.setColor(Color.MAGENTA)
					.appendDescription("**Emote:**" + Emojis.BLANK + "**Role:**\n" + value)).queue(message -> {
				ReactiveMessageCache.addReactiveMessage(ctx, message, this, ctx.getUser().getId());
				for(var role : roles.entrySet()){
					message.addReaction(":i:" + role.getValue()).queue();
				}
				message.addReaction(Emojis.WASTEBASKET).queue();
				message.addReaction(Emojis.WASTEBASKET).queue();
			});
		}
	}

	private Map<String, String> getRoleEmoteMap(Guild guild){
		var roles = SelfAssignableRoleCache.getSelfAssignableRoles(guild.getId());
		if(roles == null){
			return Collections.emptyMap();
		}
		var map = new LinkedHashMap<String, String>();
		for(var role : roles){
			if(guild.getRoleById(role.getRoleId()) == null){
				SelfAssignableRoleCache.removeSelfAssignableRoles(guild.getId(), Collections.singletonList(role));
				continue;
			}
			if(guild.getJDA().getEmoteById(role.getEmoteId()) == null){
				SelfAssignableRoleCache.removeSelfAssignableRoles(guild.getId(), Collections.singletonList(role));
				continue;
			}
			map.put(role.getRoleId(), role.getEmoteId());
		}
		return map;
	}

	@Override
	public void reactionAdd(ReactiveMessage reactiveMessage, GuildMessageReactionAddEvent event){
		super.reactionAdd(reactiveMessage, event);
		var roles = getRoleEmoteMap(event.getGuild());
		for(var role : roles.entrySet()){
			if(event.getReactionEmote().isEmote() && event.getReactionEmote().getId().equals(role.getValue())){
				var r = event.getJDA().getRoleById(role.getKey());
				if(r != null){
					if(event.getMember().getRoles().stream().anyMatch(ro -> ro.getId().equals(role.getKey()))){
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
