package de.kittybot.kittybot.objects.cache;

import de.kittybot.kittybot.database.Database;
import de.kittybot.kittybot.objects.SelfAssignableRoleGroup;

import java.util.*;
import java.util.stream.Collectors;

public class SelfAssignableRoleGroupCache{

	private static final List<SelfAssignableRoleGroup> SELF_ASSIGNABLE_ROLE_GROUPS = new ArrayList<>();

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

	public static List<SelfAssignableRoleGroup> getSelfAssignableRoleGroups(String guildId){
		var groups = SELF_ASSIGNABLE_ROLE_GROUPS.stream().filter(group -> group.getGuildId().equals(guildId)).collect(Collectors.toList());
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

	public static void removeSelfAssignableRoleGroup(String guildId, String group){
		removeSelfAssignableRoleGroups(guildId, Collections.singleton(group));
	}

	public static void removeSelfAssignableRoleGroups(String guildId, Set<String> groups){
		Database.removeSelfAssignableRoleGroups(guildId, groups);
		SELF_ASSIGNABLE_ROLE_GROUPS.removeIf(group -> groups.contains(group.getId()));
	}

	public static void removeSelfAssignableRoleGroupByName(String guildId, String group){
		removeSelfAssignableRoleGroupsByName(guildId, Collections.singleton(group));
	}

	public static void removeSelfAssignableRoleGroupsByName(String guildId, Set<String> groupNames){
		var groups = new HashSet<SelfAssignableRoleGroup>();
		for(var groupName : groupNames){
			groups.addAll(getSelfAssignableRoleGroupByName(guildId, groupName));
		}
		Database.removeSelfAssignableRoleGroups(guildId, groups.stream().map(SelfAssignableRoleGroup::getId).collect(Collectors.toSet()));
		SELF_ASSIGNABLE_ROLE_GROUPS.removeIf(groups::contains);
	}

	public static List<SelfAssignableRoleGroup> getSelfAssignableRoleGroupByName(String guildId, String groupName){
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

	private static List<SelfAssignableRoleGroup> filterGroupsByName(List<SelfAssignableRoleGroup> groups, String groupName){
		return groups.stream().filter(srg -> srg.getName().equalsIgnoreCase(groupName)).collect(Collectors.toList());
	}

}