package de.kittybot.kittybot.commands.streams;

import de.kittybot.kittybot.commands.admin.SettingsCommand;
import de.kittybot.kittybot.modules.SettingsModule;
import de.kittybot.kittybot.modules.StreamAnnouncementModule;
import de.kittybot.kittybot.objects.streams.StreamType;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionChannel;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionString;
import de.kittybot.kittybot.slashcommands.application.options.GuildSubCommand;
import de.kittybot.kittybot.slashcommands.application.options.SubCommandGroup;
import de.kittybot.kittybot.slashcommands.interaction.GuildInteraction;
import de.kittybot.kittybot.slashcommands.interaction.Options;
import de.kittybot.kittybot.utils.MessageUtils;

import java.util.stream.Collectors;

public class StreamAnnouncementsCommand extends SubCommandGroup{

	public StreamAnnouncementsCommand(){
		super("streamannouncements", "Used to configure stream announcements");
		addOptions(
			new AddCommand(),
			new RemoveCommand(),
			new ListCommand(),
			new MessageCommand(),
			new ChannelCommand()
		);
	}

	private static class AddCommand extends GuildSubCommand{

		public AddCommand(){
			super("add", "Adds a new stream announcement for twitch");
			addOptions(
                /*new CommandOptionInteger("service", "Which service the stream is from").required()
                    .addChoices(
                        new CommandOptionChoice<>("twitch", 1)/*,
                                    new CommandOptionChoice<>("youtube", 2)
                    ),*/
				new CommandOptionString("username", "The username of the streamer").required()
			);
		}

		@Override
		public void run(Options options, GuildInteraction ia){
			var type = StreamType.TWITCH;//StreamType.byId(options.getInt("service"));
			var username = options.getString("username");
			var user = ia.get(StreamAnnouncementModule.class).add(username, ia.getGuildId(), type);
			if(user == null){
				ia.error("No user found with username " + username + "for " + type.getName());
				return;
			}
			ia.reply("Stream announcement for " + type.getName() + " with username: " + user.getDisplayName() + " added");
		}

	}

	private static class RemoveCommand extends GuildSubCommand{

		public RemoveCommand(){
			super("remove", "Removes a stream announcement");
			addOptions(
                /*new CommandOptionInteger("service", "Which service the stream is from").required()
                    .addChoices(
                        new CommandOptionChoice<>("twitch", 1)/*,
                        new CommandOptionChoice<>("youtube", 0)
                    ),*/
				new CommandOptionString("username", "The username of the streamer").required()
			);
		}

		@Override
		public void run(Options options, GuildInteraction ia){
			var type = StreamType.TWITCH;//StreamType.byId(options.getInt("service"));
			var username = options.getString("username");
			var success = ia.get(StreamAnnouncementModule.class).remove(username, ia.getGuildId(), type);
			if(!success){
				ia.error("Could not find stream announcement for " + type.getName() + " with username: " + username + ". Check your spelling");
				return;
			}
			ia.reply("Stream announcement for " + type.getName() + " with username: " + username + " removed");
		}

	}

	private static class ListCommand extends GuildSubCommand{

		public ListCommand(){
			super("list", "Lists stream announcements");
		}

		@Override
		public void run(Options options, GuildInteraction ia){
			var streamAnnouncements = ia.get(StreamAnnouncementModule.class).get(ia.getGuildId());
			if(streamAnnouncements.isEmpty()){
				ia.error("No stream announcements found. Create them with `/settings streamannouncements add <service> <username>`");
				return;
			}
			ia.reply("**Stream Announcements:**\n" + streamAnnouncements.stream().map(sa -> MessageUtils.maskLink(sa.getUserName(), "https://twitch.tv/" + sa.getUserName()) + " on " + StreamType.byId(sa.getStreamType()).getName()).collect(Collectors.joining("\n")));
		}

	}

	private static class MessageCommand extends GuildSubCommand{

		public MessageCommand(){
			super("message", "Sets the stream announcement message template");
			addOptions(
				new CommandOptionString("message", "The message template").required()
			);
		}

		@Override
		public void run(Options options, GuildInteraction ia){
			var message = options.getString("message");

			ia.get(SettingsModule.class).setStreamAnnouncementMessage(ia.getGuildId(), message);
			ia.reply("Set stream announcements template to:\n" + message);
		}

	}

	private static class ChannelCommand extends GuildSubCommand{

		public ChannelCommand(){
			super("channel", "Sets the stream announcement channel");
			addOptions(
				new CommandOptionChannel("channel", "The channel which stream announcements should get send to").required()
			);
		}

		@Override
		public void run(Options options, GuildInteraction ia){
			var channel = options.getTextChannel("channel");
			ia.get(SettingsModule.class).setStreamAnnouncementChannelId(ia.getGuildId(), channel.getIdLong());
			ia.reply("Stream announcements now get send to " + channel.getAsMention());
		}

	}

}
