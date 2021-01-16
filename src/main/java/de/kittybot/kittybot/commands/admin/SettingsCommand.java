package de.kittybot.kittybot.commands.admin;

import de.kittybot.kittybot.command.Category;
import de.kittybot.kittybot.command.application.Command;
import de.kittybot.kittybot.command.context.CommandContext;
import de.kittybot.kittybot.command.interaction.Options;
import de.kittybot.kittybot.command.options.*;
import de.kittybot.kittybot.command.response.Response;
import de.kittybot.kittybot.modules.SettingsModule;
import de.kittybot.kittybot.utils.MessageUtils;
import net.dv8tion.jda.api.Permission;

@SuppressWarnings("unused")
public class SettingsCommand extends Command{

	public SettingsCommand(){
		super("settings", "Let's you see/change settings", Category.ADMIN);
		addOptions(
				new PrefixCommand(),
				new DJRoleCommand(),
				new AnnouncementChannelCommand(),
				new JoinMessageCommand(),
				new LeaveMessageCommand(),
				new LogMessagesCommand()
		);
		addPermissions(Permission.MANAGE_SERVER);
	}

	public static class PrefixCommand extends SubCommand{


		public PrefixCommand(){
			super("prefix", "Sets the prefix");
			addOptions(
					new CommandOptionString("prefix", "The new prefix").setRequired()
			);
		}

		@Override
		public void run(Options options, CommandContext ctx){
			var prefix = options.getString("prefix");
			ctx.get(SettingsModule.class).setPrefix(ctx.getGuildId(), prefix);
			ctx.reply(new Response.Builder().setContent("Prefix set to: `" + prefix + "`").build());
		}

	}

	public static class DJRoleCommand extends SubCommand{

		public DJRoleCommand(){
			super("djrole", "Sets the dj role");
			addOptions(
					new CommandOptionRole("role", "The new dj role").setRequired()
			);
		}

		@Override
		public void run(Options options, CommandContext ctx){
			var roleId = options.getLong("role");
			ctx.get(SettingsModule.class).setDjRoleId(ctx.getGuildId(), roleId);
			ctx.reply(new Response.Builder().setContent("DJ Role set to: " + MessageUtils.getRoleMention(roleId)).build());
		}

	}

	public static class AnnouncementChannelCommand extends SubCommand{

		public AnnouncementChannelCommand(){
			super("announcementchannel", "Sets the announcement channel");
			addOptions(
					new CommandOptionChannel("channel", "The new announcement channel").setRequired()
			);
		}

		@Override
		public void run(Options options, CommandContext ctx){
			var channelId = options.getLong("channel");
			ctx.get(SettingsModule.class).setAnnouncementChannelId(ctx.getGuildId(), channelId);
			ctx.reply(new Response.Builder().setContent("Announcement channel set to: " + MessageUtils.getChannelMention(channelId)).build());
		}

	}

	public static class NSFWCommand extends SubCommand{

		public NSFWCommand(){
			super("nsfw", "Sets whether nsfw commands are enabled");
			addOptions(
					new CommandOptionBoolean("enabled", "Whether nsfw commands are enabled").setRequired()
			);
		}

		@Override
		public void run(Options options, CommandContext ctx){
			var enabled = options.getBoolean("enabled");
			ctx.get(SettingsModule.class).setNsfwEnabled(ctx.getGuildId(), enabled);
			ctx.reply(new Response.Builder().setContent("NSFW commands " + (enabled ? "enabled" : "disabled")).build());
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
				ctx.reply(new Response.Builder().setContent("Join message `" + (settings.areJoinMessagesEnabled(ctx.getGuildId()) ? "enabled" : "disabled")  + "` and set to:\n" + settings.getJoinMessage(ctx.getGuildId())).build());
				return;
			}
			ctx.reply(new Response.Builder().setContent(returnMessage).build());
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
				ctx.reply(new Response.Builder().setContent("Leave message `" + (settings.areLeaveMessagesEnabled(ctx.getGuildId()) ? "enabled" : "disabled")  + "` and set to:\n" + settings.getLeaveMessage(ctx.getGuildId())).build());
				return;
			}
			ctx.reply(new Response.Builder().setContent(returnMessage).build());
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
				ctx.reply(new Response.Builder().setContent("Log message `" + (settings.areLogMessagesEnabled(ctx.getGuildId()) ? "enabled" : "disabled")  + "` and send to channel " +
						MessageUtils.getChannelMention(settings.getLogChannelId(ctx.getGuildId()))).build());
				return;
			}
			ctx.reply(new Response.Builder().setContent(returnMessage).build());
		}

	}

}
