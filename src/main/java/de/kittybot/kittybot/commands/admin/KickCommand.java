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
		var userId = options.getLong("user");
		ctx.getGuild().retrieveMemberById(userId).queue(member -> {
			if(!ctx.getSelfMember().canInteract(member)){
				ctx.error("I can't interact with this member");
				return;
			}
			var reason = options.getString("reason", "Kicked by " + ctx.getMember().getAsMention());
			ctx.getGuild().kick(member, reason).reason(reason).queue(success ->
					ctx.reply("Kicked `" + MarkdownSanitizer.escape(member.getUser().getAsTag()) + "` with reason: " + reason),
				error -> ctx.error("Failed to kick " + MessageUtils.getUserMention(userId) + " for reason: `" + error.getMessage() + "`")
			);
			}, error -> ctx.error("I could not find the provided user")
		);
	}

}
