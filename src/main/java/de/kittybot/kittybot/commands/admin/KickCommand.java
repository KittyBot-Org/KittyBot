package de.kittybot.kittybot.commands.admin;

import de.kittybot.kittybot.slashcommands.GuildCommandContext;
import de.kittybot.kittybot.slashcommands.Options;
import de.kittybot.kittybot.slashcommands.application.Category;
import de.kittybot.kittybot.slashcommands.application.RunGuildCommand;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionString;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionUser;
import de.kittybot.kittybot.utils.MessageUtils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.utils.MarkdownSanitizer;

@SuppressWarnings("unused")
public class KickCommand extends RunGuildCommand{

	public KickCommand(){
		super("kick", "Kicks a member", Category.ADMIN);
		addOptions(
			new CommandOptionUser("user", "The user to kick").required(),
			new CommandOptionString("reason", "The kick reason")
		);
		addPermissions(Permission.KICK_MEMBERS);
	}

	@Override
	public void run(Options options, GuildCommandContext ctx){
		if(!ctx.getSelfMember().hasPermission(Permission.KICK_MEMBERS)){
			ctx.error("I don't have the required permission to kick members");
			return;
		}
		var user = options.getUser("user");
		if(user.getIdLong() == ia.getUserId()){
			ia.error("You can't kick yourself");
		}
		var member = options.getMember("user");
		if(member != null){
			if(!ia.getSelfMember().canInteract(member)){
				ia.error("I can't interact with this member");
				return;
			}
		}
		var reason = options.getOrDefault("reason", "Kicked by " + ia.getMember().getAsMention());
		ia.getGuild().kick(user.getId(), reason).reason(reason).queue(success ->
				ia.reply("Kicked `" + MarkdownSanitizer.escape(user.getAsTag()) + "`(`" + user.getId() + "`).\nReason: " + reason),
			error -> ia.error("Failed to kick " + user.getAsMention() + ".\nReason: `" + error.getMessage() + "`")
		);
	}

}
