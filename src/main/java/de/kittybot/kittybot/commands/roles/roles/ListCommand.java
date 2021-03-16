package de.kittybot.kittybot.commands.roles.roles;

import de.kittybot.kittybot.modules.ReactionRoleModule;
import de.kittybot.kittybot.modules.SettingsModule;
import de.kittybot.kittybot.objects.enums.Emoji;
import de.kittybot.kittybot.objects.settings.SelfAssignableRole;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionString;
import de.kittybot.kittybot.slashcommands.application.options.GuildSubCommand;
import de.kittybot.kittybot.slashcommands.interaction.GuildInteraction;
import de.kittybot.kittybot.slashcommands.interaction.Options;
import de.kittybot.kittybot.utils.Colors;
import de.kittybot.kittybot.utils.MessageUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;

import java.util.LinkedHashSet;
import java.util.stream.Collectors;

public class ListCommand extends GuildSubCommand{

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

		if(!ia.getSelfMember().hasPermission(ia.getChannel(), Permission.MESSAGE_WRITE, Permission.MESSAGE_ADD_REACTION)){
			ia.error("To list roles make sure I have the following permissions: `MESSAGE_WRITE` & `MESSAGE_ADD_REACTION`");
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
