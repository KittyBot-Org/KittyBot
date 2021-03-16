package de.kittybot.kittybot.commands.admin.ban;

import de.kittybot.kittybot.slashcommands.application.options.CommandOptionString;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionUser;
import de.kittybot.kittybot.slashcommands.application.options.GuildSubCommand;
import de.kittybot.kittybot.slashcommands.GuildCommandContext;
import de.kittybot.kittybot.slashcommands.Options;

public class RemoveCommand extends GuildSubCommand{

	public RemoveCommand(){
		super("remove", "Removes a ban");
		addOptions(
			new CommandOptionUser("user", "The user to unban").required(),
			new CommandOptionString("reason", "The unban reason")
		);
	}

	@Override
	public void run(Options options, GuildCommandContext ctx){
		var user = options.getUser("user");
		if(user == null){
			ctx.error("Please provide a valid user id");
			return;
		}
		var reason = options.has("reason") ? options.getString("reason") : "Unbanned by " + ctx.getMember().getAsMention();
		ctx.getGuild().unban(user).reason(reason).queue(success ->
				ctx.reply("Unbanned " + user.getAsMention() + " with reason: " + reason),
			error -> ctx.error("Failed to unban " + user.getAsMention() + " for reason: `" + error.getMessage() + "`")
		);
	}

}
