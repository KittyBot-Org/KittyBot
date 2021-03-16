package de.kittybot.kittybot.commands.admin.ban;

import de.kittybot.kittybot.slashcommands.GuildCommandContext;
import de.kittybot.kittybot.slashcommands.Options;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionInteger;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionString;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionUser;
import de.kittybot.kittybot.slashcommands.application.options.GuildSubCommand;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.utils.MarkdownSanitizer;

public class AddCommand extends GuildSubCommand{

	public AddCommand(){
		super("add", "Creates a new ban");
		addOptions(
			new CommandOptionUser("user", "The user to ban").required(),
			new CommandOptionString("reason", "The ban reason"),
			new CommandOptionInteger("del-days", "How many days of messages to delete")
		);
	}

	@Override
	public void run(Options options, GuildCommandContext ctx){
		if(!ctx.getSelfMember().hasPermission(Permission.BAN_MEMBERS)){
			ctx.error("I don't have the required permission to ban members");
			return;
		}
		var user = options.getUser("user");
		if(user.getIdLong() == ctx.getUserId()){
			ctx.error("You can't ban yourself");
		}
		var member = options.getMember("user");
		if(member != null){
			if(!ctx.getSelfMember().canInteract(member)){
				ctx.error("I can't interact with this member");
				return;
			}
		}
		var reason = options.has("reason") ? options.getString("reason") : "Banned by " + ctx.getMember().getAsMention();
		var delDays = options.has("del-days") ? options.getInt("del-days") : 0;
		ctx.getGuild().ban(user, delDays, reason).reason(reason).queue(success ->
				ctx.reply("Banned `" + MarkdownSanitizer.escape(user.getAsTag()) + "`(`" + user.getId() + "`)\nReason: " + reason + "\nDeleted messages of last " + delDays + " days"),
			error -> ctx.error("Failed to ban " + user.getAsMention() + ".\nReason: `" + error.getMessage() + "`")
		);
	}

}
