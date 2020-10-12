package de.kittybot.kittybot.cache;

import de.kittybot.kittybot.database.Database;
import net.dv8tion.jda.api.entities.Guild;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class SelfAssignableRoleCache{

	private static final Map<String, Map<String, String>> SELF_ASSIGNABLE_ROLES = new HashMap<>();

	private SelfAssignableRoleCache(){}

	public static void setSelfAssignableRoles(String guildId, Map<String, String> selfAssignableRoles){
		Database.setSelfAssignableRoles(guildId, selfAssignableRoles);
		SELF_ASSIGNABLE_ROLES.put(guildId, selfAssignableRoles);
	}

	public static void removeSelfAssignableRoles(String guildId, Set<String> roles){
		Database.removeSelfAssignableRoles(guildId, roles);
		var map = SELF_ASSIGNABLE_ROLES.get(guildId);
		if(map != null){
			roles.forEach(map::remove);
		}
	}

	public static void addSelfAssignableRoles(String guildId, Map<String, String> roles){
		Database.addSelfAssignableRoles(guildId, roles);
		var map = SELF_ASSIGNABLE_ROLES.get(guildId);
		if(map != null){
			map.putAll(roles);
		}
	}

	public static boolean isSelfAssignableRole(String guildId, String roleId){
		return getSelfAssignableRoles(guildId).get(roleId) != null;
	}

	public static Map<String, String> getSelfAssignableRoles(String guildId){
		var map = SELF_ASSIGNABLE_ROLES.get(guildId);
		if(map != null){
			return map;
		}
		map = Database.getSelfAssignableRoles(guildId);
		SELF_ASSIGNABLE_ROLES.put(guildId, map);
		return map;
	}

	public static void pruneCache(Guild guild){
		SELF_ASSIGNABLE_ROLES.remove(guild.getId());
	}

}