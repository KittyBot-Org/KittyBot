package de.anteiku.kittybot.commands.commands;

import de.anteiku.kittybot.KittyBot;
import de.anteiku.kittybot.Utils;
import de.anteiku.kittybot.commands.ACommand;
import de.anteiku.kittybot.commands.CommandContext;
import de.anteiku.kittybot.database.Database;
import de.anteiku.kittybot.objects.Cache;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.List;

public class OptionsCommand extends ACommand{

	public static final String COMMAND = "options";
	public static final String USAGE = "options <prefix|welcomechannel|welcomemessage|nsfw> <value>";
	public static final String DESCRIPTION = "Used to set some guild specified options";
	protected static final String[] ALIAS = {"opts", "opt"};

	public OptionsCommand(){
		super(COMMAND, USAGE, DESCRIPTION, ALIAS);
	}

	//TODO renaming sub-commands & displaying set values
	@Override
	public void run(CommandContext ctx){
		if(ctx.getMember().isOwner() || ctx.getMember().hasPermission(Permission.ADMINISTRATOR)){
			if(ctx.getArgs().length == 0){
				var guildId = ctx.getGuild().getId();
				var embed = new EmbedBuilder();
				embed.setTitle("Guild options:");
				embed.setDescription("These are the current guild options");
				embed.addField("Command Prefix:", Cache.getCommandPrefix(guildId), false);
				embed.addField("Announcement Channel:", "<#" + Database.getAnnouncementChannelId(guildId) + ">", false);
				embed.addField("Welcome Messages Enabled:", String.valueOf(Database.getWelcomeMessageEnabled(guildId)), false);
				embed.addField("Welcome Message:", Database.getWelcomeMessage(guildId), false);
				embed.addField("Leave Messages Enabled:", String.valueOf(Database.getLeaveMessageEnabled(guildId)), false);
				embed.addField("Leave Message:", Database.getLeaveMessage(guildId), false);
				embed.addField("Boost Messages Enabled:", String.valueOf(Database.getBoostMessageEnabled(guildId)), false);
				embed.addField("Boost Message:", Database.getBoostMessage(guildId), false);
				embed.addField("NSFW Enabled:", String.valueOf(Database.getNSFWEnabled(guildId)), false);
				sendAnswer(ctx, embed);
			}
			else{
				if(ctx.getArgs()[0].equalsIgnoreCase("prefix") && ctx.getArgs().length == 2){
					Cache.setCommandPrefix(ctx.getGuild().getId(), ctx.getArgs()[1]);
					sendAnswer(ctx, "Prefix set to: `" + ctx.getArgs()[1] + "`");
				}
				else if(ctx.getArgs()[0].equalsIgnoreCase("nsfw")){
					if(ctx.getArgs().length >= 2){
						if(Utils.isEnable(ctx.getArgs()[1])){
							if(Database.setNSFWEnabled(ctx.getGuild().getId(), true)){
								sendError(ctx, "There was an error while processing your command :(");
								return;
							}
							sendAnswer(ctx, "NSFW `activated`");
						}
						else if(Utils.isDisable(ctx.getArgs()[1])){
							if(Database.setNSFWEnabled(ctx.getGuild().getId(), false)){
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
						if(Database.setNSFWEnabled(ctx.getGuild().getId(), !Database.getNSFWEnabled(ctx.getGuild().getId()))){
							sendError(ctx, "There was an error while processing your command :(");
							return;
						}
						String state;
						if(Database.getNSFWEnabled(ctx.getGuild().getId())){
							state = "activated";
						}
						else{
							state = "deactivated";
						}
						sendAnswer(ctx, "NSFW set to: `" + state + "`");
					}
				}
				else if(ctx.getArgs()[0].equalsIgnoreCase("announcementchannel")){
					List<TextChannel> channels = ctx.getMessage().getMentionedChannels();
					if(channels.size() == 1){
						if(Database.setAnnouncementChannelId(ctx.getGuild().getId(), channels.get(0).getId())){
							sendError(ctx, "There was an error while processing your command :(");
							return;
						}
						sendAnswer(ctx, channels.get(0).getAsMention() + " set as announcement channel!");
					}
					else{
						sendUsage(ctx, "options announcement <#TextChannel>");
					}
				}
				else if(ctx.getArgs()[0].equalsIgnoreCase("welcomemessage")){
					if(ctx.getArgs().length < 2){
						String message = String.join(" ", Utils.subArray(ctx.getArgs(), 1));
						if(Database.setWelcomeMessage(ctx.getGuild().getId(), message)){
							sendError(ctx, "There was an error while processing your command :(");
							return;
						}
						sendAnswer(ctx, "Welcome message set to: " + message);
					}
					else if(Utils.isHelp(ctx.getArgs()[1])){
						sendUsage(ctx, "options welcomemessage <message>");
					}
					else if(ctx.getArgs()[1].equalsIgnoreCase("enable") || ctx.getArgs()[1].equalsIgnoreCase("true") || ctx.getArgs()[1].equalsIgnoreCase("on") || ctx.getArgs()[1].equalsIgnoreCase("an")){
						if(Database.setWelcomeMessageEnabled(ctx.getGuild().getId(), true)){
							sendError(ctx, "There was an error while processing your command :(");
							return;
						}
						sendAnswer(ctx, "Welcome messages enabled!");
					}
					else if(Utils.isDisable(ctx.getArgs()[1])){
						if(Database.setWelcomeMessageEnabled(ctx.getGuild().getId(), false)){
							sendError(ctx, "There was an error while processing your command :(");
							return;
						}
						sendAnswer(ctx, "Welcome messages disabled!");
					}
				}
				else if(ctx.getArgs()[0].equalsIgnoreCase("leavemessage")){
					if(ctx.getArgs().length < 2){
						String message = String.join(" ", Utils.subArray(ctx.getArgs(), 1));
						if(Database.setWelcomeMessage(ctx.getGuild().getId(), message)){
							sendError(ctx, "There was an error while processing your command :(");
							return;
						}
						sendAnswer(ctx, "Leave message set to: "  + message);
					}
					else if(Utils.isHelp(ctx.getArgs()[1])){
						sendUsage(ctx, "options leavemessage <message>");
					}
					else if(Utils.isEnable(ctx.getArgs()[1])){
						if(Database.setWelcomeMessageEnabled(ctx.getGuild().getId(), true)){
							sendError(ctx, "There was an error while processing your command :(");
							return;
						}
						sendAnswer(ctx, "Leave messages enabled!");
					}
					else if(Utils.isDisable(ctx.getArgs()[1])){
						if(Database.setWelcomeMessageEnabled(ctx.getGuild().getId(), false)){
							sendError(ctx, "There was an error while processing your command :(");
							return;
						}
						sendAnswer(ctx, "Leave messages disabled!");
					}
				}
				else if(ctx.getArgs()[0].equalsIgnoreCase("boostmessage")){
					if(ctx.getArgs().length < 2){
						String message = String.join(" ", Utils.subArray(ctx.getArgs(), 1));
						if(Database.setWelcomeMessage(ctx.getGuild().getId(), message)){
							sendError(ctx, "There was an error while processing your command :(");
							return;
						}
						sendAnswer(ctx, "Boost message set to: " + message);
					}
					else if(Utils.isHelp(ctx.getArgs()[1])){
						sendUsage(ctx, "options boostmessage <message>");
					}
					else if(Utils.isEnable(ctx.getArgs()[1])){
						if(Database.setWelcomeMessageEnabled(ctx.getGuild().getId(), true)){
							sendError(ctx, "There was an error while processing your command :(");
							return;
						}
						sendAnswer(ctx, "Boost messages enabled!");
					}
					else if(Utils.isDisable(ctx.getArgs()[1])){
						if(Database.setWelcomeMessageEnabled(ctx.getGuild().getId(), false)){
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
