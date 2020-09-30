package de.kittybot.kittybot.commands.roles;

import de.kittybot.kittybot.objects.Emojis;
import de.kittybot.kittybot.objects.ReactiveMessage;
import de.kittybot.kittybot.objects.cache.ReactiveMessageCache;
import de.kittybot.kittybot.objects.cache.SelfAssignableRoleCache;
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

import java.awt.*;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class RolesCommand extends ACommand{

	public static final String COMMAND = "roles";
	public static final String USAGE = "roles <add|remove|list>";
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
			if(ctx.getArgs()[0].equals("?") || ctx.getArgs()[0].equals("help")){
				sendUsage(ctx);
			}
			if(ctx.getMember().isOwner() || ctx.getMember().hasPermission(Permission.ADMINISTRATOR)){
				List<Role> roles = ctx.getMessage().getMentionedRoles();
				List<Emote> emotes = ctx.getMessage().getEmotes();
				if(ctx.getArgs()[0].equalsIgnoreCase("add") && !roles.isEmpty() && !emotes.isEmpty()){
					SelfAssignableRoleCache.addSelfAssignableRoles(ctx.getGuild().getId(), Utils.toMap(roles, emotes));
					sendAnswer(ctx, "Roles added!");
				}
				else if(ctx.getArgs()[0].equalsIgnoreCase("remove") && !roles.isEmpty()){
					SelfAssignableRoleCache.removeSelfAssignableRoles(ctx.getGuild().getId(), Utils.toSet(roles));
					sendAnswer(ctx, "Roles removed!");
				}
				else if(ctx.getArgs()[0].equalsIgnoreCase("list")){
					Map<Role, Emote> map = getRoleEmoteMap(ctx.getGuild());
					if(map.isEmpty()){
						sendAnswer(ctx, "There are no roles added!");
					}
					else{
						StringBuilder message = new StringBuilder();
						for(Map.Entry<Role, Emote> m : map.entrySet()){
							message.append(m.getKey().getAsMention()).append(", ");
						}
						sendAnswer(ctx, "Roles: " + message.toString());
					}
				}
				else{
					sendError(ctx, "Please be sure to mention a role & a custom discord emote");
				}
			}
			else{
				sendError(ctx, "You need to be an administrator to use this command!");
			}
		}
		else{
			Map<Role, Emote> roles = getRoleEmoteMap(ctx.getGuild());
			if(roles.isEmpty()){
				sendError(ctx, "No self-assignable roles configured!\nIf you are an admin use `.roles add @role :emote: @role :emote:...` to add roles!");
				return;
			}
			StringBuilder value = new StringBuilder();
			for(Map.Entry<Role, Emote> k : roles.entrySet()){
				value.append(k.getValue().getAsMention()).append(Emojis.BLANK).append(Emojis.BLANK).append(k.getKey().getAsMention()).append("\n");
			}
			answer(ctx, new EmbedBuilder().setTitle(title)
					.setDescription("To get/remove a role click reaction emote. " + Emojis.KITTY_BLINK + "\n\n")
					.setColor(Color.MAGENTA)
					.appendDescription("**Emote:**" + Emojis.BLANK + "**Role:**\n" + value)).queue(message -> {
				ReactiveMessageCache.addReactiveMessage(ctx, message, this, "-1");
				for(Map.Entry<Role, Emote> role : roles.entrySet()){
					message.addReaction(role.getValue()).queue();
				}
				message.addReaction(Emojis.WASTEBASKET).queue();
				message.addReaction(Emojis.WASTEBASKET).queue();
			});
		}
	}

	private Map<Role, Emote> getRoleEmoteMap(Guild guild){
		var roles = SelfAssignableRoleCache.getSelfAssignableRoles(guild.getId());
		var map = new LinkedHashMap<Role, Emote>();
		for(var entry : roles.entrySet()){
			Role role = guild.getRoleById(entry.getKey());
			if(role == null){
				SelfAssignableRoleCache.removeSelfAssignableRoles(guild.getId(), Collections.singleton(entry.getKey()));
				continue;
			}
			Emote emote = guild.getJDA().getEmoteById(entry.getValue());
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
		Map<Role, Emote> roles = getRoleEmoteMap(event.getGuild());
		for(Map.Entry<Role, Emote> r : roles.entrySet()){
			if(event.getReactionEmote().isEmote() && event.getReactionEmote().getId().equals(r.getValue().getId())){
				if(event.getMember().getRoles().contains(r.getKey())){
					event.getGuild().removeRoleFromMember(event.getMember(), r.getKey()).queue();
				}
				else{
					event.getGuild().addRoleToMember(event.getMember(), r.getKey()).queue();
				}
				event.getReaction().removeReaction(event.getUser()).queue();
			}
		}
	}

}
