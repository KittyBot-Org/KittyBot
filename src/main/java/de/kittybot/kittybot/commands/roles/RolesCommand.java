package de.kittybot.kittybot.commands.roles;

import de.kittybot.kittybot.cache.ReactiveMessageCache;
import de.kittybot.kittybot.cache.SelfAssignableRoleCache;
import de.kittybot.kittybot.objects.Emojis;
import de.kittybot.kittybot.objects.data.ReactiveMessage;
import de.kittybot.kittybot.objects.command.ACommand;
import de.kittybot.kittybot.objects.command.Category;
import de.kittybot.kittybot.objects.command.CommandContext;
import de.kittybot.kittybot.utils.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.requests.RestAction;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class RolesCommand extends ACommand{

	public static final String COMMAND = "roles";
	public static final String USAGE = "roles <add|remove|list>";
	public static final String DESCRIPTION = "Used to manage your roles";
	protected static final String[] ALIASES = {"r", "rollen"};
	protected static final Category CATEGORY = Category.ROLES;
	private static final String TITLE = "Self-assignable roles:";

	public RolesCommand(){
		super(COMMAND, USAGE, DESCRIPTION, ALIASES, CATEGORY);
	}

	@Override
	public void run(CommandContext ctx){
		if(ctx.getArgs().length == 0){
			var roles = getRoleEmoteMap(ctx.getGuild());
			if(roles.isEmpty()){
				sendError(ctx, "No self-assignable roles configured!\nIf you are an admin use `.roles add @role :emote: @role :emote:...` to add roles!");
				return;
			}
			var sb = new StringBuilder();
			roles.forEach((key, value) -> sb.append(value.getAsMention()).append(Emojis.BLANK).append(Emojis.BLANK).append(key.getAsMention()).append("\n"));

			ctx.getChannel().sendMessage(new EmbedBuilder().setTitle(TITLE)
					.setDescription("To get/remove a role click reaction emote. " + Emojis.KITTY_BLINK + "\n\n")
					.setColor(Color.MAGENTA)
					.appendDescription("**Emote:**" + Emojis.BLANK + "**Role:**\n" + sb).build())
					.queue(message -> {
						ReactiveMessageCache.addReactiveMessage(ctx, message, this, "-1");

						var actions = new ArrayList<RestAction<Void>>(roles.size());
						roles.values().forEach(emote -> actions.add(message.addReaction(emote)));
						RestAction.allOf(actions)
								.flatMap(ignored -> message.addReaction(Emojis.WASTEBASKET))
								.queue();
					});
			return;
		}
		if(ctx.getArgs()[0].equals("?") || ctx.getArgs()[0].equals("help")){
			sendUsage(ctx);
			return;
		}
		if(!ctx.getMember().hasPermission(Permission.ADMINISTRATOR)){
			sendError(ctx, "You need to be an administrator to use this command!");
			return;
		}
		var roles = ctx.getMessage().getMentionedRoles();
		var emotes = ctx.getMessage().getEmotes();
		if(ctx.getArgs()[0].equalsIgnoreCase("add") && !roles.isEmpty() && !emotes.isEmpty()){
			SelfAssignableRoleCache.addSelfAssignableRoles(ctx.getGuild().getId(), Utils.toMap(roles, emotes));
			sendSuccess(ctx, "Roles added!");
			return;
		}
		if(ctx.getArgs()[0].equalsIgnoreCase("remove") && !roles.isEmpty()){
			SelfAssignableRoleCache.removeSelfAssignableRoles(ctx.getGuild().getId(), Utils.toSet(roles));
			sendSuccess(ctx, "Roles removed!");
			return;
		}
		if(ctx.getArgs()[0].equalsIgnoreCase("list")){
			var map = getRoleEmoteMap(ctx.getGuild());
			if(map.isEmpty()){
				sendSuccess(ctx, "There are no roles added!");
				return;
			}
			var message = new StringBuilder();
			map.keySet().forEach(role -> message.append(role.getAsMention()).append(", "));
			sendSuccess(ctx, "Roles: " + message);
			return;
		}
		sendError(ctx, "Please be sure to mention a role & a custom discord emote");
	}

	private Map<Role, Emote> getRoleEmoteMap(Guild guild){
		var roles = SelfAssignableRoleCache.getSelfAssignableRoles(guild.getId());
		var map = new LinkedHashMap<Role, Emote>();
		for(var entry : roles.entrySet()){
			var role = guild.getRoleById(entry.getKey());
			if(role == null){
				SelfAssignableRoleCache.removeSelfAssignableRoles(guild.getId(), Collections.singleton(entry.getKey()));
				continue;
			}
			var emote = guild.getJDA().getEmoteById(entry.getValue());
			if(emote == null){
				SelfAssignableRoleCache.removeSelfAssignableRoles(guild.getId(), Collections.singleton(entry.getKey()));
				continue;
			}
			map.put(role, emote);
		}
		return map;
	}

	@Override
	public void reactionAdd(ReactiveMessage reactiveMessage, GuildMessageReactionAddEvent event){
		super.reactionAdd(reactiveMessage, event);
		var roles = getRoleEmoteMap(event.getGuild());
		roles.forEach((role, emote) -> {
			if(event.getReactionEmote().isEmote() && event.getReactionEmote().getId().equals(emote.getId())){
				if(event.getMember().getRoles().contains(role)){
					event.getGuild().removeRoleFromMember(event.getMember(), role).queue();
				}
				else{
					event.getGuild().addRoleToMember(event.getMember(), role).queue();
				}
				event.getReaction().removeReaction(event.getUser()).queue();
			}
		});
	}

}
