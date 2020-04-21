package de.anteiku.kittybot.commands;

import de.anteiku.kittybot.KittyBot;
import de.anteiku.kittybot.utils.Utils;
import de.anteiku.kittybot.utils.Emotes;
import de.anteiku.kittybot.objects.ReactiveMessage;
import de.anteiku.kittybot.objects.ValuePair;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;

import java.awt.*;
import java.util.List;
import java.util.*;

public class RolesCommand extends ACommand{
	
	public static String COMMAND = "roles";
	public static String USAGE = "roles <add|remove|list>";
	public static String DESCRIPTION = "Used to manage your roles";
	protected static String[] ALIAS = {"r", "rollen"};
	
	private static final String title = "Self-assignable roles:";
	
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
				map.put(role, guild.getJDA().getEmoteById(entry.getValue()));
			}
		}
		return map;
	}
	
	@Override
	public void run(String[] args, GuildMessageReceivedEvent event){
		if(args.length > 0){
			if(args[0].equals("?") || args[0].equals("help")){
				sendUsage(event);
			}
			if(event.getMember().isOwner() || event.getMember().hasPermission(Permission.ADMINISTRATOR)){
				List<Role> roles = event.getMessage().getMentionedRoles();
				List<Emote> emotes = event.getMessage().getEmotes();
				if(args[0].equalsIgnoreCase("add") && !roles.isEmpty() && !emotes.isEmpty()){
					main.database.addSelfAssignableRoles(event.getGuild().getId(), Utils.toMap(roles, emotes));
					sendAnswer(event, "Roles added!");
				}
				else if(args[0].equalsIgnoreCase("remove") && !roles.isEmpty()){
					main.database.removeSelfAssignableRoles(event.getGuild().getId(), Utils.toSet(roles));
					sendAnswer(event, "Roles removed!");
				}
				else if(args[0].equalsIgnoreCase("list")){
					Map<Role, Emote> map = getRoleEmoteMap(event.getGuild());
					if(map.size() == 0){
						sendAnswer(event, "There are no roles added!");
					}
					else{
						StringBuilder message = new StringBuilder();
						for(Map.Entry<Role, Emote> m : map.entrySet()){
							message.append(m.getKey().getAsMention()).append(", ");
						}
						sendAnswer(event, "Roles: " + message.toString());
					}
				}
				else{
					sendError(event, "Please be sure to mention a role & a custom discord emote");
				}
			}
			else{
				sendError(event, "You need to be an administrator to use this command!");
			}
		}
		else{
			Map<Role, Emote> roles = getRoleEmoteMap(event.getGuild());
			if(roles.size() == 0){
				sendError(event, "No self-assignable roles configured!\nIf you are an admin use `.roles add @role @role ...` to add roles!");
				return;
			}
			EmbedBuilder eb = new EmbedBuilder();
			eb.setTitle(title);
			eb.appendDescription("To get a specified role press the given emote under this message. To remove it press the emote again");
			eb.setColor(Color.MAGENTA);
			//StringBuilder value = new StringBuilder();
			String value = "";
			for(Map.Entry<Role, Emote> k : roles.entrySet()){
//				value.append(k.getValue().getAsMention())
//					.append(Emotes.BLANK.get())
//					.append(Emotes.BLANK.get())
//					.append(k.getKey().getAsMention())
//					.append("\n");
				value += k.getValue().getAsMention() + Emotes.BLANK.get() + Emotes.BLANK.get() + k.getKey().getAsMention() + "\n";
			}
			eb.addField("**Emote:**" + Emotes.BLANK.get() + "**Role:**", value, true);
			sendAnswer(event, eb).queue(
				message -> {
					main.commandManager.addReactiveMessage(event, message, this, "-1");
					for(Map.Entry<Role, Emote> role : roles.entrySet()){
						message.addReaction(role.getValue()).queue();
					}
					message.addReaction(Emotes.WASTEBASKET.get()).queue();
				}
			);
		}
	}
	
	@Override
	public void reactionAdd(ReactiveMessage reactiveMessage, GuildMessageReactionAddEvent event){
		super.reactionAdd(reactiveMessage, event);
		Map<Role, Emote> roles = getRoleEmoteMap(event.getGuild());
		for(Map.Entry<Role, Emote> r : roles.entrySet()){
			if(event.getReactionEmote().isEmote() && event.getReactionEmote().getId().equals(r.getValue().getId())){
				if(event.getMember().getRoles().contains(r.getKey())){
					event.getGuild().removeRoleFromMember(event.getMember(), r.getKey()).queue();
				}
				else{
					event.getGuild().addRoleToMember(event.getMember(), r.getKey()).queue();
				}
				event.getReaction().removeReaction(event.getUser()).queue();
			}
		}
	}
	
}
