package de.kittybot.kittybot.managers;

import de.kittybot.kittybot.objects.InviteData;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Invite;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.guild.invite.GuildInviteCreateEvent;
import net.dv8tion.jda.api.events.guild.invite.GuildInviteDeleteEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

public class InviteManager extends ListenerAdapter{

	private static final Logger LOG = LoggerFactory.getLogger(InviteManager.class);

	private final Map<String, Map<String, InviteData>> invites;

	public InviteManager(){
		this.invites = new HashMap<>();
	}

	@Override
	public void onGuildReady(@Nonnull GuildReadyEvent event){
		initGuildCache(event.getGuild());
	}

	@Override
	public void onGuildJoin(@Nonnull GuildJoinEvent event){
		initGuildCache(event.getGuild());
	}

	@Override
	public void onGuildLeave(@Nonnull GuildLeaveEvent event){
		pruneGuildCache(event.getGuild());
	}

	@Override
	public void onGuildInviteCreate(@Nonnull GuildInviteCreateEvent event){
		cache(event.getInvite());
	}

	@Override
	public void onGuildInviteDelete(@Nonnull GuildInviteDeleteEvent event){
		uncache(event.getGuild().getId(), event.getCode());
	}

	public void uncache(String guild, String code){
		if(invites.get(guild) != null){
			invites.get(guild).remove(code);
		}
	}

	public void pruneGuildCache(Guild guild){
		LOG.info("Pruning invite cache for guild: {} ({})", guild.getName(), guild.getId());
		invites.remove(guild.getId());
	}

	public void initGuildCache(Guild guild){
		if(!guild.getSelfMember().hasPermission(Permission.MANAGE_SERVER)){
			return;
		}
		LOG.info("Initializing invite cache for guild: {} ({})", guild.getName(), guild.getId());
		guild.retrieveInvites().queue(invites -> invites.forEach(this::cache));
	}

	public void cache(Invite invite){
		if(invite.getGuild() != null){
			var guildId = invite.getGuild().getId();
			invites.computeIfAbsent(guildId, k -> new HashMap<>());
			invites.get(guildId).put(invite.getCode(), new InviteData(invite));
		}
	}

}
