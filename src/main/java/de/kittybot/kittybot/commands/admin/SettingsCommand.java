package de.kittybot.kittybot.commands.admin;

import de.kittybot.kittybot.slashcommands.application.Category;
import de.kittybot.kittybot.slashcommands.application.Command;
import de.kittybot.kittybot.slashcommands.context.CommandContext;
import de.kittybot.kittybot.slashcommands.context.Options;
import de.kittybot.kittybot.slashcommands.application.options.*;
import de.kittybot.kittybot.slashcommands.interaction.response.InteractionResponse;
import de.kittybot.kittybot.modules.SettingsModule;
import de.kittybot.kittybot.objects.Emoji;
import de.kittybot.kittybot.utils.Colors;
import de.kittybot.kittybot.utils.Config;
import de.kittybot.kittybot.utils.MessageUtils;
import de.kittybot.kittybot.utils.TimeUtils;
import net.dv8tion.jda.api.EmbedBuilder;

@SuppressWarnings("unused")
public class SettingsCommand extends Command{

	public SettingsCommand(){
		super("settings", "Let's you see/change settings", Category.ADMIN);
		addOptions(
				new ViewCommand(),
				new PrefixCommand(),
				new DJRoleCommand(),
				new AnnouncementChannelCommand(),
				new JoinMessageCommand(),
				new LeaveMessageCommand(),
				new LogMessagesCommand(),
				new SnipesCommand(),
				new StreamAnnouncementsCommand()
		);
		//addPermissions(Permission.ADMINISTRATOR);
	}

	public static class ViewCommand extends SubCommand{


		public ViewCommand(){
			super("view", "Shows the current settings");
		}

		@Override
		public void run(Options options, CommandContext ctx){
			var guildId = ctx.getGuildId();
			var settings = ctx.get(SettingsModule.class).getSettings(guildId);
			ctx.reply(new EmbedBuilder()
					.setColor(Colors.KITTYBOT_BLUE)
					.setAuthor("Guild settings:", Config.ORIGIN_URL + "/guilds/" + guildId + "/dashboard", Emoji.SETTINGS.getUrl())
					.addField("Command Prefix: ", "`" + settings.getPrefix() + "`", false)
					.addField("Announcement Channel: ", settings.getAnnouncementChannel(), false)
					.addField("DJ Role: ", settings.getDjRole(), false)
					.addField("NSFW Enabled: ", MessageUtils.getBoolEmote(settings.isNsfwEnabled()), false)
					.addField("Join Messages: " + MessageUtils.getBoolEmote(settings.areJoinMessagesEnabled()), settings.getJoinMessage(), false)
					.addField("Leave Messages: " + MessageUtils.getBoolEmote(settings.areLeaveMessagesEnabled()), settings.getLeaveMessage(), false)
					.addField("Log Messages: " + MessageUtils.getBoolEmote(settings.areLogMessagesEnabled()), settings.getLogChannel(), false)
					.addField("Inactive Role: " + TimeUtils.formatDurationDHMS(settings.getInactiveDuration()), settings.getLogChannel(), false)
			);
		}

	}

	public static class PrefixCommand extends SubCommand{


		public PrefixCommand(){
			super("prefix", "Sets the prefix");
			addOptions(
					new CommandOptionString("prefix", "The new prefix").required()
			);
		}

		@Override
		public void run(Options options, CommandContext ctx){
			var prefix = options.getString("prefix");
			if(prefix.length() > 4){
				ctx.error("The prefix can't be longer than 4");
				return;
			}
			ctx.get(SettingsModule.class).setPrefix(ctx.getGuildId(), prefix);
			ctx.reply(new InteractionResponse.Builder().setContent("Prefix set to: `" + prefix + "`").build());
		}

	}

	public static class DJRoleCommand extends SubCommand{

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

	public static class AnnouncementChannelCommand extends SubCommand{

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

	public static class NSFWCommand extends SubCommand{

		public NSFWCommand(){
			super("nsfw", "Sets whether nsfw commands are enabled");
			addOptions(
					new CommandOptionBoolean("enabled", "Whether nsfw commands are enabled").required()
			);
		}

		@Override
		public void run(Options options, CommandContext ctx){
			var enabled = options.getBoolean("enabled");
			ctx.get(SettingsModule.class).setNsfwEnabled(ctx.getGuildId(), enabled);
			ctx.reply(new InteractionResponse.Builder().setContent("NSFW commands " + (enabled ? "enabled" : "disabled")).build());
		}

	}

	public static class JoinMessageCommand extends SubCommand{

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

	public static class LeaveMessageCommand extends SubCommand{

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

	public static class LogMessagesCommand extends SubCommand{

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

	public static class SnipesCommand extends SubCommandGroup{

		public SnipesCommand(){
			super("snipes", "Used to disable snipes");
			addOptions(
					new ChannelCommand(),
					new EnableCommand()
			);
		}

		public static class ChannelCommand extends SubCommand{

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

		public static class EnableCommand extends SubCommand{

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

	public static class StreamAnnouncementsCommand extends SubCommand{

		public StreamAnnouncementsCommand(){
			super("streamannouncements", "Used to configure stream announcements");
		}

		@Override
		public void run(Options options, CommandContext ctx){

		}

	}

}
