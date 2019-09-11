package de.anteiku.kittybot.commands;

import de.anteiku.kittybot.API;
import de.anteiku.kittybot.Emotes;
import de.anteiku.kittybot.KittyBot;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionAddEvent;

import java.awt.*;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RolesCommand extends Command{
	
	public static String COMMAND = "roles";
	public static String USAGE = "roles <add|remove|list>";
	public static String DESCRIPTION = "Used to manage your roles";
	public static String[] ALIAS = {"r", "rollen"};
	
	public static String title = "Self-assignable roles:";
	
	public RolesCommand(KittyBot main){
		super(main, COMMAND, USAGE, DESCRIPTION, ALIAS);
	}
	
	private Map<Role, String> getRoleEmoteMap(Guild guild){
		Set<String> roleIds = main.database.getSelfAssignableRoles(guild.getId());
		Map<Role, String> map = new LinkedHashMap<>();
		int i = 0;
		for(String roleId : roleIds){
			Role role = guild.getRoleById(roleId);
			if(role == null){
				main.database.removeSelfAssignableRoles(guild.getId(), roleId);
			}
			else{
				map.put(role, API.parseEmote(i));
			}
			i++;
		}
		return map;
	}
	
	@Override
	public void run(String[] args, GuildMessageReceivedEvent event){
		if(args.length > 0){
			if(args[0].equals("?") || args[0].equals("help")){
				sendUsage(event.getChannel());
			}
			if(event.getMember().isOwner() || event.getMember().hasPermission(Permission.ADMINISTRATOR)){
				List<Role> roles = event.getMessage().getMentionedRoles();
				if(roles.size() > 0){
					if(args[0].equalsIgnoreCase("add")){
						main.database.addSelfAssignableRoles(event.getGuild().getId(), API.toArray(roles));
						sendAnswer(event.getChannel(), "Roles added!");
					}
					else if(args[0].equalsIgnoreCase("remove")){
						main.database.removeSelfAssignableRoles(event.getGuild().getId(), API.toArray(roles));
						sendAnswer(event.getChannel(), "Roles removed!");
					}
					else{
						event.getMessage().addReaction(Emotes.QUESTIONMARK).queue();
						sendUsage(event.getChannel());
					}
				}
				else{
					if(args[0].equalsIgnoreCase("list")){
						Map<Role, String> map = getRoleEmoteMap(event.getGuild());
						if(map.size() == 0){
							sendAnswer(event.getChannel(), "There are no roles added!");
						}
						else{
							String message = "";
							for(Map.Entry<Role, String> m : map.entrySet()){
								message += m.getKey().getAsMention() + ", ";
							}
							sendAnswer(event.getChannel(), "Roles: " + message);
						}
					}
					else{
						event.getMessage().addReaction(Emotes.QUESTIONMARK).queue();
						sendError(event.getChannel(), "Please mention roles!");
					}
				}
			}
			else{
				event.getMessage().addReaction(Emotes.X).queue();
				sendError(event.getChannel(), "You need to be an administrator to use this command!");
			}
		}
		else{
			Map<Role, String> roles = getRoleEmoteMap(event.getGuild());
			if(roles.size() == 0){
				sendError(event.getChannel(), "No self-assignable roles configured!\nIf you are an admin use `.roles add @role @role ...` to add roles!");
				return;
			}
			EmbedBuilder eb = new EmbedBuilder();
			eb.setTitle(title);
			eb.appendDescription("To get a role react to this message with the specified Emote\nTo remove react another time!");
			eb.setColor(Color.MAGENTA);
			String value = "";
			for(Map.Entry<Role, String> k : roles.entrySet()){
				value += k.getValue() + Emotes.blank.getAsMention() + Emotes.blank.getAsMention() + k.getKey().getAsMention() + "\n";
			}
			eb.addField("**Emote:**" + Emotes.blank.getAsMention() + "**Role:**", value, true);
			Message message = event.getChannel().sendMessage(eb.build()).complete();
			main.commandManager.addListenerCmd(message, event.getMessage(), this, - 1L);
			for(Map.Entry<Role, String> r : roles.entrySet()){
				message.addReaction(r.getValue()).queue();
			}
			message.addReaction(Emotes.WASTEBASKET).queue();
		}
	}
	
	@Override
	public void reactionAdd(Message command, GuildMessageReactionAddEvent event){
		Map<Role, String> roles = getRoleEmoteMap(event.getGuild());
		for(Map.Entry<Role, String> r : roles.entrySet()){
			if(event.getReactionEmote().getName().equals(r.getValue())){
				if(event.getMember().getRoles().contains(r.getKey())){
					event.getGuild().getController().removeSingleRoleFromMember(event.getMember(), r.getKey()).queue();
				}
				else{
					event.getGuild().getController().addSingleRoleToMember(event.getMember(), r.getKey()).queue();
					
				}
				event.getReaction().removeReaction(event.getUser()).queue();
			}
		}
	}
	
}
