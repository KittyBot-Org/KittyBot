package de.kittybot.kittybot.commands.admin.ignore;

import de.kittybot.kittybot.modules.SettingsModule;
import de.kittybot.kittybot.slashcommands.GuildCommandContext;
import de.kittybot.kittybot.slashcommands.Options;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionChannel;
import de.kittybot.kittybot.slashcommands.application.options.GuildSubCommand;
import de.kittybot.kittybot.slashcommands.application.options.SubCommandGroup;
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
		public void run(Options options, GuildCommandContext ctx){
			var channel = options.getTextChannel("channel");
			ctx.get(SettingsModule.class).setBotDisabledInChannel(ctx.getGuildId(), channel.getIdLong(), true);
			ctx.reply("Disabled commands in " + channel.getAsMention());
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
		public void run(Options options, GuildCommandContext ctx){
			var channel = options.getTextChannel("channel");
			ctx.get(SettingsModule.class).setBotDisabledInChannel(ctx.getGuildId(), channel.getIdLong(), false);
			ctx.reply("Enabled commands in " + channel.getAsMention());
		}

	}

	private static class ListCommand extends GuildSubCommand{

		public ListCommand(){
			super("list", "Used to list ignored a users");
		}

		@Override
		public void run(Options options, GuildCommandContext ctx){
			var channels = ctx.get(SettingsModule.class).getBotDisabledChannels(ctx.getGuildId());
			ctx.reply("**Commands are disabled in following channels:**\n" + channels.stream().map(MessageUtils::getChannelMention).collect(Collectors.joining(", ")));
		}

	}

}