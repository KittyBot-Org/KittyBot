package de.kittybot.kittybot.commands.admin;

import de.kittybot.kittybot.command.Category;
import de.kittybot.kittybot.command.Command;
import de.kittybot.kittybot.command.ctx.CommandContext;
import de.kittybot.kittybot.main.KittyBot;
import de.kittybot.kittybot.utils.MessageUtils;
import de.kittybot.kittybot.utils.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;

import java.util.List;

public class SettingsCommand extends Command{

	private final KittyBot main;

	public SettingsCommand(KittyBot main){
		super("settings", "Used to set guild specified settings", Category.ADMIN);
		setUsage("<prefix/announcementchannel/joinmessage/leavemessage/boostmessage/nsfw> <value>");
		addAliases("opts", "opt", "set", "sett");
		this.main = main;
	}

	@Override
	public void run(List<String> args, CommandContext ctx){
		if(!ctx.getMember().hasPermission(Permission.ADMINISTRATOR)){
			ctx.sendError("You need to be an administrator to use this command!");
			return;
		}
		var guildId = ctx.getGuild().getIdLong();
		var settingsManager = ctx.getCommandManager().getGuildSettingsManager();
		if(args.isEmpty()){
			var settings = settingsManager.getSettings(ctx.getGuild().getIdLong());
			ctx.sendSuccess(new EmbedBuilder()
					.setAuthor("Guild settings:", this.main.getConfig().getString("origin_url") + "/guilds/" + guildId + "/dashboard", "https://cdn.discordapp.com/emojis/787994539105320970.png")
					.addField("Command Prefix: ", "`" + settings.getCommandPrefix() + "`", false)
					.addField("Announcement Channel: ", settings.getAnnouncementChannel(), false)
					.addField("DJ Role: ", settings.getDjRole(), false)
					.addField("NSFW Enabled: ", MessageUtils.getBoolEmote(settings.isNsfwEnabled()), false)
					.addField("Join Messages: " + MessageUtils.getBoolEmote(settings.areJoinMessagesEnabled()), settings.getJoinMessage(), false)
					.addField("Leave Messages: " + MessageUtils.getBoolEmote(settings.areLeaveMessagesEnabled()), settings.getLeaveMessage(), false)
					.addField("Log Messages: " + MessageUtils.getBoolEmote(settings.areLogMessagesEnabled()), settings.getLogChannel(), false)
					.addField("Inactive Role: " + MessageUtils.formatDurationDHMS(settings.getInactiveDuration()), settings.getLogChannel(), false)
			);
		}
		else{
			var joined = String.join(" ", args.subList(1, args.size()));
			if(args.get(0).equalsIgnoreCase("prefix") && args.size() == 2){
				settingsManager.setPrefix(guildId, args.get(1));
				ctx.sendSuccess("Prefix set to: `" + args.get(1) + "`");
			}
			else if(args.get(0).equalsIgnoreCase("nsfw")){
				if(args.size() >= 2){
					if(Utils.isEnable(args.get(1))){
						settingsManager.setNsfwEnabled(guildId, true);
						ctx.sendSuccess("NSFW `activated`");
					}
					else if(Utils.isDisable(args.get(1))){
						settingsManager.setNsfwEnabled(guildId, false);
						ctx.sendSuccess("NSFW `deactivated`");
					}
					else{
						ctx.sendUsage("options nsfw <on|off|yes|no|on|off|ja|nein>");
					}
				}
				else{
					var state = settingsManager.isNsfwEnabled(ctx.getGuild().getIdLong());
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
				else if(Utils.isHelp(args.get(1))){
					ctx.sendUsage("options joinmessage <message>");
				}
				else if(Utils.isEnable(args.get(1))){
					settingsManager.setJoinMessagesEnabled(guildId, true);
					ctx.sendSuccess("Join messages enabled!");
				}
				else if(Utils.isDisable(args.get(1))){
					settingsManager.setJoinMessagesEnabled(guildId, false);
					ctx.sendSuccess("Join messages disabled!");
				}
			}
			else if(args.get(0).equalsIgnoreCase("leavemessage")){
				if(args.size() < 2){
					settingsManager.setLeaveMessage(guildId, joined);
					ctx.sendSuccess("Leave message set to: " + joined);
				}
				else if(Utils.isHelp(args.get(1))){
					ctx.sendUsage("options leavemessage <message>");
				}
				else if(Utils.isEnable(args.get(1))){
					settingsManager.setLeaveMessagesEnabled(guildId, true);
					ctx.sendSuccess("Leave messages enabled!");
				}
				else if(Utils.isDisable(args.get(1))){
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
