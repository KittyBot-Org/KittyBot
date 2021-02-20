package de.kittybot.kittybot.commands.roles;

import de.kittybot.kittybot.modules.ReactionRoleModule;
import de.kittybot.kittybot.modules.SettingsModule;
import de.kittybot.kittybot.objects.enums.Emoji;
import de.kittybot.kittybot.objects.settings.SelfAssignableRole;
import de.kittybot.kittybot.slashcommands.application.Category;
import de.kittybot.kittybot.slashcommands.application.Command;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionEmote;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionRole;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionString;
import de.kittybot.kittybot.slashcommands.application.options.GuildSubCommand;
import de.kittybot.kittybot.slashcommands.interaction.GuildInteraction;
import de.kittybot.kittybot.slashcommands.interaction.Options;
import de.kittybot.kittybot.utils.Colors;
import de.kittybot.kittybot.utils.MessageUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class RolesCommand extends Command{

	public RolesCommand(){
		super("roles", "Used to configure self assignable roles", Category.ROLES);
		addOptions(
			new AddCommand(),
			new RemoveCommand(),
			new ListCommand()
		);
	}

	private static class AddCommand extends GuildSubCommand{

		public AddCommand(){
			super("add", "Adds a new self assignable role");
			addOptions(
				new CommandOptionRole("role", "The self assignable role to add").required(),
				new CommandOptionEmote("emote", "The emote for this self assignable role").required(),
				new CommandOptionString("group", "The group which the self assignable role should be assigned to").required()
			);
			addPermissions(Permission.ADMINISTRATOR);
		}

		@Override
		public void run(Options options, GuildInteraction ia){
			var roleId = options.getLong("role");
			var emoteAction = options.getEmote(ia.getGuild(), "emote");
			var groupName = options.getString("group");

			emoteAction.queue(emote -> {
					var group = ia.get(SettingsModule.class).getSelfAssignableRoleGroups(ia.getGuildId()).stream().filter(g -> g.getName().equalsIgnoreCase(groupName)).findFirst();
					if(group.isEmpty()){
						ia.error("Please provide a valid group");
						return;
					}

					ia.get(SettingsModule.class).addSelfAssignableRoles(ia.getGuildId(), Collections.singleton(new SelfAssignableRole(roleId, emote.getIdLong(), ia.getGuildId(), group.get().getId())));
					ia.reply("Added self assignable role");
				}, error -> ia.error("Please provide a valid emote from this server")
			);

		}

	}

	private static class RemoveCommand extends GuildSubCommand{

		public RemoveCommand(){
			super("remove", "Removes a self assignable role");
			addOptions(
				new CommandOptionRole("role", "The self assignable role to remove").required()
			);
			addPermissions(Permission.ADMINISTRATOR);
		}

		@Override
		public void run(Options options, GuildInteraction ia){
			var roleId = options.getLong("role");
			var settings = ia.get(SettingsModule.class);
			if(settings.getSelfAssignableRoles(ia.getGuildId()).stream().noneMatch(role -> role.getRoleId() == roleId)){
				ia.error("This role is not self assignable");
				return;
			}
			settings.removeSelfAssignableRoles(ia.getGuildId(), Collections.singleton(roleId));
			ia.reply("Removed self assignable role");
		}

	}

	private static class ListCommand extends GuildSubCommand{

		public ListCommand(){
			super("list", "Lists all self assignable roles");
			addOptions(
				new CommandOptionString("group", "The group to list")
			);
		}

		@Override
		public void run(Options options, GuildInteraction ia){
			var settings = ia.get(SettingsModule.class).getSettings(ia.getGuildId());
			var roles = settings.getSelfAssignableRoles();
			var groups = settings.getSelfAssignableRoleGroups();
			if(roles == null || roles.isEmpty()){
				ia.error("No self assignable roles configured");
				return;
			}
			var sortedRoles = new LinkedHashSet<>(roles);
			var embed = new EmbedBuilder()
				.setTitle("**Self Assignable Roles:**")
				.setColor(Colors.KITTYBOT_BLUE)
				.setDescription(sortedRoles.stream().collect(Collectors.groupingBy(SelfAssignableRole::getGroupId)).entrySet().stream().map(entry -> {
						var group = groups.stream().filter(g -> entry.getKey() == g.getId()).findFirst().orElse(null);
						if(group == null){
							return "";
						}
						return "**Group:** `" + group.getName() + "` **Max Roles:** `" + group.getFormattedMaxRoles() + "`\n" +
							entry.getValue().stream().map(role -> MessageUtils.getEmoteMention(role.getEmoteId()) + Emoji.BLANK.get() + Emoji.BLANK.get() + MessageUtils.getRoleMention(role.getRoleId())).collect(Collectors.joining("\n"));
					}).collect(Collectors.joining("\n"))
				).build();

			if(!ia.getSelfMember().hasPermission(Permission.MESSAGE_WRITE, Permission.MESSAGE_ADD_REACTION)){
				ia.reply(embed);
				return;
			}
			ia.acknowledge(true).queue(success ->
				ia.getChannel().sendMessage(embed).queue(message -> {
					ia.get(ReactionRoleModule.class).add(message.getGuild().getIdLong(), message.getIdLong());
					sortedRoles.forEach(role -> message.addReaction("test:" + role.getEmoteId()).queue());
				})
			);
		}

	}

}
