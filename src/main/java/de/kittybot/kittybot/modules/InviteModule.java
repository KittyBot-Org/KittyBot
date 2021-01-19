package de.kittybot.kittybot.modules;

import de.kittybot.kittybot.module.Module;
import de.kittybot.kittybot.module.Modules;
import de.kittybot.kittybot.objects.InviteData;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Invite;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.guild.invite.GuildInviteCreateEvent;
import net.dv8tion.jda.api.events.guild.invite.GuildInviteDeleteEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("unused")
public class InviteModule extends Module{

	private static final Logger LOG = LoggerFactory.getLogger(InviteModule.class);

	private Map<Long, Map<String, InviteData>> invites;
	private Map<Long, Map<Long, InviteData>> usedInvites;

	@Override
	public Set<Class<? extends Module>> getDependencies(){
		return null;
	}

	@Override
	public void onEnable(){
		this.invites = new HashMap<>();
		this.usedInvites = new HashMap<>();
	}

	@Override
	public void onGuildReady(@Nonnull GuildReadyEvent event){
		init(event.getGuild());
	}

	@Override
	public void onGuildJoin(@Nonnull GuildJoinEvent event){
		init(event.getGuild());
	}

	@Override
	public void onGuildLeave(@Nonnull GuildLeaveEvent event){
		prune(event.getGuild().getIdLong());
	}

	@Override
	public void onGuildInviteCreate(@Nonnull GuildInviteCreateEvent event){
		cache(event.getInvite());
	}

	@Override
	public void onGuildInviteDelete(@Nonnull GuildInviteDeleteEvent event){
		uncache(event.getGuild().getIdLong(), event.getCode());
	}

	@Override
	public void onGuildMemberJoin(@Nonnull GuildMemberJoinEvent event){
		var guild = event.getGuild();
		var guildId = guild.getIdLong();
		var invite = retrieveUsedInvite(guild);
		if(invite == null){
			return;
		}
		this.usedInvites.computeIfAbsent(guildId, k -> new HashMap<>());
		this.usedInvites.get(guildId).put(event.getUser().getIdLong(), new InviteData(invite));
	}

	private Invite retrieveUsedInvite(Guild guild){
		if(!guild.getSelfMember().hasPermission(Permission.MANAGE_SERVER)){
			return null;
		}
		var guildId = guild.getIdLong();
		var value = this.invites.get(guildId);
		if(value == null){ // how?
			init(guild);
			return null;
		}
		for(var invite : guild.retrieveInvites().complete()){
			var oldInvite = value.get(invite.getCode());
			if(oldInvite == null){
				continue;
			}
			if(invite.getUses() > oldInvite.getUses()){
				oldInvite.used();
				return invite;
			}
		}
		return null;
	}

	public void uncache(long guildId, String code){
		if(invites.get(guildId) != null){
			invites.get(guildId).remove(code);
		}
	}

	public void prune(long guildId){
		LOG.info("Pruning invite cache for guild: {}", guildId);
		invites.remove(guildId);
		this.usedInvites.remove(guildId);
	}

	public void init(Guild guild){
		if(!guild.getSelfMember().hasPermission(Permission.MANAGE_SERVER)){
			return;
		}
		guild.retrieveInvites().queue(invites -> invites.forEach(this::cache));
	}

	public void cache(Invite invite){
		if(invite.getGuild() != null){
			var guildId = invite.getGuild().getIdLong();
			invites.computeIfAbsent(guildId, k -> new HashMap<>());
			invites.get(guildId).put(invite.getCode(), new InviteData(invite));
		}
	}

	public InviteData getUsedInvite(long guildId, long userId){
		return this.usedInvites.get(guildId).get(userId);
	}

	public Map<String, InviteData> getGuildInvites(long guildId){
		return this.invites.get(guildId);
	}

}
