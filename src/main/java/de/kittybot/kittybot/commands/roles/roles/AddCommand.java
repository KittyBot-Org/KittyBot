package de.kittybot.kittybot.commands.roles.roles;

import de.kittybot.kittybot.modules.SettingsModule;
import de.kittybot.kittybot.objects.settings.SelfAssignableRole;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionEmote;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionRole;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionString;
import de.kittybot.kittybot.slashcommands.application.options.GuildSubCommand;
import de.kittybot.kittybot.slashcommands.GuildCommandContext;
import de.kittybot.kittybot.slashcommands.Options;
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
