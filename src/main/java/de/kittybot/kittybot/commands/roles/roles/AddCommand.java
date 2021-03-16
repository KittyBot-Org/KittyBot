package de.kittybot.kittybot.commands.roles.roles;

import de.kittybot.kittybot.modules.SettingsModule;
import de.kittybot.kittybot.objects.settings.SelfAssignableRole;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionEmote;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionRole;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionString;
import de.kittybot.kittybot.slashcommands.application.options.GuildSubCommand;
import de.kittybot.kittybot.slashcommands.interaction.GuildInteraction;
import de.kittybot.kittybot.slashcommands.interaction.Options;
import net.dv8tion.jda.api.Permission;

import java.util.Collections;

public class AddCommand extends GuildSubCommand{

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
		var role = options.getRole("role");
		var emoteAction = options.getEmote(ia.getGuild(), "emote");
		var groupName = options.getString("group");

		emoteAction.queue(emote -> {
				var group = ia.get(SettingsModule.class).getSelfAssignableRoleGroups(ia.getGuildId()).stream().filter(g -> g.getName().equalsIgnoreCase(groupName)).findFirst();
				if(group.isEmpty()){
					ia.error("Please provide a valid group");
					return;
				}

				ia.get(SettingsModule.class).addSelfAssignableRoles(ia.getGuildId(), Collections.singleton(new SelfAssignableRole(role.getIdLong(), emote.getIdLong(), ia.getGuildId(), group.get().getId())));
				ia.reply("Added self assignable role");
			}, error -> ia.error("Please provide a valid emote from this server")
		);

	}

}
