package de.kittybot.kittybot.commands.admin.ignore;

import de.kittybot.kittybot.modules.GuildSettingsModule;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionChannel;
import de.kittybot.kittybot.slashcommands.application.options.GuildSubCommand;
import de.kittybot.kittybot.slashcommands.application.options.SubCommandGroup;
import de.kittybot.kittybot.slashcommands.interaction.GuildInteraction;
import de.kittybot.kittybot.slashcommands.interaction.Options;
import de.kittybot.kittybot.utils.MessageUtils;

import java.util.stream.Collectors;

public class ChannelIgnoreCommand extends SubCommandGroup{

	public ChannelIgnoreCommand(){
		super("channels", "Used to list/ignore/unignore a channel");
		addOptions(
			new AddCommand(),
			new RemoveCommand(),
			new ListCommand()
		);
	}

	private static class AddCommand extends GuildSubCommand{

		public AddCommand(){
			super("add", "Used to ignore a channel");
			addOptions(
				new CommandOptionChannel("channel", "Channel to ignore").required()
			);
		}

		@Override
		public void run(Options options, GuildInteraction ia){
			var channel = options.getTextChannel("channel");
			ia.get(GuildSettingsModule.class).setBotDisabledInChannel(ia.getGuildId(), channel.getIdLong(), true);
			ia.reply("Disabled commands in " + channel.getAsMention());
		}

	}

	private static class RemoveCommand extends GuildSubCommand{

		public RemoveCommand(){
			super("remove", "Used to unignore a channel");
			addOptions(
				new CommandOptionChannel("channel", "Channel to unignore").required()
			);
		}

		@Override
		public void run(Options options, GuildInteraction ia){
			var channel = options.getTextChannel("channel");
			ia.get(GuildSettingsModule.class).setBotDisabledInChannel(ia.getGuildId(), channel.getIdLong(), false);
			ia.reply("Enabled commands in " + channel.getAsMention());
		}

	}

	private static class ListCommand extends GuildSubCommand{

		public ListCommand(){
			super("list", "Used to list ignored a users");
		}

		@Override
		public void run(Options options, GuildInteraction ia){
			var channels = ia.get(GuildSettingsModule.class).getBotDisabledChannels(ia.getGuildId());
			ia.reply("**Commands are disabled in following channels:**\n" + channels.stream().map(MessageUtils::getChannelMention).collect(Collectors.joining(", ")));
		}

	}

}