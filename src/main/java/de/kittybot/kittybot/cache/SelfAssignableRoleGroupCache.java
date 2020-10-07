package de.kittybot.kittybot.cache;

import de.kittybot.kittybot.database.Database;
import de.kittybot.kittybot.objects.SelfAssignableRoleGroup;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class SelfAssignableRoleGroupCache{

	private static final Set<SelfAssignableRoleGroup> SELF_ASSIGNABLE_ROLE_GROUPS = new HashSet<>();

	public static void setSelfAssignableRoleGroups(String guildId, Set<SelfAssignableRoleGroup> groups){
		var setGroups = Database.setSelfAssignableRoleGroups(guildId, groups);
		SELF_ASSIGNABLE_ROLE_GROUPS.removeIf(selfAssignableRole -> selfAssignableRole.getGuildId().equals(guildId));
		SELF_ASSIGNABLE_ROLE_GROUPS.addAll(setGroups);
	}

	public static boolean isSelfAssignableRoleGroup(String guildId, String groupId){
		var groups = getSelfAssignableRoleGroups(guildId);
		if(groups == null){
			return false;
		}
		return groups.stream().anyMatch(selfAssignableRoleGroup -> selfAssignableRoleGroup.getId().equals(groupId));
	}

	public static Set<SelfAssignableRoleGroup> getSelfAssignableRoleGroups(String guildId){
		var groups = SELF_ASSIGNABLE_ROLE_GROUPS.stream().filter(group -> group.getGuildId().equals(guildId)).collect(Collectors.toSet());
		if(!groups.isEmpty()){
			return groups;
		}
		groups = Database.getSelfAssignableRoleGroups(guildId);
		if(groups == null){
			return null;
		}
		SELF_ASSIGNABLE_ROLE_GROUPS.addAll(groups);
		return groups;
	}

	public static SelfAssignableRoleGroup getSelfAssignableRoleGroup(String guildId, String groupId){
		var groups = getSelfAssignableRoleGroups(guildId);
		if(groups == null){
			return null;
		}
		return groups.stream().filter(group -> group.getId().equals(groupId)).findFirst().orElse(null);
	}

	public static void addSelfAssignableRoleGroup(String guildId, SelfAssignableRoleGroup group){
		addSelfAssignableRoleGroups(guildId, Collections.singleton(group));
	}

	public static void addSelfAssignableRoleGroups(String guildId, Set<SelfAssignableRoleGroup> groups){
		var newGroups = Database.addSelfAssignableRoleGroups(guildId, groups);
		if(newGroups == null || newGroups.isEmpty()){
			return;
		}
		SELF_ASSIGNABLE_ROLE_GROUPS.addAll(newGroups);
	}

	public static void removeSelfAssignableRoleGroup(String guildId, String group){
		removeSelfAssignableRoleGroups(guildId, Collections.singleton(group));
	}

	public static void removeSelfAssignableRoleGroups(String guildId, Set<String> groups){
		Database.removeSelfAssignableRoleGroups(guildId, groups);
		SELF_ASSIGNABLE_ROLE_GROUPS.removeIf(group -> groups.contains(group.getId()));
	}

	public static Set<SelfAssignableRoleGroup> removeSelfAssignableRoleGroupByName(String guildId, String group){
		return removeSelfAssignableRoleGroupsByName(guildId, Collections.singleton(group));
	}

	public static Set<SelfAssignableRoleGroup> removeSelfAssignableRoleGroupsByName(String guildId, Set<String> groupNames){
		var groups = new HashSet<SelfAssignableRoleGroup>();
		for(var groupName : groupNames){
			groups.addAll(getSelfAssignableRoleGroupByName(guildId, groupName));
		}
		Database.removeSelfAssignableRoleGroups(guildId, groups.stream().map(SelfAssignableRoleGroup::getId).collect(Collectors.toSet()));
		SELF_ASSIGNABLE_ROLE_GROUPS.removeIf(groups::contains);
		return groups;
	}

	public static Set<SelfAssignableRoleGroup> getSelfAssignableRoleGroupByName(String guildId, String groupName){
		var groups = filterGroupsByName(SELF_ASSIGNABLE_ROLE_GROUPS, groupName);
		if(!groups.isEmpty()){
			return groups;
		}
		var newGroups = Database.getSelfAssignableRoleGroups(guildId);
		if(newGroups == null){
			return null;
		}
		SELF_ASSIGNABLE_ROLE_GROUPS.addAll(newGroups);
		return filterGroupsByName(newGroups, groupName);
	}

	private static Set<SelfAssignableRoleGroup> filterGroupsByName(Set<SelfAssignableRoleGroup> groups, String groupName){
		return groups.stream().filter(srg -> srg.getName().equalsIgnoreCase(groupName)).collect(Collectors.toSet());
	}

}