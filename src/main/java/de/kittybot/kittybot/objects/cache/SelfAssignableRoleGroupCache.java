package de.anteiku.kittybot.objects.cache;

import de.anteiku.kittybot.database.Database;
import de.anteiku.kittybot.objects.SelfAssignableRole;
import de.anteiku.kittybot.objects.SelfAssignableRoleGroup;
import net.dv8tion.jda.api.entities.Guild;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class SelfAssignableRoleGroupCache{

	private static final List<SelfAssignableRoleGroup> SELF_ASSIGNABLE_ROLE_GROUPS = new ArrayList<>();


	public static void addSelfAssignableRoleGroups(String guildId, String group, boolean only){

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

	public static SelfAssignableRoleGroup getSelfAssignableRoleGroupByName(String guildId, String groupName){
		//TODO
		return null;
	}

	public static void removeSelfAssignableRoleGroups(String guildId, List<SelfAssignableRoleGroup> groups){
		removeSelfAssignableRoleGroupsById(guildId, groups.stream().map(SelfAssignableRoleGroup::getId).collect(Collectors.toList()));
	}

	public static void removeSelfAssignableRoleGroupByName(String guildId, String group){
		Database.removeSelfAssignableRoleGroupsByName(guildId, Collections.singletonList(group));
		SELF_ASSIGNABLE_ROLE_GROUPS.removeIf(g -> g.getName().equals(group));
	}

	public static void removeSelfAssignableRoleGroupsByName(String guildId, List<String> groups){
		Database.removeSelfAssignableRoleGroupsByName(guildId, groups);
		SELF_ASSIGNABLE_ROLE_GROUPS.removeIf(group -> groups.contains(group.getName()));
	}

	public static void removeSelfAssignableRoleGroupsById(String guildId, List<String> groups){
		Database.removeSelfAssignableRoleGroupsById(guildId, groups);
		SELF_ASSIGNABLE_ROLE_GROUPS.removeIf(group -> groups.contains(group.getName()));
	}

}