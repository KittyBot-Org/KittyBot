package de.kittybot.kittybot.commands.admin.ban;

import de.kittybot.kittybot.slashcommands.application.options.CommandOptionString;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionUser;
import de.kittybot.kittybot.slashcommands.application.options.GuildSubCommand;
import de.kittybot.kittybot.slashcommands.interaction.GuildInteraction;
import de.kittybot.kittybot.slashcommands.interaction.Options;

public class RemoveCommand extends GuildSubCommand{

	public RemoveCommand(){
		super("remove", "Removes a ban");
		addOptions(
			new CommandOptionUser("user", "The user to unban").required(),
			new CommandOptionString("reason", "The unban reason")
		);
	}

	@Override
	public void run(Options options, GuildInteraction ia){
		var user = options.getUser("user");
		if(user == null){
			ia.error("Please provide a valid user id");
			return;
		}
		var reason = options.has("reason") ? options.getString("reason") : "Unbanned by " + ia.getMember().getAsMention();
		ia.getGuild().unban(user).reason(reason).queue(success ->
				ia.reply("Unbanned " + user.getAsMention() + " with reason: " + reason),
			error -> ia.error("Failed to unban " + user.getAsMention() + " for reason: `" + error.getMessage() + "`")
		);
	}

}
