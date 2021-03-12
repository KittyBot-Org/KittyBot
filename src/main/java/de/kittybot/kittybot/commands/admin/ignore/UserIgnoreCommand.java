package de.kittybot.kittybot.commands.admin.ignore;

import de.kittybot.kittybot.modules.SettingsModule;
import de.kittybot.kittybot.slashcommands.GuildCommandContext;
import de.kittybot.kittybot.slashcommands.Options;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionUser;
import de.kittybot.kittybot.slashcommands.application.options.GuildSubCommand;
import de.kittybot.kittybot.slashcommands.application.options.SubCommandGroup;
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

	private static class AddCommand extends GuildSubCommand{

		public AddCommand(){
			super("add", "Used to ignore a user");
			addOptions(
				new CommandOptionUser("user", "User to ignore").required()
			);
		}

		@Override
		public void run(Options options, GuildCommandContext ctx){
			var user = options.getUser("user");
			ctx.get(SettingsModule.class).addBotIgnoredUsers(ctx.getGuildId(), Collections.singleton(user.getIdLong()));
			ctx.reply("Disabled commands for " + user.getAsMention());
		}

	}

	private static class RemoveCommand extends GuildSubCommand{

		public RemoveCommand(){
			super("remove", "Used to unignore a user");
			addOptions(
				new CommandOptionUser("user", "User to unignore").required()
			);
		}

		@Override
		public void run(Options options, GuildCommandContext ctx){
			var user = options.getUser("user");
			ctx.get(SettingsModule.class).removeBotIgnoredUsers(ctx.getGuildId(), Collections.singleton(user.getIdLong()));
			ctx.reply("Enabled commands for " + user.getAsMention());
		}

	}

	private static class ListCommand extends GuildSubCommand{

		public ListCommand(){
			super("list", "Used to list ignored a users");
		}

		@Override
		public void run(Options options, GuildCommandContext ctx){
			var users = ctx.get(SettingsModule.class).getBotIgnoredUsers(ctx.getGuildId());
			ctx.reply("**Commands are disabled for following users:**\n" + users.stream().map(MessageUtils::getUserMention).collect(Collectors.joining(", ")));
		}

	}

}
