package de.kittybot.kittybot.commands.roles;

import de.kittybot.kittybot.modules.ReactionRoleModule;
import de.kittybot.kittybot.modules.SettingsModule;
import de.kittybot.kittybot.objects.enums.Emoji;
import de.kittybot.kittybot.objects.settings.SelfAssignableRole;
import de.kittybot.kittybot.slashcommands.GuildCommandContext;
import de.kittybot.kittybot.slashcommands.Options;
import de.kittybot.kittybot.slashcommands.application.Category;
import de.kittybot.kittybot.slashcommands.application.Command;
import de.kittybot.kittybot.slashcommands.application.GenericHelpCommand;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionEmote;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionRole;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionString;
import de.kittybot.kittybot.slashcommands.application.options.GuildSubCommand;
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
			new ListCommand(),
			new GroupsCommand(),
			new GenericHelpCommand("To configure self assignable roles you need first need to create a group with `/roles groups add <group name> <max-roles-from-group>`.\n" +
				"Then you can add a role to this group by doing `/roles add <@role> <custom-emote> <group-name>`.\n" +
				"You can add as much as you like but only 20 of them are assignable by reactions vctx the specific emote.\n" +
				"Use `/roles list` to view all roles with groups in a nice message"
			)
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
		public void run(Options options, GuildCommandContext ctx){
			var role = options.getRole("role");
			var emoteAction = options.getEmote(ctx.getGuild(), "emote");
			var groupName = options.getString("group");

			emoteAction.queue(emote -> {
					var group = ctx.get(SettingsModule.class).getSelfAssignableRoleGroups(ctx.getGuildId()).stream().filter(g -> g.getName().equalsIgnoreCase(groupName)).findFirst();
					if(group.isEmpty()){
						ctx.error("Please provide a valid group");
						return;
					}

					ctx.get(SettingsModule.class).addSelfAssignableRoles(ctx.getGuildId(), Collections.singleton(new SelfAssignableRole(role.getIdLong(), emote.getIdLong(), ctx.getGuildId(), group.get().getId())));
					ctx.reply("Added self assignable role");
				}, error -> ctx.error("Please provide a valid emote from this server")
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
		public void run(Options options, GuildCommandContext ctx){
			var role = options.getRole("role");
			var settings = ctx.get(SettingsModule.class);
			if(settings.getSelfAssignableRoles(ctx.getGuildId()).stream().noneMatch(r -> r.getRoleId() == role.getIdLong())){
				ctx.error("This role is not self assignable");
				return;
			}
			settings.removeSelfAssignableRoles(ctx.getGuildId(), Collections.singleton(role.getIdLong()));
			ctx.reply("Removed self assignable role");
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
		public void run(Options options, GuildCommandContext ctx){
			var settings = ctx.get(SettingsModule.class).getSettings(ctx.getGuildId());
			var roles = settings.getSelfAssignableRoles();
			var groups = settings.getSelfAssignableRoleGroups();
			if(roles == null || roles.isEmpty()){
				ctx.error("No self assignable roles configured");
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

			if(!ctx.getSelfMember().hasPermission(ctx.getChannel(), Permission.MESSAGE_WRITE, Permission.MESSAGE_ADD_REACTION)){
				ctx.error("To list roles make sure I have the following permissions: `MESSAGE_WRITE` & `MESSAGE_ADD_REACTION`");
				return;
			}
			ctx.acknowledge(true).queue(success ->
				ctx.getChannel().sendMessage(embed).queue(message -> {
					ctx.get(ReactionRoleModule.class).add(message.getGuild().getIdLong(), message.getIdLong());
					sortedRoles.forEach(role -> message.addReaction("test:" + role.getEmoteId()).queue());
				})
			);
		}

	}

}
