package de.kittybot.kittybot.commands.admin.ignore;

import de.kittybot.kittybot.command.context.CommandContext;
import de.kittybot.kittybot.command.interaction.Options;
import de.kittybot.kittybot.command.options.CommandOptionUser;
import de.kittybot.kittybot.command.options.SubCommand;
import de.kittybot.kittybot.command.options.SubCommandGroup;
import de.kittybot.kittybot.modules.SettingsModule;
import de.kittybot.kittybot.utils.MessageUtils;

import java.util.Collections;
import java.util.stream.Collectors;

public class UserIgnoreCommand extends SubCommandGroup{

	public UserIgnoreCommand(){
		super("users", "Used to list/ignore/unignore a user");
		addOptions(
				new AddCommand(),
				new RemoveCommand(),
				new ListCommand()
		);
	}

	public static class AddCommand extends SubCommand{

		public AddCommand(){
			super("add", "Used to ignore a user");
			addOptions(
					new CommandOptionUser("user", "User to ignore").setRequired()
			);
		}

		@Override
		public void run(Options options, CommandContext ctx){
			var userId = options.getLong("user");
			ctx.get(SettingsModule.class).addBotIgnoredUsers(ctx.getGuildId(), Collections.singleton(userId));
			ctx.reply("Disabled commands for " + MessageUtils.getUserMention(userId));
		}

	}

	public static class RemoveCommand extends SubCommand{

		public RemoveCommand(){
			super("remove", "Used to unignore a user");
			addOptions(
					new CommandOptionUser("user", "User to unignore").setRequired()
			);
		}

		@Override
		public void run(Options options, CommandContext ctx){
			var userId = options.getLong("user");
			ctx.get(SettingsModule.class).removeBotIgnoredUsers(ctx.getGuildId(), Collections.singleton(userId));
			ctx.reply("Enabled commands for " + MessageUtils.getUserMention(userId));

		}

	}

	public static class ListCommand extends SubCommand{

		public ListCommand(){
			super("list", "Used to list ignored a users");
		}

		@Override
		public void run(Options options, CommandContext ctx){
			var users = ctx.get(SettingsModule.class).getBotIgnoredUsers(ctx.getGuildId());
			ctx.reply("**Commands are disabled for following users:**\n" + users.stream().map(MessageUtils::getUserMention).collect(Collectors.joining(", ")));
		}

	}

}
