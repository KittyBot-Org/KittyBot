package de.kittybot.kittybot.commands.admin;

import de.kittybot.kittybot.slashcommands.application.Category;
import de.kittybot.kittybot.slashcommands.application.RunGuildCommand;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionString;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionUser;
import de.kittybot.kittybot.slashcommands.interaction.GuildInteraction;
import de.kittybot.kittybot.slashcommands.interaction.Options;
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
	public void run(Options options, GuildInteraction ia){
		if(!ia.getSelfMember().hasPermission(Permission.KICK_MEMBERS)){
			ia.error("I don't have the required permission to kick members");
			return;
		}
		var userId = options.getLong("user");
		ia.getGuild().retrieveMemberById(userId).queue(member -> {
				if(!ia.getSelfMember().canInteract(member)){
					ia.error("I can't interact with this member");
					return;
				}
				var reason = options.getOrDefault("reason", "Kicked by " + ia.getMember().getAsMention());
				ia.getGuild().kick(member, reason).reason(reason).queue(success ->
						ia.reply("Kicked `" + MarkdownSanitizer.escape(member.getUser().getAsTag()) + "` with reason: " + reason),
					error -> ia.error("Failed to kick " + MessageUtils.getUserMention(userId) + " for reason: `" + error.getMessage() + "`")
				);
			}, error -> ia.error("I could not find the provided user")
		);
	}

}
