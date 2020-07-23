package de.anteiku.kittybot.commands.commands;

import de.anteiku.kittybot.KittyBot;
import de.anteiku.kittybot.commands.ACommand;
import de.anteiku.kittybot.commands.CommandContext;
import de.anteiku.kittybot.database.Database;
import de.anteiku.kittybot.Utils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.List;

public class OptionsCommand extends ACommand{

	public static String COMMAND = "options";
	public static String USAGE = "options <prefix|welcomechannel|welcomemessage|nsfw> <value>";
	public static String DESCRIPTION = "Used to set some guild specified options";
	protected static String[] ALIAS = {"opts", "opt"};

	public OptionsCommand(KittyBot main){
		super(main, COMMAND, USAGE, DESCRIPTION, ALIAS);
	}

	//TODO renaming sub-commands & displaying set values
	@Override
	public void run(CommandContext ctx){
		if(ctx.getArgs().length > 0){
			if(ctx.getMember().isOwner() || ctx.getMember().hasPermission(Permission.ADMINISTRATOR)){
				if(ctx.getArgs()[0].equalsIgnoreCase("prefix") && ctx.getArgs().length == 2){
					if(Database.setCommandPrefix(ctx.getGuild().getId(), ctx.getArgs()[1])){
						sendError(ctx, "There was an error while processing your command :(");
						return;
					}
					sendAnswer(ctx, "Prefix set to: `" + ctx.getArgs()[1] + "`");
				}
				else if(ctx.getArgs()[0].equalsIgnoreCase("nsfw")){
					if(ctx.getArgs().length >= 2){
						if(ctx.getArgs()[1].equalsIgnoreCase("true") || ctx.getArgs()[1].equalsIgnoreCase("ja") || ctx.getArgs()[1].equalsIgnoreCase("yes") || ctx.getArgs()[1].equalsIgnoreCase("on")){
							if(Database.setNSFWEnabled(ctx.getGuild().getId(), true)){
								sendError(ctx, "There was an error while processing your command :(");
								return;
							}
							sendAnswer(ctx, "NSFW `activated`");
						}
						else if(ctx.getArgs()[1].equalsIgnoreCase("false") || ctx.getArgs()[1].equalsIgnoreCase("nein") || ctx.getArgs()[1].equalsIgnoreCase("no") || ctx.getArgs()[1].equalsIgnoreCase("off")){
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
						if(Database.setNSFWEnabled(ctx.getGuild().getId(), Database.getNSFWEnabled(ctx.getGuild().getId()))){
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
				else if(ctx.getArgs()[0].equalsIgnoreCase("welcomechannel")){
					List<TextChannel> channels = ctx.getMessage().getMentionedChannels();
					if(channels.size() == 1){
						if(Database.setWelcomeChannelId(ctx.getGuild().getId(), channels.get(0).getId())){
							sendError(ctx, "There was an error while processing your command :(");
							return;
						}
						sendAnswer(ctx, channels.get(0).getAsMention() + " set as welcome channel!");
					}
					else{
						sendUsage(ctx, "options welcomechannel <#TextChannel>");
					}
				}
				else if(ctx.getArgs()[0].equalsIgnoreCase("welcomemessage")){
					if(ctx.getArgs()[1].equalsIgnoreCase("?") || ctx.getArgs()[1].equalsIgnoreCase("help")){
						sendUsage(ctx, "options welcomemessage <message> ([randomwelcomemessage] = random Discord welcome message, [username] = joined member)");
						return;
					}
					else if(ctx.getArgs()[1].equalsIgnoreCase("enable") || ctx.getArgs()[1].equalsIgnoreCase("true") || ctx.getArgs()[1].equalsIgnoreCase("on") || ctx.getArgs()[1].equalsIgnoreCase("an")){
						if(Database.setWelcomeMessageEnabled(ctx.getGuild().getId(), true)){
							sendError(ctx, "There was an error while processing your command :(");
							return;
						}
						sendAnswer(ctx, "Welcome messages enabled!");
						return;
					}
					else if(ctx.getArgs()[1].equalsIgnoreCase("disable") || ctx.getArgs()[1].equalsIgnoreCase("false") || ctx.getArgs()[1].equalsIgnoreCase("off") || ctx.getArgs()[1].equalsIgnoreCase("aus")){
						if(Database.setWelcomeMessageEnabled(ctx.getGuild().getId(), false)){
							sendError(ctx, "There was an error while processing your command :(");
							return;
						}
						sendAnswer(ctx, "Welcome messages disabled!");
						return;
					}
					String message = String.join(" ", Utils.subArray(ctx.getArgs(), 1));
					if(Database.setWelcomeMessage(ctx.getGuild().getId(), message)){
						sendError(ctx, "There was an error while processing your command :(");
						return;
					}
					sendAnswer(ctx, "Welcome message set to: ");
				}
				else{
					sendUsage(ctx);
				}
			}
			else{
				sendError(ctx, "You need to be an administrator to use this command!");
			}
		}
		else{
			sendUsage(ctx);
		}
	}

}
