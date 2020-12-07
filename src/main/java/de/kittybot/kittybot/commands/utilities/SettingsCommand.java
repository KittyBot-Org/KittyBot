package de.kittybot.kittybot.commands.utilities;

import de.kittybot.kittybot.cache.GuildSettingsCache;
import de.kittybot.kittybot.objects.Emojis;
import de.kittybot.kittybot.objects.command.ACommand;
import de.kittybot.kittybot.objects.command.Category;
import de.kittybot.kittybot.objects.command.CommandContext;
import de.kittybot.kittybot.utils.MessageUtils;
import de.kittybot.kittybot.utils.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;

import java.util.Arrays;

public class SettingsCommand extends ACommand{

	public static final String COMMAND = "settings";
	public static final String USAGE = "settings <prefix/announcementchannel/joinmessage/leavemessage/boostmessage/nsfw> <value>";
	public static final String DESCRIPTION = "Used to set guild specified settings";
	protected static final String[] ALIASES = {"opts", "opt", "set"};
	protected static final Category CATEGORY = Category.UTILITIES;

	public SettingsCommand(){
		super(COMMAND, USAGE, DESCRIPTION, ALIASES, CATEGORY);
	}

	@Override
	public void run(CommandContext ctx){
		if(!ctx.getMember().hasPermission(Permission.ADMINISTRATOR)){
			sendError(ctx, "You need to be an administrator to use this command!");
			return;
		}
		var args = ctx.getArgs();
		if(args.length == 0){
			var settings = GuildSettingsCache.getGuildSettings(ctx.getGuild().getId());
			sendSuccess(ctx, new EmbedBuilder()
					.setTitle("Guild settings:")
					.addField("Command Prefix: ", "`" + settings.getCommandPrefix() + "`", false)
					.addField("Announcement Channel: ", settings.getAnnouncementChannel(), false)
					.addField("NSFW Enabled: ", MessageUtils.getBoolEmote(settings.isNSFWEnabled()), false)
					.addField("Join Messages: " + MessageUtils.getBoolEmote(settings.areJoinMessagesEnabled()), settings.getJoinMessage(), false)
					.addField("Leave Messages: " + MessageUtils.getBoolEmote(settings.areLeaveMessagesEnabled()), settings.getLeaveMessage(), false)
					.addField("Boost Messages: " + MessageUtils.getBoolEmote(settings.areBoostMessagesEnabled()), settings.getBoostMessage(), false)
			);
		}
		else{
			var joined = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
			if(args[0].equalsIgnoreCase("prefix") && args.length == 2){
				GuildSettingsCache.setCommandPrefix(ctx.getGuild().getId(), args[1]);
				sendSuccess(ctx, "Prefix set to: `" + args[1] + "`");
			}
			else if(args[0].equalsIgnoreCase("nsfw")){
				if(args.length >= 2){
					if(Utils.isEnable(args[1])){
						GuildSettingsCache.setNSFWEnabled(ctx.getGuild().getId(), true);
						sendSuccess(ctx, "NSFW `activated`");
					}
					else if(Utils.isDisable(args[1])){
						GuildSettingsCache.setNSFWEnabled(ctx.getGuild().getId(), false);
						sendSuccess(ctx, "NSFW `deactivated`");
					}
					else{
						sendUsage(ctx, "options nsfw <on|off|yes|no|on|off|ja|nein>");
					}
				}
				else{
					var state = GuildSettingsCache.isNSFWEnabled(ctx.getGuild().getId());
					GuildSettingsCache.setNSFWEnabled(ctx.getGuild().getId(), !state);
					sendSuccess(ctx, "NSFW set to: `" + (state ? "deactivated" : "activated") + "`");
				}
			}
			else if(args[0].equalsIgnoreCase("announcementchannel")){
				var channels = ctx.getMessage().getMentionedChannels();
				if(channels.size() == 1){
					GuildSettingsCache.setAnnouncementChannelId(ctx.getGuild().getId(), channels.get(0).getId());
					sendSuccess(ctx, channels.get(0).getAsMention() + " set as announcement channel!");
				}
				else{
					sendUsage(ctx, "settings announcement <#TextChannel>");
				}
			}
			else if(args[0].equalsIgnoreCase("joinmessage")){
				if(args.length >= 2 && !Utils.isHelp(args[1])){
					if(Utils.isEnable(args[1])){
						GuildSettingsCache.setJoinMessagesEnabled(ctx.getGuild().getId(), true);
						sendSuccess(ctx, "Join messages enabled!");
					}
					else if(Utils.isDisable(args[1])){
						GuildSettingsCache.setJoinMessagesEnabled(ctx.getGuild().getId(), false);
						sendSuccess(ctx, "Join messages disabled!");
					} else {
						GuildSettingsCache.setJoinMessage(ctx.getGuild().getId(), joined);
						sendSuccess(ctx, "Join message set to:\n" + joined);
					}
				}
				else{
					sendUsage(ctx, "settings joinmessage <message>");
				}
			}
			else if(args[0].equalsIgnoreCase("leavemessage")){
				if(args.length >= 2 && !Utils.isHelp(args[1])){
					if(Utils.isEnable(args[1])){
						GuildSettingsCache.setLeaveMessagesEnabled(ctx.getGuild().getId(), true);
						sendSuccess(ctx, "Leave messages enabled!");
					}
					else if(Utils.isDisable(args[1])){
						GuildSettingsCache.setLeaveMessagesEnabled(ctx.getGuild().getId(), false);
						sendSuccess(ctx, "Leave messages disabled!");
					} else {
						GuildSettingsCache.setLeaveMessage(ctx.getGuild().getId(), joined);
						sendSuccess(ctx, "Leave message set to:\n" + joined);
					}
				}
				else{
					sendUsage(ctx, "settings leavemessage <message>");
				}
			}
			else if(args[0].equalsIgnoreCase("boostmessage")){
				if(args.length >= 2 && !Utils.isHelp(args[1])){
					if(Utils.isEnable(args[1])){
						GuildSettingsCache.setBoostMessagesEnabled(ctx.getGuild().getId(), true);
						sendSuccess(ctx, "Boost messages enabled!");
					}
					else if(Utils.isDisable(args[1])){
						GuildSettingsCache.setBoostMessagesEnabled(ctx.getGuild().getId(), false);
						sendSuccess(ctx, "Boost messages disabled!");
					} else {
						GuildSettingsCache.setBoostMessage(ctx.getGuild().getId(), joined);
						sendSuccess(ctx, "Boost message set to:\n" + joined);
					}
				}
				else{
					sendUsage(ctx, "settings boostmessage <message>");
				}
			}
			else{
				sendUsage(ctx);
			}
		}
	}

}
