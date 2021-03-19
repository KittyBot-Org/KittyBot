package de.kittybot.kittybot.commands.roles;

import de.kittybot.kittybot.modules.InviteModule;
import de.kittybot.kittybot.modules.SettingsModule;
import de.kittybot.kittybot.slashcommands.GuildCommandContext;
import de.kittybot.kittybot.slashcommands.Options;
import de.kittybot.kittybot.slashcommands.application.Category;
import de.kittybot.kittybot.slashcommands.application.Command;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionRole;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionString;
import de.kittybot.kittybot.slashcommands.application.options.GuildSubCommand;
import de.kittybot.kittybot.utils.MessageUtils;
import net.dv8tion.jda.api.Permission;

import java.util.Collections;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class InviteRolesCommand extends Command{

	public InviteRolesCommand(){
		super("inviteroles", "Used to map roles to invite links", Category.ROLES);
		addOptions(
			new AddCommand(),
			new RemoveCommand(),
			new ResetCommand(),
			new ListCommand()
		);
		addPermissions(Permission.ADMINISTRATOR);
	}

	private static class AddCommand extends GuildSubCommand{

		public AddCommand(){
			super("add", "Maps roles to invite links");
			addOptions(
				new CommandOptionString("code", "The invite code to add a role").required(),
				new CommandOptionRole("role", "The role to add to the invite").required()
			);
		}

		@Override
		public void run(Options options, GuildCommandContext ctx){
			var code = options.getString("code");
			var role = options.getRole("role");
			var invites = ctx.get(InviteModule.class).getGuildInvites(ctx.getGuildId());
			if(invites == null || invites.isEmpty()){
				ctx.error("No invites found for this guild");
				return;
			}
			if(!invites.containsKey(code)){
				ctx.error("No invite with code '" + code + "' found");
				return;
			}
			ctx.get(SettingsModule.class).addInviteRoles(ctx.getGuildId(), code, Collections.singleton(role.getIdLong()));
			ctx.reply("Added role " + role.getAsMention() + " to invite `" + code + "`");
		}

	}

	private static class RemoveCommand extends GuildSubCommand{

		public RemoveCommand(){
			super("remove", "Maps roles to invite links");
			addOptions(
				new CommandOptionString("code", "The invite code to remove a role").required(),
				new CommandOptionRole("role", "The role to remove from the invite").required()
			);
		}

		@Override
		public void run(Options options, GuildCommandContext ctx){
			var code = options.getString("code");
			var role = options.getRole("role");
			var invites = ctx.get(InviteModule.class).getGuildInvites(ctx.getGuildId());
			if(invites == null || invites.isEmpty()){
				ctx.error("No invites found for this guild");
				return;
			}
			if(!invites.containsKey(code)){
				ctx.error("No invite with code '" + code + "' found");
				return;
			}
			ctx.get(SettingsModule.class).removeInviteRoles(ctx.getGuildId(), code, Collections.singleton(role.getIdLong()));
			ctx.reply("Removed role " + role.getAsMention() + " from invite `" + code + "`");
		}

	}

	private static class ResetCommand extends GuildSubCommand{

		public ResetCommand(){
			super("reset", "Resets roles from invites");
			addOptions(
				new CommandOptionString("code", "The invite code to reset").required()
			);
		}

		@Override
		public void run(Options options, GuildCommandContext ctx){
			var code = options.getString("code");
			var invites = ctx.get(InviteModule.class).getGuildInvites(ctx.getGuildId());
			if(invites == null || invites.isEmpty()){
				ctx.error("No invites found for this guild");
				return;
			}
			if(!invites.containsKey(code)){
				ctx.error("No invite with code '" + code + "' found");
				return;
			}
			ctx.get(SettingsModule.class).removeInviteRoles(ctx.getGuildId(), code);
			ctx.reply("Roles reset from invite `" + code + "`");
		}

	}

	private static class ListCommand extends GuildSubCommand{

		public ListCommand(){
			super("list", "Maps roles to invite links");
			addOptions(
				new CommandOptionString("code", "The invite code to list roles")
			);
		}

		@Override
		public void run(Options options, GuildCommandContext ctx){
			var inviteRoles = ctx.get(SettingsModule.class).getInviteRoles(ctx.getGuildId());
			if(inviteRoles.isEmpty()){
				ctx.error("No invite roles found");
				return;
			}

			ctx.reply("**Invite Roles:**\n\n" + inviteRoles.entrySet().stream().map(entry ->
				"**" + entry.getKey() + "**" + entry.getValue().stream().map(MessageUtils::getRoleMention).collect(Collectors.joining("\n"))
			).collect(Collectors.joining("\n")));
		}

	}

}
