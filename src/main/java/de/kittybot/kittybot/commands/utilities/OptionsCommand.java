package de.kittybot.kittybot.commands.utilities;

import de.kittybot.kittybot.cache.PrefixCache;
import de.kittybot.kittybot.database.Database;
import de.kittybot.kittybot.objects.command.ACommand;
import de.kittybot.kittybot.objects.command.Category;
import de.kittybot.kittybot.objects.command.CommandContext;
import de.kittybot.kittybot.utils.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;

import java.util.Arrays;

public class OptionsCommand extends ACommand{

	public static final String COMMAND = "options";
	public static final String USAGE = "options <prefix/announcementchannel/joinmessage/leavemessage/boostmessage/nsfw> <value>";
	public static final String DESCRIPTION = "Used to set some guild specified options";
	protected static final String[] ALIASES = {"opts", "opt"};
	protected static final Category CATEGORY = Category.UTILITIES;

	public OptionsCommand(){
		super(COMMAND, USAGE, DESCRIPTION, ALIASES, CATEGORY);
	}

	@Override
	public void run(CommandContext ctx){
		if(ctx.getMember().isOwner() || ctx.getMember().hasPermission(Permission.ADMINISTRATOR)){
			if(ctx.getArgs().length == 0){
				var guildId = ctx.getGuild().getId();
				sendAnswer(ctx, new EmbedBuilder()
						.setTitle("Current guild options:")
						.addField("Prefix:", PrefixCache.getCommandPrefix(guildId), false)
						.addField("Announcement Channel:", "<#" + Database.getAnnouncementChannelId(guildId) + ">", false)

						.addField("Join Messages Enabled:", String.valueOf(Database.getJoinMessageEnabled(guildId)), true)
						.addField("Join Message:", Database.getJoinMessage(guildId), true)
						.addBlankField(true)

						.addField("Leave Messages Enabled:", String.valueOf(Database.getLeaveMessageEnabled(guildId)), true)
						.addField("Leave Message:", Database.getLeaveMessage(guildId), true)
						.addBlankField(true)

						.addField("Boost Messages Enabled:", String.valueOf(Database.getBoostMessageEnabled(guildId)), true)
						.addField("Boost Message:", Database.getBoostMessage(guildId), true)
						.addBlankField(true)

						.addField("NSFW Enabled:", String.valueOf(Database.getNSFWEnabled(guildId)), false));
			}
			else{
				var joined = String.join(" ", Arrays.copyOfRange(ctx.getArgs(), 1, ctx.getArgs().length));
				if(ctx.getArgs()[0].equalsIgnoreCase("prefix") && ctx.getArgs().length == 2){
					PrefixCache.setCommandPrefix(ctx.getGuild().getId(), ctx.getArgs()[1]);
					sendAnswer(ctx, "Prefix set to: `" + ctx.getArgs()[1] + "`");
				}
				else if(ctx.getArgs()[0].equalsIgnoreCase("nsfw")){
					if(ctx.getArgs().length >= 2){
						if(Utils.isEnable(ctx.getArgs()[1])){
							Database.setNSFWEnabled(ctx.getGuild().getId(), true);
							sendAnswer(ctx, "NSFW `activated`");
						}
						else if(Utils.isDisable(ctx.getArgs()[1])){
							Database.setNSFWEnabled(ctx.getGuild().getId(), false);
							sendAnswer(ctx, "NSFW `deactivated`");
						}
						else{
							sendUsage(ctx, "options nsfw <on|off|yes|no|on|off|ja|nein>");
						}
					}
					else{
						var state = Database.getNSFWEnabled(ctx.getGuild().getId());
						Database.setNSFWEnabled(ctx.getGuild().getId(), !state);
						sendAnswer(ctx, "NSFW set to: `" + (state ? "deactivated" : "activated") + "`");
					}
				}
				else if(ctx.getArgs()[0].equalsIgnoreCase("announcementchannel")){
					var channels = ctx.getMessage().getMentionedChannels();
					if(channels.size() == 1){
						Database.setAnnouncementChannelId(ctx.getGuild().getId(), channels.get(0).getId());
						sendAnswer(ctx, channels.get(0).getAsMention() + " set as announcement channel!");
					}
					else{
						sendUsage(ctx, "options announcement <#TextChannel>");
					}
				}
				else if(ctx.getArgs()[0].equalsIgnoreCase("joinmessage")){
					if(ctx.getArgs().length < 2){
						Database.setJoinMessage(ctx.getGuild().getId(), joined);
						sendAnswer(ctx, "Join message set to: " + joined);
					}
					else if(Utils.isHelp(ctx.getArgs()[1])){
						sendUsage(ctx, "options joinmessage <message>");
					}
					else if(ctx.getArgs()[1].equalsIgnoreCase("enable") || ctx.getArgs()[1].equalsIgnoreCase("true") || ctx.getArgs()[1].equalsIgnoreCase("on") || ctx.getArgs()[1].equalsIgnoreCase("an")){
						Database.setJoinMessageEnabled(ctx.getGuild().getId(), true);
						sendAnswer(ctx, "Join messages enabled!");
					}
					else if(Utils.isDisable(ctx.getArgs()[1])){
						Database.setJoinMessageEnabled(ctx.getGuild().getId(), false);
						sendAnswer(ctx, "Join messages disabled!");
					}
				}
				else if(ctx.getArgs()[0].equalsIgnoreCase("leavemessage")){
					if(ctx.getArgs().length < 2){
						Database.setLeaveMessage(ctx.getGuild().getId(), joined);
						sendAnswer(ctx, "Leave message set to: " + joined);
					}
					else if(Utils.isHelp(ctx.getArgs()[1])){
						sendUsage(ctx, "options leavemessage <message>");
					}
					else if(Utils.isEnable(ctx.getArgs()[1])){
						Database.setLeaveMessageEnabled(ctx.getGuild().getId(), true);
						sendAnswer(ctx, "Leave messages enabled!");
					}
					else if(Utils.isDisable(ctx.getArgs()[1])){
						Database.setLeaveMessageEnabled(ctx.getGuild().getId(), false);
						sendAnswer(ctx, "Leave messages disabled!");
					}
				}
				else if(ctx.getArgs()[0].equalsIgnoreCase("boostmessage")){
					if(ctx.getArgs().length < 2){
						Database.setBoostMessage(ctx.getGuild().getId(), joined);
						sendAnswer(ctx, "Boost message set to: " + joined);
					}
					else if(Utils.isHelp(ctx.getArgs()[1])){
						sendUsage(ctx, "options boostmessage <message>");
					}
					else if(Utils.isEnable(ctx.getArgs()[1])){
						Database.setBoostMessageEnabled(ctx.getGuild().getId(), true);
						sendAnswer(ctx, "Boost messages enabled!");
					}
					else if(Utils.isDisable(ctx.getArgs()[1])){
						Database.setBoostMessageEnabled(ctx.getGuild().getId(), false);
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
