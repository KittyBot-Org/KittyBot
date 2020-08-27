package de.anteiku.kittybot.objects.cache;

import de.anteiku.kittybot.database.Database;
import net.dv8tion.jda.api.entities.Guild;

import java.util.HashMap;
import java.util.Map;

public class SelfAssignableRoleCache {
    private static final Map<String, Map<String, String>> SELF_ASSIGNABLE_ROLES = new HashMap<>();

    public static Map<String, String> getSelfAssignableRoles(String guildId) {
        var map = SELF_ASSIGNABLE_ROLES.get(guildId);
        if (map != null) {
            return map;
        }
        map = Database.getSelfAssignableRoles(guildId);
        SELF_ASSIGNABLE_ROLES.put(guildId, map);
        return map;
    }

    public static void setSelfAssignableRoles(String guildId, Map<String, String> selfAssignableRoles) {
        SELF_ASSIGNABLE_ROLES.put(guildId, selfAssignableRoles);
        //Database.setSelfAssignableRoles(guildId, selfAssignableRoles);
    }

    public static void pruneCache(Guild guild) {
        SELF_ASSIGNABLE_ROLES.remove(guild.getId());
    }
}