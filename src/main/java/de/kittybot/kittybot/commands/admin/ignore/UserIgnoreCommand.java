package de.kittybot.kittybot.commands.admin.ignore;

import de.kittybot.kittybot.modules.SettingsModule;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionUser;
import de.kittybot.kittybot.slashcommands.application.options.GuildSubCommand;
import de.kittybot.kittybot.slashcommands.application.options.SubCommandGroup;
import de.kittybot.kittybot.slashcommands.interaction.GuildInteraction;
import de.kittybot.kittybot.slashcommands.interaction.Options;
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
		public void run(Options options, GuildInteraction ia){
			var userId = options.getLong("user");
			ia.get(SettingsModule.class).addBotIgnoredUsers(ia.getGuildId(), Collections.singleton(userId));
			ia.reply("Disabled commands for " + MessageUtils.getUserMention(userId));
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
		public void run(Options options, GuildInteraction ia){
			var userId = options.getLong("user");
			ia.get(SettingsModule.class).removeBotIgnoredUsers(ia.getGuildId(), Collections.singleton(userId));
			ia.reply("Enabled commands for " + MessageUtils.getUserMention(userId));

		}

	}

	private static class ListCommand extends GuildSubCommand{

		public ListCommand(){
			super("list", "Used to list ignored a users");
		}

		@Override
		public void run(Options options, GuildInteraction ia){
			var users = ia.get(SettingsModule.class).getBotIgnoredUsers(ia.getGuildId());
			ia.reply("**Commands are disabled for following users:**\n" + users.stream().map(MessageUtils::getUserMention).collect(Collectors.joining(", ")));
		}

	}

}
