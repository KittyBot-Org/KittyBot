package de.anteiku.kittybot.commands.utilities;

import de.anteiku.kittybot.objects.Emojis;
import de.anteiku.kittybot.objects.cache.GuildSettingsCache;
import de.anteiku.kittybot.objects.command.ACommand;
import de.anteiku.kittybot.objects.command.Category;
import de.anteiku.kittybot.objects.command.CommandContext;
import de.anteiku.kittybot.utils.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;

public class SettingsCommand extends ACommand{

	public static final String COMMAND = "settings";
	public static final String USAGE = "settings <prefix/joinchannel/joinmessage/nsfw/djrole> <value>";
	public static final String DESCRIPTION = "Used to set some guild specified settings";
	protected static final String[] ALIASES = {"sett", "options"};
	protected static final Category CATEGORY = Category.UTILITIES;

	public SettingsCommand(){
		super(COMMAND, USAGE, DESCRIPTION, ALIASES, CATEGORY);
	}

	//TODO renaming sub-commands & displaying set values
	@Override
	public void run(CommandContext ctx){
		if(ctx.getMember().isOwner() || ctx.getMember().hasPermission(Permission.ADMINISTRATOR)){
			if(ctx.getArgs().length == 0){
				var settings = GuildSettingsCache.getGuildSettings(ctx.getGuild().getId());
				sendAnswer(ctx, new EmbedBuilder()
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
						.addField("DJ Role:", settings.getDJRole(), false));
			}
			else{
				if(ctx.getArgs()[0].equalsIgnoreCase("prefix") && ctx.getArgs().length == 2){
					GuildSettingsCache.setCommandPrefix(ctx.getGuild().getId(), ctx.getArgs()[1]);
					sendAnswer(ctx, "Prefix set to: `" + ctx.getArgs()[1] + "`");
				}
				else if(ctx.getArgs()[0].equalsIgnoreCase("djrole")){
					var roles = ctx.getMessage().getMentionedRoles();
					if(roles.isEmpty()){
						sendUsage(ctx, "options djrole @role");
					}
					else{
						if(GuildSettingsCache.setDJRoleId(ctx.getGuild().getId(), roles.get(0).getId())){
							sendError(ctx, "There was an error while processing your command :(");
							return;
						}
						sendAnswer(ctx, roles.get(0).getAsMention() + " set as DJ role!");
					}
				}
				else if(ctx.getArgs()[0].equalsIgnoreCase("nsfw")){
					if(ctx.getArgs().length >= 2){
						if(Utils.isEnable(ctx.getArgs()[1])){
							if(GuildSettingsCache.setNSFWEnabled(ctx.getGuild().getId(), true)){
								sendError(ctx, "There was an error while processing your command :(");
								return;
							}
							sendAnswer(ctx, "NSFW `activated`");
						}
						else if(Utils.isDisable(ctx.getArgs()[1])){
							if(GuildSettingsCache.setNSFWEnabled(ctx.getGuild().getId(), false)){
								sendError(ctx, "There was an error while processing your command :(");
								return;
							}
							sendAnswer(ctx, "NSFW `deactivated`");
						}
						else{
							sendUsage(ctx, "options nsfw <on|off|yes|no|on|off|ja|nein>");
						}
					}
					else{
						if(GuildSettingsCache.setNSFWEnabled(ctx.getGuild().getId(), !GuildSettingsCache.isNSFWEnabled(ctx.getGuild().getId()))){
							sendError(ctx, "There was an error while processing your command :(");
							return;
						}
						String state;
						if(GuildSettingsCache.isNSFWEnabled(ctx.getGuild().getId())){
							state = "activated";
						}
						else{
							state = "deactivated";
						}
						sendAnswer(ctx, "NSFW set to: `" + state + "`");
					}
				}
				else if(ctx.getArgs()[0].equalsIgnoreCase("announcementchannel")){
					var channels = ctx.getMessage().getMentionedChannels();
					if(channels.size() == 1){
						if(GuildSettingsCache.setAnnouncementChannelId(ctx.getGuild().getId(), channels.get(0).getId())){
							sendError(ctx, "There was an error while processing your command :(");
							return;
						}
						sendAnswer(ctx, channels.get(0).getAsMention() + " set as announcement channel!");
					}
					else{
						sendUsage(ctx, "options announcement #textChannel");
					}
				}
				else if(ctx.getArgs()[0].equalsIgnoreCase("joinmessage")){
					if(ctx.getArgs().length < 2){
						String message = String.join(" ", Utils.subArray(ctx.getArgs(), 1));
						if(GuildSettingsCache.setJoinMessage(ctx.getGuild().getId(), message)){
							sendError(ctx, "There was an error while processing your command :(");
							return;
						}
						sendAnswer(ctx, "Join message set to: " + message);
					}
					else if(Utils.isHelp(ctx.getArgs()[1])){
						sendUsage(ctx, "options joinmessage <message>");
					}
					else if(ctx.getArgs()[1].equalsIgnoreCase("enable") || ctx.getArgs()[1].equalsIgnoreCase("true") || ctx.getArgs()[1].equalsIgnoreCase("on") || ctx.getArgs()[1]
							.equalsIgnoreCase("an")){
						if(GuildSettingsCache.setJoinMessagesEnabled(ctx.getGuild().getId(), true)){
							sendError(ctx, "There was an error while processing your command :(");
							return;
						}
						sendAnswer(ctx, "Join messages enabled!");
					}
					else if(Utils.isDisable(ctx.getArgs()[1])){
						if(GuildSettingsCache.setJoinMessagesEnabled(ctx.getGuild().getId(), false)){
							sendError(ctx, "There was an error while processing your command :(");
							return;
						}
						sendAnswer(ctx, "Join messages disabled!");
					}
				}
				else if(ctx.getArgs()[0].equalsIgnoreCase("leavemessage")){
					if(ctx.getArgs().length < 2){
						String message = String.join(" ", Utils.subArray(ctx.getArgs(), 1));
						if(GuildSettingsCache.setJoinMessage(ctx.getGuild().getId(), message)){
							sendError(ctx, "There was an error while processing your command :(");
							return;
						}
						sendAnswer(ctx, "Leave message set to: " + message);
					}
					else if(Utils.isHelp(ctx.getArgs()[1])){
						sendUsage(ctx, "options leavemessage <message>");
					}
					else if(Utils.isEnable(ctx.getArgs()[1])){
						if(GuildSettingsCache.setJoinMessagesEnabled(ctx.getGuild().getId(), true)){
							sendError(ctx, "There was an error while processing your command :(");
							return;
						}
						sendAnswer(ctx, "Leave messages enabled!");
					}
					else if(Utils.isDisable(ctx.getArgs()[1])){
						if(GuildSettingsCache.setJoinMessagesEnabled(ctx.getGuild().getId(), false)){
							sendError(ctx, "There was an error while processing your command :(");
							return;
						}
						sendAnswer(ctx, "Leave messages disabled!");
					}
				}
				else if(ctx.getArgs()[0].equalsIgnoreCase("boostmessage")){
					if(ctx.getArgs().length < 2){
						String message = String.join(" ", Utils.subArray(ctx.getArgs(), 1));
						if(GuildSettingsCache.setJoinMessage(ctx.getGuild().getId(), message)){
							sendError(ctx, "There was an error while processing your command :(");
							return;
						}
						sendAnswer(ctx, "Boost message set to: " + message);
					}
					else if(Utils.isHelp(ctx.getArgs()[1])){
						sendUsage(ctx, "options boostmessage <message>");
					}
					else if(Utils.isEnable(ctx.getArgs()[1])){
						if(GuildSettingsCache.setJoinMessagesEnabled(ctx.getGuild().getId(), true)){
							sendError(ctx, "There was an error while processing your command :(");
							return;
						}
						sendAnswer(ctx, "Boost messages enabled!");
					}
					else if(Utils.isDisable(ctx.getArgs()[1])){
						if(GuildSettingsCache.setJoinMessagesEnabled(ctx.getGuild().getId(), false)){
							sendError(ctx, "There was an error while processing your command :(");
							return;
						}
						sendAnswer(ctx, "Boost messages disabled!");
					}
				}
				else{
					sendUsage(ctx);
				}
			}
		}
		else{
			sendError(ctx, "You need to be an administrator to use this command!");
		}
	}

}
