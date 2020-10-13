package de.kittybot.kittybot.commands.utilities;

import de.kittybot.kittybot.cache.GuildSettingsCache;
import de.kittybot.kittybot.objects.Emojis;
import de.kittybot.kittybot.objects.command.ACommand;
import de.kittybot.kittybot.objects.command.Category;
import de.kittybot.kittybot.objects.command.CommandContext;
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
		if(ctx.getArgs().length == 0){
			var settings = GuildSettingsCache.getGuildSettings(ctx.getGuild().getId());
			ACommand.sendAnswer(ctx, new EmbedBuilder()
					.setTitle("Guild settings:")
					.addField("Command Prefix:", settings.getCommandPrefix(), false)
					.addField("Announcement Channel:", settings.getAnnouncementChannel(), false)

					//.addField("Join Messages Enabled:", String.valueOf(settings.areJoinMessagesEnabled()), true)
					.addField("Join Messages: " + (settings.areJoinMessagesEnabled() ? Emojis.X : Emojis.CHECK), settings.getJoinMessage(), false)

					//.addField("Leave Messages Enabled:", String.valueOf(settings.areLeaveMessagesEnabled()), true)
					.addField("Leave Messages: " + (settings.areLeaveMessagesEnabled() ? Emojis.X : Emojis.CHECK), settings.getLeaveMessage(), false)

					//.addField("Boost Messages Enabled:", String.valueOf(settings.areBoostMessagesEnabled()), true)
					.addField("Boost Messages: " + (settings.areBoostMessagesEnabled() ? Emojis.X : Emojis.CHECK), settings.getBoostMessage(), false)

					.addField("NSFW Enabled: " + (settings.isNSFWEnabled() ? Emojis.X : Emojis.CHECK), "", false)
			);
		}
		else{
			var joined = String.join(" ", Arrays.copyOfRange(ctx.getArgs(), 1, ctx.getArgs().length));
			if(ctx.getArgs()[0].equalsIgnoreCase("prefix") && ctx.getArgs().length == 2){
				GuildSettingsCache.setCommandPrefix(ctx.getGuild().getId(), ctx.getArgs()[1]);
				this.sendAnswer(ctx, "Prefix set to: `" + ctx.getArgs()[1] + "`");
			}
			else if(ctx.getArgs()[0].equalsIgnoreCase("nsfw")){
				if(ctx.getArgs().length >= 2){
					if(Utils.isEnable(ctx.getArgs()[1])){
						GuildSettingsCache.setNSFWEnabled(ctx.getGuild().getId(), true);
						this.sendAnswer(ctx, "NSFW `activated`");
					}
					else if(Utils.isDisable(ctx.getArgs()[1])){
						GuildSettingsCache.setNSFWEnabled(ctx.getGuild().getId(), false);
						this.sendAnswer(ctx, "NSFW `deactivated`");
					}
					else{
						ACommand.sendUsage(ctx, "options nsfw <on|off|yes|no|on|off|ja|nein>");
					}
				}
				else{
					var state = GuildSettingsCache.isNSFWEnabled(ctx.getGuild().getId());
					GuildSettingsCache.setNSFWEnabled(ctx.getGuild().getId(), !state);
					this.sendAnswer(ctx, "NSFW set to: `" + (state ? "deactivated" : "activated") + "`");
				}
			}
			else if(ctx.getArgs()[0].equalsIgnoreCase("announcementchannel")){
				var channels = ctx.getMessage().getMentionedChannels();
				if(channels.size() == 1){
					GuildSettingsCache.setAnnouncementChannelId(ctx.getGuild().getId(), channels.get(0).getId());
					this.sendAnswer(ctx, channels.get(0).getAsMention() + " set as announcement channel!");
				}
				else{
					ACommand.sendUsage(ctx, "options announcement <#TextChannel>");
				}
			}
			else if(ctx.getArgs()[0].equalsIgnoreCase("joinmessage")){
				if(ctx.getArgs().length < 2){
					GuildSettingsCache.setJoinMessage(ctx.getGuild().getId(), joined);
					this.sendAnswer(ctx, "Join message set to: " + joined);
				}
				else if(Utils.isHelp(ctx.getArgs()[1])){
					ACommand.sendUsage(ctx, "options joinmessage <message>");
				}
				else if(ctx.getArgs()[1].equalsIgnoreCase("enable") || ctx.getArgs()[1].equalsIgnoreCase("true") || ctx.getArgs()[1].equalsIgnoreCase("on") || ctx.getArgs()[1].equalsIgnoreCase("an")){
					GuildSettingsCache.setJoinMessagesEnabled(ctx.getGuild().getId(), true);
					this.sendAnswer(ctx, "Join messages enabled!");
				}
				else if(Utils.isDisable(ctx.getArgs()[1])){
					GuildSettingsCache.setJoinMessagesEnabled(ctx.getGuild().getId(), false);
					this.sendAnswer(ctx, "Join messages disabled!");
				}
			}
			else if(ctx.getArgs()[0].equalsIgnoreCase("leavemessage")){
				if(ctx.getArgs().length < 2){
					GuildSettingsCache.setLeaveMessage(ctx.getGuild().getId(), joined);
					this.sendAnswer(ctx, "Leave message set to: " + joined);
				}
				else if(Utils.isHelp(ctx.getArgs()[1])){
					ACommand.sendUsage(ctx, "options leavemessage <message>");
				}
				else if(Utils.isEnable(ctx.getArgs()[1])){
					GuildSettingsCache.setLeaveMessagesEnabled(ctx.getGuild().getId(), true);
					this.sendAnswer(ctx, "Leave messages enabled!");
				}
				else if(Utils.isDisable(ctx.getArgs()[1])){
					GuildSettingsCache.setLeaveMessagesEnabled(ctx.getGuild().getId(), false);
					this.sendAnswer(ctx, "Leave messages disabled!");
				}
			}
			else if(ctx.getArgs()[0].equalsIgnoreCase("boostmessage")){
				if(ctx.getArgs().length < 2){
					GuildSettingsCache.setBoostMessage(ctx.getGuild().getId(), joined);
					this.sendAnswer(ctx, "Boost message set to: " + joined);
				}
				else if(Utils.isHelp(ctx.getArgs()[1])){
					ACommand.sendUsage(ctx, "options boostmessage <message>");
				}
				else if(Utils.isEnable(ctx.getArgs()[1])){
					GuildSettingsCache.setBoostMessagesEnabled(ctx.getGuild().getId(), true);
					this.sendAnswer(ctx, "Boost messages enabled!");
				}
				else if(Utils.isDisable(ctx.getArgs()[1])){
					GuildSettingsCache.setBoostMessagesEnabled(ctx.getGuild().getId(), false);
					this.sendAnswer(ctx, "Boost messages disabled!");
				}
			}
			else{
				this.sendUsage(ctx);
			}
		}
	}

}
