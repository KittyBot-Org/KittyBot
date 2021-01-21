package de.kittybot.kittybot.commands.admin;

import de.kittybot.kittybot.modules.SettingsModule;
import de.kittybot.kittybot.modules.StreamAnnouncementModule;
import de.kittybot.kittybot.objects.enums.Emoji;
import de.kittybot.kittybot.objects.streams.StreamType;
import de.kittybot.kittybot.slashcommands.application.Category;
import de.kittybot.kittybot.slashcommands.application.Command;
import de.kittybot.kittybot.slashcommands.application.CommandOptionChoice;
import de.kittybot.kittybot.slashcommands.application.options.*;
import de.kittybot.kittybot.slashcommands.context.CommandContext;
import de.kittybot.kittybot.slashcommands.context.Options;
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
			new StreamAnnouncementsCommand()
		);
		addPermissions(Permission.ADMINISTRATOR);
	}

	private static class ListCommand extends SubCommand{


		public ListCommand(){
			super("list", "Lists the current settings");
		}

		@Override
		public void run(Options options, CommandContext ctx){
			var guildId = ctx.getGuildId();
			var settings = ctx.get(SettingsModule.class).getSettings(guildId);
			ctx.reply(new EmbedBuilder()
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
				//.addField("Inactive Role: " + TimeUtils.formatDurationDHMS(settings.getInactiveDuration()), settings.getLogChannel(), false)
			);
		}

	}

	private static class DJRoleCommand extends SubCommand{

		public DJRoleCommand(){
			super("djrole", "Sets the dj role");
			addOptions(
				new CommandOptionRole("role", "The new dj role").required()
			);
		}

		@Override
		public void run(Options options, CommandContext ctx){
			var roleId = options.getLong("role");
			ctx.get(SettingsModule.class).setDjRoleId(ctx.getGuildId(), roleId);
			ctx.reply(new InteractionResponse.Builder().setContent("DJ Role set to: " + MessageUtils.getRoleMention(roleId)).build());
		}

	}

	private static class AnnouncementChannelCommand extends SubCommand{

		public AnnouncementChannelCommand(){
			super("announcementchannel", "Sets the announcement channel");
			addOptions(
				new CommandOptionChannel("channel", "The new announcement channel").required()
			);
		}

		@Override
		public void run(Options options, CommandContext ctx){
			var channelId = options.getLong("channel");
			ctx.get(SettingsModule.class).setAnnouncementChannelId(ctx.getGuildId(), channelId);
			ctx.reply(new InteractionResponse.Builder().setContent("Announcement channel set to: " + MessageUtils.getChannelMention(channelId)).build());
		}

	}

	private static class JoinMessageCommand extends SubCommand{

		public JoinMessageCommand(){
			super("joinmessage", "Sets or enable/disables join messages");
			addOptions(
				new CommandOptionBoolean("enabled", "Whether join messages are enabled"),
				new CommandOptionString("message", "The join message template")
			);
		}

		@Override
		public void run(Options options, CommandContext ctx){
			var settings = ctx.get(SettingsModule.class);
			var returnMessage = "";
			if(options.has("enabled")){
				var enabled = options.getBoolean("enabled");
				settings.setJoinMessagesEnabled(ctx.getGuildId(), enabled);
				returnMessage += "Join messages `" + (enabled ? "enabled" : "disabled") + "`\n";
			}

			if(options.has("message")){
				var message = options.getString("message");
				settings.setJoinMessage(ctx.getGuildId(), message);
				returnMessage += "Join message to:\n" + message + "\n";
			}

			if(returnMessage.isBlank()){
				ctx.reply(new InteractionResponse.Builder().setContent("Join message `" + (settings.areJoinMessagesEnabled(ctx.getGuildId()) ? "enabled" : "disabled") + "` and set to:\n" + settings.getJoinMessage(ctx.getGuildId())).build());
				return;
			}
			ctx.reply(new InteractionResponse.Builder().setContent(returnMessage).build());
		}

	}

	private static class LeaveMessageCommand extends SubCommand{

		public LeaveMessageCommand(){
			super("leavemessage", "Sets or enable/disables leave messages");
			addOptions(
				new CommandOptionBoolean("enabled", "Whether leave messages are enabled"),
				new CommandOptionString("message", "The leave message template")
			);
		}

		@Override
		public void run(Options options, CommandContext ctx){
			var settings = ctx.get(SettingsModule.class);
			var returnMessage = "";
			if(options.has("enabled")){
				var enabled = options.getBoolean("enabled");
				settings.setLeaveMessagesEnabled(ctx.getGuildId(), enabled);
				returnMessage += "Leave messages `" + (enabled ? "enabled" : "disabled") + "`\n";
			}

			if(options.has("message")){
				var message = options.getString("message");
				settings.setLeaveMessage(ctx.getGuildId(), message);
				returnMessage += "Leave message to:\n" + message + "\n";
			}

			if(returnMessage.isBlank()){
				ctx.reply(new InteractionResponse.Builder().setContent("Leave message `" + (settings.areLeaveMessagesEnabled(ctx.getGuildId()) ? "enabled" : "disabled") + "` and set to:\n" + settings.getLeaveMessage(ctx.getGuildId())).build());
				return;
			}
			ctx.reply(new InteractionResponse.Builder().setContent(returnMessage).build());
		}

	}

	private static class NsfwCommand extends SubCommand{

		public NsfwCommand(){
			super("nsfw", "Enables/Disables nsfw commands");
			addOptions(
				new CommandOptionBoolean("enabled", "Whether nsfw commands are enabled").required()
			);
		}

		@Override
		public void run(Options options, CommandContext ctx){
			var enabled = options.getBoolean("enabled");
			ctx.get(SettingsModule.class).setNsfwEnabled(ctx.getGuildId(), enabled);
			ctx.reply((enabled ? "Enabled" : "Disabled") + "nsfw commands");
		}

	}

	private static class LogMessagesCommand extends SubCommand{

		public LogMessagesCommand(){
			super("logmessages", "Sets the logging channel or enable/disables log messages");
			addOptions(
				new CommandOptionBoolean("enabled", "Whether log messages are enabled"),
				new CommandOptionChannel("channel", "The log message channel")
			);
		}

		@Override
		public void run(Options options, CommandContext ctx){
			var settings = ctx.get(SettingsModule.class);
			var returnMessage = "";
			if(options.has("enabled")){
				var enabled = options.getBoolean("enabled");
				settings.setLogMessagesEnabled(ctx.getGuildId(), enabled);
				returnMessage += "Log messages `" + (enabled ? "enabled" : "disabled") + "`\n";
			}

			if(options.has("channel")){
				var channelId = options.getLong("channel");
				settings.setLogChannelId(ctx.getGuildId(), channelId);
				returnMessage += "Log channel to:\n" + MessageUtils.getChannelMention(channelId) + "\n";
			}

			if(returnMessage.isBlank()){
				ctx.reply(new InteractionResponse.Builder().setContent("Log message `" + (settings.areLogMessagesEnabled(ctx.getGuildId()) ? "enabled" : "disabled") + "` and send to channel " +
					MessageUtils.getChannelMention(settings.getLogChannelId(ctx.getGuildId()))).build());
				return;
			}
			ctx.reply(new InteractionResponse.Builder().setContent(returnMessage).build());
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

		private static class ChannelCommand extends SubCommand{

			public ChannelCommand(){
				super("channel", "Used to enable/disable snipes in a specific channel");
				addOptions(
					new CommandOptionChannel("channel", "The channel to enable/disable snipes").required(),
					new CommandOptionBoolean("enabled", "Whether to enable/disable snipes").required()
				);
			}

			@Override
			public void run(Options options, CommandContext ctx){
				var channelId = options.getLong("channel");
				var enabled = options.getBoolean("enabled");
				ctx.get(SettingsModule.class).setSnipesDisabledInChannel(ctx.getGuildId(), channelId, enabled);
				ctx.reply("Snipes `" + (enabled ? "enabled" : "disabled") + "` in " + MessageUtils.getChannelMention(channelId));
			}

		}

		private static class EnableCommand extends SubCommand{

			public EnableCommand(){
				super("enable", "Used to globally disable snipes");
				addOptions(
					new CommandOptionBoolean("enabled", "Whether to enable/disable snipes globally")
				);
			}

			@Override
			public void run(Options options, CommandContext ctx){
				var enabled = options.getBoolean("enabled");
				ctx.get(SettingsModule.class).setSnipesEnabled(ctx.getGuildId(), enabled);
				ctx.reply("Snipes globally `" + (enabled ? "enabled" : "disabled") + "`");
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

		private static class AddCommand extends SubCommand{

			public AddCommand(){
				super("add", "Adds a new stream announcement");
				addOptions(
					new CommandOptionInteger("service", "Which service the stream is from").required()
						.addChoices(
							new CommandOptionChoice<>("twitch", 0)/*,
										new CommandOptionChoice<>("youtube", 0)*/
						),
					new CommandOptionString("username", "The username of the streamer").required()
				);
			}

			@Override
			public void run(Options options, CommandContext ctx){
				var type = StreamType.byId(options.getInt("service"));
				var username = options.getString("username");
				var user = ctx.get(StreamAnnouncementModule.class).add(username, ctx.getGuildId(), type);
				if(user == null){
					ctx.error("No user found with username " + username + "for " + type.getName());
					return;
				}
				ctx.reply("Stream announcement for " + type.getName() + " with username: " + user.getDisplayName() + " added");
			}

		}

		private static class RemoveCommand extends SubCommand{

			public RemoveCommand(){
				super("remove", "Removes a stream announcement");
				addOptions(
					new CommandOptionInteger("service", "Which service the stream is from").required()
						.addChoices(
							new CommandOptionChoice<>("twitch", 1)/*,
							new CommandOptionChoice<>("youtube", 0)*/
						),
					new CommandOptionString("username", "The username of the streamer").required()
				);
			}

			@Override
			public void run(Options options, CommandContext ctx){
				var type = StreamType.byId(options.getInt("service"));
				var username = options.getString("username");
				var success = ctx.get(StreamAnnouncementModule.class).remove(username, ctx.getGuildId(), type);
				if(!success){
					ctx.error("Could not find stream announcement for " + type.getName() + " with username: " + username + ". Check your spelling");
					return;
				}
				ctx.reply("Stream announcement for " + type.getName() + " with username: " + username + " removed");
			}

		}

		private static class ListCommand extends SubCommand{

			public ListCommand(){
				super("list", "Lists stream announcements");
			}

			@Override
			public void run(Options options, CommandContext ctx){
				var streamAnnouncements = ctx.get(StreamAnnouncementModule.class).get(ctx.getGuildId());
				if(streamAnnouncements.isEmpty()){
					ctx.error("No stream announcements found. Create them with `/settings streamannouncements add <service> <username>`");
					return;
				}
				ctx.reply("**Stream Announcements:**\n" + streamAnnouncements.stream().map(sa -> MessageUtils.maskLink(sa.getUserName(), sa.getStreamUrl()) + " on " + sa.getStreamType().getName()).collect(Collectors.joining("\n")));
			}

		}

		private static class MessageCommand extends SubCommand{

			public MessageCommand(){
				super("message", "Sets the stream announcement message template");
				addOptions(
					new CommandOptionString("message", "The message template").required()
				);
			}

			@Override
			public void run(Options options, CommandContext ctx){
				var message = options.getString("message");

				ctx.get(SettingsModule.class).setStreamAnnouncementMessage(ctx.getGuildId(), message);
				ctx.reply("Set stream announcements template to:\n" + message);
			}

		}

		private static class ChannelCommand extends SubCommand{

			public ChannelCommand(){
				super("channel", "Sets the stream announcement channel");
				addOptions(
					new CommandOptionChannel("channel", "The channel which stream announcements should get send to").required()
				);
			}

			@Override
			public void run(Options options, CommandContext ctx){
				var channelId = options.getLong("channel");
				var channel = ctx.getGuild().getTextChannelById(channelId);
				if(channel == null){
					ctx.error("Please provide a valid text channel. No voice channel/category");
					return;
				}
				ctx.get(SettingsModule.class).setStreamAnnouncementChannelId(ctx.getGuildId(), channelId);
				ctx.reply("Stream announcements now get send to " + MessageUtils.getChannelMention(channelId));
			}

		}

	}

}
