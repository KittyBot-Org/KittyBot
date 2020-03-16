package de.anteiku.kittybot.commands;

import de.anteiku.kittybot.KittyBot;
import de.anteiku.kittybot.utils.API;
import de.anteiku.kittybot.utils.Emotes;
import de.anteiku.kittybot.utils.Logger;
import de.anteiku.kittybot.utils.ValuePair;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Emote;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionAddEvent;

import java.awt.*;
import java.util.*;
import java.util.List;

public class RolesCommand extends ACommand{
	
	public static String COMMAND = "roles";
	public static String USAGE = "roles <add|remove|list>";
	public static String DESCRIPTION = "Used to manage your roles";
	protected static String[] ALIAS = {"r", "rollen"};
	
	private static String title = "Self-assignable roles:";
	
	public RolesCommand(KittyBot main){
		super(main, COMMAND, USAGE, DESCRIPTION, ALIAS);
	}
	
	private Map<Role, Emote> getRoleEmoteMap(Guild guild){
		Set<ValuePair<String, String>> roles = main.database.getSelfAssignableRoles(guild.getId());
		Map<Role, Emote> map = new LinkedHashMap<>();
		for(ValuePair<String, String> entry : roles){
			Role role = guild.getRoleById(entry.getKey());
			if(role == null){
				main.database.removeSelfAssignableRoles(guild.getId(), new HashSet<>(Collections.singleton(entry.getKey())));
			}
			else{
				map.put(role, guild.getEmoteById(entry.getValue()));
			}
		}
		return map;
	}
	
	@Override
	public void run(String[] args, GuildMessageReceivedEvent event){
		if(args.length > 0){
			if(args[0].equals("?") || args[0].equals("help")){
				sendUsage(event.getMessage());
			}
			if(event.getMember().isOwner() || event.getMember().hasPermission(Permission.ADMINISTRATOR)){
				List<Role> roles = event.getMessage().getMentionedRoles();
				List<Emote> emotes = event.getMessage().getEmotes();
				if(! roles.isEmpty()){
					if(args[0].equalsIgnoreCase("add")){
						main.database.addSelfAssignableRoles(event.getGuild().getId(), API.toMap(roles, emotes));
						sendAnswer(event.getMessage(), "Roles added!");
					}
					else if(args[0].equalsIgnoreCase("remove")){
						main.database.removeSelfAssignableRoles(event.getGuild().getId(), API.toSet(roles));
						sendAnswer(event.getMessage(), "Roles removed!");
					}
					else{
						sendUsage(event.getMessage());
					}
				}
				else{
					if(args[0].equalsIgnoreCase("list")){
						Map<Role, Emote> map = getRoleEmoteMap(event.getGuild());
						if(map.size() == 0){
							sendAnswer(event.getMessage(), "There are no roles added!");
						}
						else{
							String message = "";
							for(Map.Entry<Role, Emote> m : map.entrySet()){
								message += m.getKey().getAsMention() + ", ";
							}
							sendAnswer(event.getMessage(), "Roles: " + message);
						}
					}
					else{
						sendError(event.getMessage(), "Please mention roles!");
					}
				}
			}
			else{
				sendError(event.getMessage(), "You need to be an administrator to use this command!");
			}
		}
		else{
			Map<Role, Emote> roles = getRoleEmoteMap(event.getGuild());
			if(roles.size() == 0){
				sendError(event.getMessage(), "No self-assignable roles configured!\nIf you are an admin use `.roles add @role @role ...` to add roles!");
				return;
			}
			EmbedBuilder eb = new EmbedBuilder();
			eb.setTitle(title);
			eb.appendDescription("To get a specified role press the given emote under this message. To remove it press the emote again");
			eb.setColor(Color.MAGENTA);
			String value = "";
			for(Map.Entry<Role, Emote> k : roles.entrySet()){
				value += k.getValue().getAsMention() + Emotes.BLANK.get() + Emotes.BLANK.get() + k.getKey().getAsMention() + "\n";
			}
			eb.addField("**Emote:**" + Emotes.BLANK.get() + "**Role:**", value, true);
			Message message = event.getChannel().sendMessage(eb.build()).complete();
			main.commandManager.addListenerCmd(message, event.getMessage(), this, - 1L);
			for(Map.Entry<Role, Emote> r : roles.entrySet()){
				message.addReaction(r.getValue()).queue();
			}
			message.addReaction(Emotes.WASTEBASKET.get()).queue();
		}
	}
	
	@Override
	public void reactionAdd(Message command, GuildMessageReactionAddEvent event){
		Map<Role, Emote> roles = getRoleEmoteMap(event.getGuild());
		for(Map.Entry<Role, Emote> r : roles.entrySet()){
			if(event.getReactionEmote().getId().equals(r.getValue().getId())){
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
