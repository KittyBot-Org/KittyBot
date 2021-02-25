package de.kittybot.kittybot.commands.admin;

import de.kittybot.kittybot.modules.GuildSettingsModule;
import de.kittybot.kittybot.modules.StreamAnnouncementModule;
import de.kittybot.kittybot.objects.enums.Emoji;
import de.kittybot.kittybot.objects.streams.StreamType;
import de.kittybot.kittybot.slashcommands.application.Category;
import de.kittybot.kittybot.slashcommands.application.Command;
import de.kittybot.kittybot.slashcommands.application.options.*;
import de.kittybot.kittybot.slashcommands.interaction.GuildInteraction;
import de.kittybot.kittybot.slashcommands.interaction.Options;
import de.kittybot.kittybot.slashcommands.interaction.response.InteractionResponse;
import de.kittybot.kittybot.utils.Colors;
import de.kittybot.kittybot.utils.Config;
import de.kittybot.kittybot.utils.MessageUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;

import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class SettingsCommand extends Command{

	public SettingsCommand(){
		super("settings", "Let's you see/change settings", Category.ADMIN);
		addOptions(
			new ListCommand(),
			new DJRoleCommand(),
			new AnnouncementChannelCommand(),
			new JoinMessageCommand(),
			new LeaveMessageCommand(),
			new NsfwCommand(),
			new LogMessagesCommand(),
			new SnipesCommand(),
			new StreamAnnouncementsCommand(),
			new RoleSaverCommand()
		);
		addPermissions(Permission.ADMINISTRATOR);
	}

	private static class ListCommand extends GuildSubCommand{

		public ListCommand(){
			super("list", "Lists the current settings");
		}

		@Override
		public void run(Options options, GuildInteraction ia){
			var guildId = ia.getGuildId();
			var settings = ia.get(GuildSettingsModule.class).getSettings(guildId);
			ia.reply(new EmbedBuilder()
					.setColor(Colors.KITTYBOT_BLUE)
					.setAuthor("Guild settings:", Config.ORIGIN_URL + "/guilds/" + guildId + "/dashboard", Emoji.SETTINGS.getUrl())
					.addField("Announcement Channel: ", settings.getAnnouncementChannel(), false)
					.addField("Join Messages: " + MessageUtils.getBoolEmote(settings.areJoinMessagesEnabled()), settings.getJoinMessage(), false)
					.addField("Leave Messages: " + MessageUtils.getBoolEmote(settings.areLeaveMessagesEnabled()), settings.getLeaveMessage(), false)
					.addField("Stream Announcement Channel:", settings.getStreamAnnouncementChannel(), false)
					.addField("DJ Role: ", settings.getDjRole(), false)
					.addField("NSFW Enabled: ", MessageUtils.getBoolEmote(settings.isNsfwEnabled()), false)
					.addField("Log Messages: " + MessageUtils.getBoolEmote(settings.areLogMessagesEnabled()), settings.getLogChannel(), false)
					.addField("Snipes Enabled:", MessageUtils.getBoolEmote(settings.areSnipesEnabled()), false)
					.addField("Role Saver Enabled:", MessageUtils.getBoolEmote(settings.isRoleSaverEnabled()), false)
				//.addField("Inactive Role: " + TimeUtils.formatDurationDHMS(settings.getInactiveDuration()), settings.getLogChannel(), false)
			);
		}

	}

	private static class DJRoleCommand extends GuildSubCommand{

		public DJRoleCommand(){
			super("djrole", "Sets the dj role");
			addOptions(
				new CommandOptionRole("role", "The new dj role").required()
			);
		}

		@Override
		public void run(Options options, GuildInteraction ia){
			var role = options.getRole("role");
			ia.get(GuildSettingsModule.class).setDjRoleId(ia.getGuildId(), role.getIdLong());
			ia.reply(new InteractionResponse.Builder().setContent("DJ Role set to: " + role.getAsMention()).build());
		}

	}

	private static class AnnouncementChannelCommand extends GuildSubCommand{

		public AnnouncementChannelCommand(){
			super("announcementchannel", "Sets the announcement channel");
			addOptions(
				new CommandOptionChannel("channel", "The new announcement channel").required()
			);
		}

		@Override
		public void run(Options options, GuildInteraction ia){
			var channel = options.getTextChannel("channel");
			ia.get(GuildSettingsModule.class).setAnnouncementChannelId(ia.getGuildId(), channel.getIdLong());
			ia.reply(new InteractionResponse.Builder().setContent("Announcement channel set to: " + channel.getAsMention()).build());
		}

	}

	private static class JoinMessageCommand extends GuildSubCommand{

		public JoinMessageCommand(){
			super("joinmessage", "Sets or enable/disables join messages");
			addOptions(
				new CommandOptionBoolean("enabled", "Whether join messages are enabled"),
				new CommandOptionString("message", "The join message template")
			);
		}

		@Override
		public void run(Options options, GuildInteraction ia){
			var settings = ia.get(GuildSettingsModule.class);
			var returnMessage = "";
			if(options.has("enabled")){
				var enabled = options.getBoolean("enabled");
				settings.setJoinMessagesEnabled(ia.getGuildId(), enabled);
				returnMessage += "Join messages `" + (enabled ? "enabled" : "disabled") + "`\n";
			}

			if(options.has("message")){
				var message = options.getString("message");
				settings.setJoinMessage(ia.getGuildId(), message);
				returnMessage += "Join message to:\n" + message + "\n";
			}

			if(returnMessage.isBlank()){
				ia.reply(new InteractionResponse.Builder().setContent("Join message `" + (settings.areJoinMessagesEnabled(ia.getGuildId()) ? "enabled" : "disabled") + "` and set to:\n" + settings.getJoinMessage(ia.getGuildId())).build());
				return;
			}
			ia.reply(new InteractionResponse.Builder().setContent(returnMessage).build());
		}

	}

	private static class LeaveMessageCommand extends GuildSubCommand{

		public LeaveMessageCommand(){
			super("leavemessage", "Sets or enable/disables leave messages");
			addOptions(
				new CommandOptionBoolean("enabled", "Whether leave messages are enabled"),
				new CommandOptionString("message", "The leave message template")
			);
		}

		@Override
		public void run(Options options, GuildInteraction ia){
			var settings = ia.get(GuildSettingsModule.class);
			var returnMessage = "";
			if(options.has("enabled")){
				var enabled = options.getBoolean("enabled");
				settings.setLeaveMessagesEnabled(ia.getGuildId(), enabled);
				returnMessage += "Leave messages `" + (enabled ? "enabled" : "disabled") + "`\n";
			}

			if(options.has("message")){
				var message = options.getString("message");
				settings.setLeaveMessage(ia.getGuildId(), message);
				returnMessage += "Leave message to:\n" + message + "\n";
			}

			if(returnMessage.isBlank()){
				ia.reply(new InteractionResponse.Builder().setContent("Leave message `" + (settings.areLeaveMessagesEnabled(ia.getGuildId()) ? "enabled" : "disabled") + "` and set to:\n" + settings.getLeaveMessage(ia.getGuildId())).build());
				return;
			}
			ia.reply(new InteractionResponse.Builder().setContent(returnMessage).build());
		}

	}

	private static class NsfwCommand extends GuildSubCommand{

		public NsfwCommand(){
			super("nsfw", "Enables/Disables nsfw commands");
			addOptions(
				new CommandOptionBoolean("enabled", "Whether nsfw commands are enabled").required()
			);
		}

		@Override
		public void run(Options options, GuildInteraction ia){
			var enabled = options.getBoolean("enabled");
			ia.get(GuildSettingsModule.class).setNsfwEnabled(ia.getGuildId(), enabled);
			ia.reply((enabled ? "Enabled" : "Disabled") + "nsfw commands");
		}

	}

	private static class LogMessagesCommand extends GuildSubCommand{

		public LogMessagesCommand(){
			super("logmessages", "Sets the logging channel or enable/disables log messages");
			addOptions(
				new CommandOptionBoolean("enabled", "Whether log messages are enabled"),
				new CommandOptionChannel("channel", "The log message channel")
			);
		}

		@Override
		public void run(Options options, GuildInteraction ia){
			var settings = ia.get(GuildSettingsModule.class);
			var returnMessage = "";
			if(options.has("enabled")){
				var enabled = options.getBoolean("enabled");
				settings.setLogMessagesEnabled(ia.getGuildId(), enabled);
				returnMessage += "Log messages `" + (enabled ? "enabled" : "disabled") + "`\n";
			}

			if(options.has("channel")){
				var channel = options.getTextChannel("channel");
				settings.setLogChannelId(ia.getGuildId(), channel.getIdLong());
				returnMessage += "Log channel to:\n" + channel.getAsMention() + "\n";
			}

			if(returnMessage.isBlank()){
				ia.reply(new InteractionResponse.Builder().setContent("Log message `" + (settings.areLogMessagesEnabled(ia.getGuildId()) ? "enabled" : "disabled") + "` and send to channel " +
					MessageUtils.getChannelMention(settings.getLogChannelId(ia.getGuildId()))).build());
				return;
			}
			ia.reply(new InteractionResponse.Builder().setContent(returnMessage).build());
		}

	}

	private static class SnipesCommand extends SubCommandGroup{

		public SnipesCommand(){
			super("snipes", "Used to disable snipes");
			addOptions(
				new ChannelCommand(),
				new EnableCommand()
			);
		}

		private static class ChannelCommand extends GuildSubCommand{

			public ChannelCommand(){
				super("channel", "Used to enable/disable snipes in a specific channel");
				addOptions(
					new CommandOptionChannel("channel", "The channel to enable/disable snipes").required(),
					new CommandOptionBoolean("enabled", "Whether to enable/disable snipes").required()
				);
			}

			@Override
			public void run(Options options, GuildInteraction ia){
				var channel = options.getTextChannel("channel");
				var enabled = options.getBoolean("enabled");
				ia.get(GuildSettingsModule.class).setSnipesDisabledInChannel(ia.getGuildId(), channel.getIdLong(), !enabled);
				ia.reply("Snipes `" + (enabled ? "enabled" : "disabled") + "` in " + channel.getAsMention());
			}

		}

		private static class EnableCommand extends GuildSubCommand{

			public EnableCommand(){
				super("enable", "Used to globally disable snipes");
				addOptions(
					new CommandOptionBoolean("enabled", "Whether to enable/disable snipes globally")
				);
			}

			@Override
			public void run(Options options, GuildInteraction ia){
				var enabled = options.getBoolean("enabled");
				ia.get(GuildSettingsModule.class).setSnipesEnabled(ia.getGuildId(), enabled);
				ia.reply("Snipes globally `" + (enabled ? "enabled" : "disabled") + "`");
			}

		}

	}

	private static class StreamAnnouncementsCommand extends SubCommandGroup{

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

				ia.get(GuildSettingsModule.class).setStreamAnnouncementMessage(ia.getGuildId(), message);
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
				ia.get(GuildSettingsModule.class).setStreamAnnouncementChannelId(ia.getGuildId(), channel.getIdLong());
				ia.reply("Stream announcements now get send to " + channel.getAsMention());
			}

		}

	}

	private static class RoleSaverCommand extends GuildSubCommand{

		public RoleSaverCommand(){
			super("rolesaver", "Enabled/Disables saving of user roles on leave");
			addOptions(
				new CommandOptionBoolean("enabled", "Whether role saving is enabled or disabled").required()
			);
		}

		@Override
		public void run(Options options, GuildInteraction ia){
			var enabled = options.getBoolean("enabled");
			ia.get(GuildSettingsModule.class).setRoleSaverEnabled(ia.getGuildId(), enabled);
			ia.reply((enabled ? "Enabled" : "Disabled") + " role saving");
		}

	}

}
