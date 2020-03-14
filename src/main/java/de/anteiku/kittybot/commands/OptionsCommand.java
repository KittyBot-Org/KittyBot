package de.anteiku.kittybot.commands;

import de.anteiku.kittybot.KittyBot;
import de.anteiku.kittybot.utils.API;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

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
	public void run(String[] args, GuildMessageReceivedEvent event){
		if(args.length > 0){
			if(event.getMember().isOwner() || event.getMember().hasPermission(Permission.ADMINISTRATOR)){
				if(args[0].equalsIgnoreCase("prefix") && args.length == 2){
					main.database.setCommandPrefix(event.getGuild().getId(), args[1]);
					sendAnswer(event.getMessage(), "Prefix set to: `" + args[1] + "`");
				}
				else if(args[0].equalsIgnoreCase("nsfw")){
					if(args.length >= 2){
						if(args[1].equalsIgnoreCase("true") || args[1].equalsIgnoreCase("ja") || args[1].equalsIgnoreCase("yes") || args[1].equalsIgnoreCase("on")){
							main.database.setNSFWEnabled(event.getGuild().getId(), true);
							sendAnswer(event.getMessage(), "NSFW `activated`");
						}
						else if(args[1].equalsIgnoreCase("false") || args[1].equalsIgnoreCase("nein") || args[1].equalsIgnoreCase("no") || args[1].equalsIgnoreCase("off")){
							main.database.setNSFWEnabled(event.getGuild().getId(), false);
							sendAnswer(event.getMessage(), "NSFW `deactivated`");
						}
						else{
							sendUsage(event.getMessage(), "options nsfw <on|off|yes|no|on|off|ja|nein>");
						}
					}
					else{
						main.database.setNSFWEnabled(event.getGuild().getId(), main.database.getNSFWEnabled(event.getGuild().getId()));
						String state;
						if(main.database.getNSFWEnabled(event.getGuild().getId())){
							state = "activated";
						}
						else{
							state = "deactivated";
						}
						sendAnswer(event.getMessage(), "NSFW set to: `" + state + "`");
					}
				}
				else if(args[0].equalsIgnoreCase("welcomechannel")){
					List<TextChannel> channels = event.getMessage().getMentionedChannels();
					if(channels.size() == 1){
						main.database.setWelcomeChannelId(event.getGuild().getId(), channels.get(0).getId());
						sendAnswer(event.getMessage(), channels.get(0).getAsMention() + " set as welcome channel!");
					}
					else{
						sendUsage(event.getMessage(), "options welcomechannel <#TextChannel>");
					}
				}
				else if(args[0].equalsIgnoreCase("welcomemessage")){
					if(args[1].equalsIgnoreCase("?") || args[1].equalsIgnoreCase("help")){
						sendUsage(event.getMessage(), "options welcomemessage <message> ([randomwelcomemessage] = random Discord welcome message, [username] = joined member)");
						return;
					}
					else if(args[1].equalsIgnoreCase("enable") || args[1].equalsIgnoreCase("true") || args[1].equalsIgnoreCase("on") || args[1].equalsIgnoreCase("an")){
						main.database.setWelcomeMessageEnabled(event.getGuild().getId(), true);
						sendAnswer(event.getMessage(), "Welcome messages enabled!");
						return;
					}
					else if(args[1].equalsIgnoreCase("disable") || args[1].equalsIgnoreCase("false") || args[1].equalsIgnoreCase("off") || args[1].equalsIgnoreCase("aus")){
						main.database.setWelcomeMessageEnabled(event.getGuild().getId(), false);
						sendAnswer(event.getMessage(), "Welcome messages disabled!");
						return;
					}
					String message = String.join(" ", API.subArray(args, 1));
					main.database.setWelcomeMessage(event.getGuild().getId(), message);
					sendAnswer(event.getMessage(), "Welcome message set to: ");
				}
				else{
					sendUsage(event.getMessage());
				}
			}
			else{
				sendError(event.getMessage(), "You need to be an administrator to use this command!");
			}
		}
		else{
			sendUsage(event.getMessage());
		}
	}
	
}
