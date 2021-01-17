package de.kittybot.kittybot.main.commands.roles;

import de.kittybot.kittybot.command.Category;
import de.kittybot.kittybot.command.old.Args;
import de.kittybot.kittybot.command.old.Command;
import de.kittybot.kittybot.command.old.CommandContext;
import de.kittybot.kittybot.modules.InviteModule;
import de.kittybot.kittybot.modules.SettingsModule;
import de.kittybot.kittybot.utils.Config;
import de.kittybot.kittybot.utils.MessageUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Role;

import java.util.Collections;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class InviteRolesCommand extends Command{

	public InviteRolesCommand(){
		super("inviteroles", "Let's you attach roles to invite links", Category.ROLES);
		addAliases("iroles", "inviter");
		setUsage("<invite code> <@Role,.../delete>");
		addPermissions(Permission.MANAGE_SERVER, Permission.MANAGE_ROLES);
	}

	@Override
	public void run(Args args, CommandContext ctx){
		var roles = ctx.getMentionedRoles();
		if(args.isEmpty()){
			ctx.sendUsage(this);
			return;
		}
		var guildId = ctx.getGuildId();
		if(args.get(0).equalsIgnoreCase("list")){
			var message = new StringBuilder();
			var inviteRoles = ctx.get(SettingsModule.class).getInviteRoles(guildId).entrySet();
			if(inviteRoles.isEmpty()){
				message.append("No invite roles found");
			}
			else{
				for(var entry : inviteRoles){
					message.append("**").append(entry.getKey()).append("**\n");
					for(var role : entry.getValue()){
						message.append(MessageUtils.getRoleMention(role)).append("\n");
					}
					message.append("\n");
				}
			}
			ctx.sendSuccess(new EmbedBuilder().setAuthor("Invite Roles", Config.ORIGIN_URL, ctx.getSelfUser().getEffectiveAvatarUrl()).setDescription(message.toString()));
			return;
		}
		var invites = ctx.get(InviteModule.class).getGuildInvites(guildId);
		if(invites == null){
			ctx.sendError("No invites found for this guild");
			return;
		}
		if(!invites.containsKey(args.get(0))){
			ctx.sendError("No invite with code '" + args.get(0) + "' found");
			return;
		}
		if(args.size() > 1 && args.get(1).equalsIgnoreCase("delete")){
			ctx.get(SettingsModule.class).setInviteRoles(guildId, args.get(0), Collections.emptySet());
			ctx.sendSuccess("Deleted roles for invite '" + args.get(0) + "'");
			return;
		}
		if(roles.isEmpty()){
			ctx.sendUsage(this);
			return;
		}
		ctx.get(SettingsModule.class).setInviteRoles(guildId, args.get(0), roles.stream().map(Role::getIdLong).collect(Collectors.toSet()));
		ctx.sendSuccess("Added roles " + roles.stream().map(Role::getAsMention).collect(Collectors.joining(", ")) + " to invite with code '" + args.get(0) + "'");
	}

}
