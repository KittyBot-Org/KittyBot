package de.anteiku.kittybot.objects;

import de.anteiku.kittybot.KittyBot;
import de.anteiku.kittybot.database.Database;
import de.anteiku.kittybot.objects.command.ACommand;
import de.anteiku.kittybot.objects.command.CommandContext;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Invite;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class Cache{

	public static final Map<String, String> GUILD_PREFIXES = new HashMap<>();
	public static final Map<String, Map<String, InviteData>> INVITES = new HashMap<>();
	public static final Map<String, MusicPlayer> MUSIC_PLAYERS = new HashMap<>();
	public static final Map<String, ReactiveMessage> REACTIVE_MESSAGES = new HashMap<>();
	public static final Map<String, String> COMMAND_RESPONSES = new HashMap<>();
	public static final Map<String, Map<String, String>> SELF_ASSIGNABLE_ROLES = new HashMap<>();
	private static final Logger LOG = LoggerFactory.getLogger(Cache.class);

	private Cache(){}

	public static Invite getUsedInvite(Guild guild){
		if(guild.getSelfMember().hasPermission(Permission.MANAGE_SERVER)){
			for(Invite invite : guild.retrieveInvites().complete()){
				var oldInvite = INVITES.get(guild.getId()).get(invite.getCode());
				if(oldInvite != null && oldInvite.getUses() < invite.getUses()){
					return invite;
				}
			}
		}
		return null;
	}

	public static void deleteInvite(String guild, String code){
		if(INVITES.get(guild) != null){
			INVITES.get(guild).remove(code);
		}
	}

	public static void initGuildInviteCache(Guild guild){
		LOG.info("Initializing invite cache for guild: " + guild.getName() + "(" + guild.getId() + ")");
		if(guild.getSelfMember().hasPermission(Permission.MANAGE_SERVER)){
			guild.retrieveInvites().queue(invites -> {
				for(Invite invite : invites){
					addNewInvite(invite);
				}
			});
		}
	}

	public static void addNewInvite(Invite invite){
		var guildId = invite.getGuild().getId();
		if(INVITES.get(guildId) == null){
			INVITES.put(guildId, new HashMap<>());
		}
		INVITES.get(guildId).put(invite.getCode(), new InviteData(invite));
	}


	//Self Assignable Roles

	public static Map<String, String> getSelfAssignableRoles(String guildId){
		var map = SELF_ASSIGNABLE_ROLES.get(guildId);
		if(map != null){
			return map;
		}
		map = Database.getSelfAssignableRoles(guildId);
		SELF_ASSIGNABLE_ROLES.put(guildId, map);
		return map;
	}

	public static void setSelfAssignableRoles(String guildId, Map<String, String> selfAssignableRoles){
		SELF_ASSIGNABLE_ROLES.put(guildId, selfAssignableRoles);
		//Database.setSelfAssignableRoles(guildId, selfAssignableRoles);
	}


	//Command Prefixes

	public static String getCommandPrefix(String guildId){
		String prefix = GUILD_PREFIXES.get(guildId);
		if(prefix == null){
			prefix = Database.getCommandPrefix(guildId);
			GUILD_PREFIXES.put(guildId, prefix);
		}
		return prefix;
	}

	public static void setCommandPrefix(String guildId, String prefix){
		Database.setCommandPrefix(guildId, prefix);
		GUILD_PREFIXES.put(guildId, prefix);
	}


	//Command Responses

	public static void addCommandResponse(Message command, Message response){
		COMMAND_RESPONSES.put(command.getId(), response.getId());
	}

	public static void deleteCommandResponse(TextChannel channel, String command){
		var commandResponse = COMMAND_RESPONSES.get(command);
		if(commandResponse != null){
			channel.deleteMessageById(commandResponse).queue();
			COMMAND_RESPONSES.remove(command);
		}
	}


	//Music Player Cache

	public static void addMusicPlayer(Guild guild, MusicPlayer player){
		MUSIC_PLAYERS.put(guild.getId(), player);
	}

	public static MusicPlayer getMusicPlayer(Guild guild){
		return MUSIC_PLAYERS.get(guild.getId());
	}

	public static void destroyMusicPlayer(Guild guild, String controllerId){
		KittyBot.getLavalink().getLink(guild).destroy();
		removeReactiveMessage(guild, controllerId);
		MUSIC_PLAYERS.remove(guild.getId());
	}


	// Reactive Messages Cache

	public static void removeReactiveMessage(Guild guild, String messageId){
		guild.getTextChannelById(REACTIVE_MESSAGES.get(messageId).channelId).deleteMessageById(messageId).queue();
		REACTIVE_MESSAGES.remove(messageId);
		Database.removeReactiveMessage(guild.getId(), messageId);
	}

	public static void addReactiveMessage(CommandContext ctx, Message message, ACommand cmd, String allowed){
		REACTIVE_MESSAGES.put(message.getId(), new ReactiveMessage(ctx.getMessage().getId(), ctx.getUser().getId(), ctx.getChannel().getId(), message.getId(), cmd.command, allowed));
		Database.addReactiveMessage(ctx.getGuild().getId(), ctx.getUser().getId(), ctx.getChannel().getId(), message.getId(), ctx.getMessage().getId(), cmd.command, allowed);
	}

	public static ReactiveMessage getReactiveMessage(Guild guild, String messageId){
		var reactiveMessage = REACTIVE_MESSAGES.get(messageId);
		if(reactiveMessage != null){
			return reactiveMessage;
		}
		reactiveMessage = Database.isReactiveMessage(guild.getId(), messageId);
		REACTIVE_MESSAGES.put(messageId, reactiveMessage);
		return reactiveMessage;
	}

}
