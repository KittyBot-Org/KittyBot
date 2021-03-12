package de.kittybot.kittybot.commands.admin;

import de.kittybot.kittybot.slashcommands.GuildCommandContext;
import de.kittybot.kittybot.slashcommands.Options;
import de.kittybot.kittybot.slashcommands.application.Category;
import de.kittybot.kittybot.slashcommands.application.Command;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionInteger;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionString;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionUser;
import de.kittybot.kittybot.slashcommands.application.options.GuildSubCommand;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.utils.MarkdownSanitizer;

import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class BanCommand extends Command{

	public BanCommand(){
		super("ban", "Bans a member", Category.ADMIN);
		addOptions(
			new AddCommand(),
			new RemoveCommand(),
			new ListCommand()
		);
		addPermissions(Permission.BAN_MEMBERS);
	}

	private static class AddCommand extends GuildSubCommand{

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
			var member = options.getMember("user");
			if(member != null){
				if(!ctx.getSelfMember().canInteract(member)){
					ctx.error("I can't interact with this member");
					return;
				}
			}
			var user = options.getUser("user");
			var reason = options.has("reason") ? options.getString("reason") : "Banned by " + ctx.getMember().getAsMention();
			var delDays = options.has("del-days") ? options.getInt("del-days") : 0;
			ctx.getGuild().ban(user, delDays, reason).reason(reason).queue(success ->
					ctx.reply("Banned `" + MarkdownSanitizer.escape(user.getAsTag()) + "`nReason: " + reason + "\nDeleted messages of last " + delDays + " days"),
				error -> ctx.error("Failed to ban " + user.getAsMention() + " for reason: `" + error.getMessage() + "`")
			);
		}

	}

	private static class RemoveCommand extends GuildSubCommand{

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

	private static class ListCommand extends GuildSubCommand{

		public ListCommand(){
			super("list", "Lists all bans");
		}

		@Override
		public void run(Options options, GuildCommandContext ctx){
			ctx.getGuild().retrieveBanList().queue(bans -> {
					if(bans.isEmpty()){
						ctx.reply("There are no banned users yet");
						return;
					}
					ctx.reply("**Banned Users:**\n" + bans.stream().map(ban -> MarkdownSanitizer.escape(ban.getUser().getAsTag()) + "(`" + ban.getUser().getId() + "`)" + " - " + ban.getReason()).collect(Collectors.joining("\n")));
				}, error -> ctx.error("I was not able to retrieve the bans. Please give me the `ban members` permission")
			);
		}

	}

}
