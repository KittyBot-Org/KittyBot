package de.kittybot.kittybot.commands.admin;

import de.kittybot.kittybot.slashcommands.application.Category;
import de.kittybot.kittybot.slashcommands.application.Command;
import de.kittybot.kittybot.slashcommands.application.options.*;
import de.kittybot.kittybot.slashcommands.context.CommandContext;
import de.kittybot.kittybot.slashcommands.context.Options;
import de.kittybot.kittybot.utils.MessageUtils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.utils.MarkdownSanitizer;

import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class BanCommand extends Command{

	public BanCommand(){
		super("ban", "Bans a member", Category.ADMIN);
		addOptions(
			new AddCommand(),
			new DeleteCommand(),
			new ListCommand()
		);
		addPermissions(Permission.BAN_MEMBERS);
	}

	private static class AddCommand extends SubCommand{


		public AddCommand(){
			super("add", "Creates a new ban");
			addOptions(
				new CommandOptionUser("user", "The user to ban").required(),
				new CommandOptionString("reason", "The ban reason"),
				new CommandOptionInteger("del-days", "How many days of messages to delete")
			);
		}

		@Override
		public void run(Options options, CommandContext ctx){
			if(!ctx.getSelfMember().hasPermission(Permission.BAN_MEMBERS)){
				ctx.error("I don't have the required permission to ban members");
				return;
			}
			var userId = options.getLong("user");
			ctx.getGuild().retrieveMemberById(userId).queue(member -> {
					if(!ctx.getSelfMember().canInteract(member)){
						ctx.error("I can't interact with this member");
						return;
					}
					var reason = options.has("reason") ? options.getString("reason") : "Banned by " + ctx.getMember().getAsMention();
					var delDays = options.has("del-days") ? options.getInt("del-days") : 0;
					ctx.getGuild().ban(member, delDays, reason).reason(reason).queue(success ->
							ctx.reply("Banned `" + MarkdownSanitizer.escape(member.getUser().getAsTag()) + "` with reason: " + reason + " and deleted messages of the last " + delDays + " days"),
						error -> ctx.error("Failed to ban " + MessageUtils.getUserMention(userId) + " for reason: `" + error.getMessage() + "`")
					);
				}, error -> ctx.error("I could not find the provided member")
			);
		}

	}

	private static class DeleteCommand extends SubCommand{


		public DeleteCommand(){
			super("delete", "Deletes a ban");
			addOptions(
				new CommandOptionLong("user-id", "The user-id to unban").required(),
				new CommandOptionString("reason", "The unban reason")
			);
		}

		@Override
		public void run(Options options, CommandContext ctx){
			var userId = options.getLong("user");
			if(userId == -1){
				ctx.error("Please provide a valid user id");
				return;
			}
			var reason = options.has("reason") ? options.getString("reason") : "Unbanned by " + ctx.getMember().getAsMention();
			ctx.getGuild().unban(User.fromId(userId)).reason(reason).queue(success ->
					ctx.reply("Unbanned " + MessageUtils.getUserMention(userId) + " with reason: " + reason),
				error -> ctx.error("Failed to unban " + MessageUtils.getUserMention(userId) + " for reason: `" + error.getMessage() + "`")
			);
		}

	}

	private static class ListCommand extends SubCommand{


		public ListCommand(){
			super("list", "Lists all bans");
		}

		@Override
		public void run(Options options, CommandContext ctx){
			ctx.getGuild().retrieveBanList().queue(bans ->
					ctx.reply("**Banned Users:**\n" + bans.stream().map(ban -> MarkdownSanitizer.escape(ban.getUser().getAsTag()) + "(`" + ban.getUser().getId() + "`)" + " - " + ban.getReason()).collect(Collectors.joining("\n")))
				, error -> ctx.error("I was not able to retrieve the bans. Please give me the `ban members` permission")
			);
		}

	}

}
