package de.kittybot.kittybot.commands.admin;

import de.kittybot.kittybot.command.Args;
import de.kittybot.kittybot.command.Category;
import de.kittybot.kittybot.command.Command;
import de.kittybot.kittybot.command.CommandContext;
import de.kittybot.kittybot.modules.SettingsModule;
import de.kittybot.kittybot.utils.Config;
import de.kittybot.kittybot.utils.MessageUtils;
import de.kittybot.kittybot.utils.TimeUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;

public class SettingsCommand extends Command{

	public SettingsCommand(){
		super("settings", "Used to set guild specified settings", Category.ADMIN);
		setUsage("<prefix/announcementchannel/joinmessage/leavemessage/boostmessage/nsfw> <value>");
		addAliases("opts", "opt", "set", "sett");
		addPermissions(Permission.ADMINISTRATOR);
	}

	@Override
	public void run(Args args, CommandContext ctx){
		var guildId = ctx.getGuildId();
		var settingsManager = ctx.get(SettingsModule.class);
		if(args.isEmpty()){
			var settings = settingsManager.getSettings(ctx.getGuildId());
			ctx.sendSuccess(new EmbedBuilder()
					.setAuthor("Guild settings:", Config.ORIGIN_URL + "/guilds/" + guildId + "/dashboard", "https://cdn.discordapp.com/emojis/787994539105320970.png")
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
		else{
			var joined = ctx.getRawMessage(1);
			if(args.get(0).equalsIgnoreCase("prefix") && args.size() == 2){
				settingsManager.setPrefix(guildId, args.get(1));
				ctx.sendSuccess("Prefix set to: `" + args.get(1) + "`");
			}
			else if(args.get(0).equalsIgnoreCase("nsfw")){
				if(args.size() >= 2){
					if(args.isEnable(1)){
						settingsManager.setNsfwEnabled(guildId, true);
						ctx.sendSuccess("NSFW `activated`");
					}
					else if(args.isEnable(1)){
						settingsManager.setNsfwEnabled(guildId, false);
						ctx.sendSuccess("NSFW `deactivated`");
					}
					else{
						ctx.sendUsage("options nsfw <on|off|yes|no|on|off|ja|nein>");
					}
				}
				else{
					var state = settingsManager.isNsfwEnabled(ctx.getGuildId());
					settingsManager.setNsfwEnabled(guildId, !state);
					ctx.sendSuccess("NSFW set to: `" + (state ? "deactivated" : "activated") + "`");
				}
			}
			else if(args.get(0).equalsIgnoreCase("announcementchannel")){
				var channels = ctx.getMessage().getMentionedChannels();
				if(channels.size() == 1){
					settingsManager.setAnnouncementChannelId(guildId, channels.get(0).getIdLong());
					ctx.sendSuccess(channels.get(0).getAsMention() + " set as announcement channel!");
				}
				else{
					ctx.sendUsage("options announcement <#TextChannel>");
				}
			}
			else if(args.get(0).equalsIgnoreCase("joinmessage")){
				if(args.size() < 2){
					settingsManager.setJoinMessage(guildId, joined);
					ctx.sendSuccess("Join message set to: " + joined);
				}
				else if(args.isHelp(1)){
					ctx.sendUsage("options joinmessage <message>");
				}
				else if(args.isEnable(1)){
					settingsManager.setJoinMessagesEnabled(guildId, true);
					ctx.sendSuccess("Join messages enabled!");
				}
				else if(args.isDisable(1)){
					settingsManager.setJoinMessagesEnabled(guildId, false);
					ctx.sendSuccess("Join messages disabled!");
				}
			}
			else if(args.get(0).equalsIgnoreCase("leavemessage")){
				if(args.size() < 2){
					settingsManager.setLeaveMessage(guildId, joined);
					ctx.sendSuccess("Leave message set to: " + joined);
				}
				else if(args.isHelp(1)){
					ctx.sendUsage("options leavemessage <message>");
				}
				else if(args.isEnable(1)){
					settingsManager.setLeaveMessagesEnabled(guildId, true);
					ctx.sendSuccess("Leave messages enabled!");
				}
				else if(args.isDisable(1)){
					settingsManager.setLeaveMessagesEnabled(guildId, false);
					ctx.sendSuccess("Leave messages disabled!");
				}
			}
			else{
				ctx.sendUsage(this);
			}
		}
	}

}
