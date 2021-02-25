package de.kittybot.kittybot.commands.roles;

import de.kittybot.kittybot.modules.InviteModule;
import de.kittybot.kittybot.modules.GuildSettingsModule;
import de.kittybot.kittybot.slashcommands.application.Category;
import de.kittybot.kittybot.slashcommands.application.Command;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionRole;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionString;
import de.kittybot.kittybot.slashcommands.application.options.GuildSubCommand;
import de.kittybot.kittybot.slashcommands.interaction.GuildInteraction;
import de.kittybot.kittybot.slashcommands.interaction.Options;
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
		public void run(Options options, GuildInteraction ia){
			var code = options.getString("code");
			var role = options.getRole("role");
			var invites = ia.get(InviteModule.class).getGuildInvites(ia.getGuildId());
			if(invites == null || invites.isEmpty()){
				ia.error("No invites found for this guild");
				return;
			}
			if(!invites.containsKey(code)){
				ia.error("No invite with code '" + code + "' found");
				return;
			}
			ia.get(GuildSettingsModule.class).addInviteRoles(ia.getGuildId(), code, Collections.singleton(role.getIdLong()));
			ia.reply("Added role " + role.getAsMention() + " to invite `" + code + "`");
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
		public void run(Options options, GuildInteraction ia){
			var code = options.getString("code");
			var role = options.getRole("role");
			var invites = ia.get(InviteModule.class).getGuildInvites(ia.getGuildId());
			if(invites == null || invites.isEmpty()){
				ia.error("No invites found for this guild");
				return;
			}
			if(!invites.containsKey(code)){
				ia.error("No invite with code '" + code + "' found");
				return;
			}
			ia.get(GuildSettingsModule.class).removeInviteRoles(ia.getGuildId(), code, Collections.singleton(role.getIdLong()));
			ia.reply("Removed role " + role.getAsMention() + " from invite `" + code + "`");
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
		public void run(Options options, GuildInteraction ia){
			var code = options.getString("code");
			var invites = ia.get(InviteModule.class).getGuildInvites(ia.getGuildId());
			if(invites == null || invites.isEmpty()){
				ia.error("No invites found for this guild");
				return;
			}
			if(!invites.containsKey(code)){
				ia.error("No invite with code '" + code + "' found");
				return;
			}
			ia.get(GuildSettingsModule.class).removeInviteRoles(ia.getGuildId(), code);
			ia.reply("Roles reset from invite `" + code + "`");
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
		public void run(Options options, GuildInteraction ia){
			var inviteRoles = ia.get(GuildSettingsModule.class).getInviteRoles(ia.getGuildId());
			if(inviteRoles.isEmpty()){
				ia.error("No invite roles found");
				return;
			}

			ia.reply("**Invite Roles:**\n\n" + inviteRoles.entrySet().stream().map(entry ->
				"**" + entry.getKey() + "**" + entry.getValue().stream().map(MessageUtils::getRoleMention).collect(Collectors.joining("\n"))
			).collect(Collectors.joining("\n")));
		}

	}

}
