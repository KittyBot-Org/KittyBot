package de.kittybot.kittybot.cache;

import de.kittybot.kittybot.database.Database;
import de.kittybot.kittybot.objects.SelfAssignableRole;
import net.dv8tion.jda.api.entities.Guild;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class SelfAssignableRoleCache{

	private static final List<SelfAssignableRole> SELF_ASSIGNABLE_ROLES = new ArrayList<>();

	public static void setSelfAssignableRoles(String guildId, Set<SelfAssignableRole> selfAssignableRoles){
		Database.setSelfAssignableRoles(guildId, selfAssignableRoles);
		SELF_ASSIGNABLE_ROLES.removeIf(selfAssignableRole -> selfAssignableRole.getGroupId().equals(guildId));
		SELF_ASSIGNABLE_ROLES.addAll(selfAssignableRoles);
	}

	public static void removeSelfAssignableRole(String guildId, String role){
		removeSelfAssignableRoles(guildId, Collections.singleton(role));
	}

	public static void removeSelfAssignableRoles(String guildId, Set<String> roles){
		Database.removeSelfAssignableRoles(guildId, roles);
		SELF_ASSIGNABLE_ROLES.removeIf(role -> roles.contains(role.getRoleId()));
	}

	public static void addSelfAssignableRoles(String guildId, Set<SelfAssignableRole> roles){
		Database.addSelfAssignableRoles(guildId, roles);
		SELF_ASSIGNABLE_ROLES.addAll(roles);
	}

	public static boolean isSelfAssignableRole(String guildId, String roleId){
		var roles = getSelfAssignableRoles(guildId);
		if(roles == null){
			return false;
		}
		return roles.stream().anyMatch(selfAssignableRole -> selfAssignableRole.getRoleId().equals(roleId));
	}

	public static List<SelfAssignableRole> getSelfAssignableRoles(String guildId){
		var roles = SELF_ASSIGNABLE_ROLES.stream().filter(selfAssignableRole -> selfAssignableRole.getGuildId().equals(guildId)).collect(Collectors.toList());
		if(!roles.isEmpty()){
			return roles;
		}
		roles = Database.getSelfAssignableRoles(guildId);
		if(roles == null){
			return null;
		}
		SELF_ASSIGNABLE_ROLES.addAll(roles);
		return roles;
	}

	public static void pruneCache(Guild guild){
		SELF_ASSIGNABLE_ROLES.removeIf(selfAssignableRole -> selfAssignableRole.getGuildId().equals(guild.getId()));
	}

}