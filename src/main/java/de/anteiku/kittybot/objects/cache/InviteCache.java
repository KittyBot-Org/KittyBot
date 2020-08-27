package de.anteiku.kittybot.objects.cache;

import de.anteiku.kittybot.objects.invites.InviteData;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Invite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class InviteCache {
    private static final Map<String, Map<String, InviteData>> INVITES = new HashMap<>();
    private static final Logger LOG = LoggerFactory.getLogger(InviteCache.class);

    public static Invite getUsedInvite(Guild guild){
        if(!guild.getSelfMember().hasPermission(Permission.MANAGE_SERVER)){
            return null;
        }
        final var guildId = guild.getId();
        final var value = INVITES.get(guildId);
        if(value == null){ // how?
            initCaching(guild);
            return null;
        }
        for(final var invite : guild.retrieveInvites().complete()){
            final var oldInvite = value.get(invite.getCode());
            if(invite.getUses() > oldInvite.getUses()){
                oldInvite.used();
                return invite;
            }
        }
        return null;
    }

    public static void initCaching(Guild guild){
        if (!guild.getSelfMember().hasPermission(Permission.MANAGE_SERVER))
            return;
        LOG.info("Initializing invite cache for guild: {} ({})", guild.getName(), guild.getId());
        guild.retrieveInvites().queue(invites -> invites.forEach(InviteCache::cacheInvite));
    }

    public static void cacheInvite(Invite invite){
        if(invite.getGuild() != null){
            var guildId = invite.getGuild().getId();
            INVITES.computeIfAbsent(guildId, k -> new HashMap<>());
            INVITES.get(guildId).put(invite.getCode(), new InviteData(invite));
        }
    }

    public static void uncacheInvite(String guild, String code){
        if(INVITES.get(guild) != null){
            INVITES.get(guild).remove(code);
        }
    }

    public static void pruneCache(Guild guild){
        LOG.info("Pruning invite cache for guild: {} ({})", guild.getName(), guild.getId());
        INVITES.remove(guild.getId());
    }
}
