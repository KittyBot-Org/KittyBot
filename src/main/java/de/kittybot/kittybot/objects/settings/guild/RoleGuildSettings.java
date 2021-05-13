package de.kittybot.kittybot.objects.settings.guild;

import de.kittybot.kittybot.objects.settings.IGuildSettings;
import de.kittybot.kittybot.objects.settings.SelfAssignableRole;
import de.kittybot.kittybot.objects.settings.SelfAssignableRoleGroup;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class RoleGuildSettings implements IGuildSettings{

	private final Set<SelfAssignableRole> selfAssignableRoles;
	private final Set<SelfAssignableRoleGroup> selfAssignableRoleGroups;
	private final Map<String, Set<Long>> guildInviteRoles;

	public RoleGuildSettings(Collection<SelfAssignableRole> selfAssignableRoles, Collection<SelfAssignableRoleGroup> selfAssignableRoleGroups, Map<String, Set<Long>> guildInviteRoles){
		this.selfAssignableRoles = new HashSet<>(selfAssignableRoles);
		this.selfAssignableRoleGroups = new HashSet<>(selfAssignableRoleGroups);
		this.guildInviteRoles = guildInviteRoles;
	}

	public Set<SelfAssignableRole> getSelfAssignableRoles(){
		return this.selfAssignableRoles;
	}

	public void addSelfAssignableRoles(Set<SelfAssignableRole> roles){
		this.selfAssignableRoles.addAll(roles);
	}

	public void removeSelfAssignableRoles(Set<Long> roles){
		this.selfAssignableRoles.removeIf(role -> roles.contains(role.getRoleId()));
	}

	public Set<SelfAssignableRoleGroup> getSelfAssignableRoleGroups(){
		return this.selfAssignableRoleGroups;
	}

	public void addSelfAssignableRoleGroups(Set<SelfAssignableRoleGroup> groups){
		this.selfAssignableRoleGroups.addAll(groups);
	}

	public void removeSelfAssignableRoleGroups(Set<Long> groups){
		this.selfAssignableRoleGroups.removeIf(group -> groups.contains(group.getId()));
	}

	public Map<String, Set<Long>> getInviteRoles(){
		return this.guildInviteRoles;
	}

	public void setInviteRoles(String code, Set<Long> roles){
		if(roles.isEmpty()){
			this.guildInviteRoles.remove(code);
			return;
		}
		this.guildInviteRoles.put(code, roles);
	}

	public void addInviteRoles(String code, Set<Long> roles){
		this.guildInviteRoles.computeIfAbsent(code, s -> new HashSet<>()).addAll(roles);
	}

	public void removeInviteRoles(String code, Set<Long> roles){
		var inviteRoles = this.guildInviteRoles.get(code);
		if(inviteRoles == null){
			return;
		}
		inviteRoles.removeAll(roles);
		if(inviteRoles.isEmpty()){
			this.guildInviteRoles.remove(code);
		}
	}

	public Set<Long> getInviteRoles(String code){
		return this.guildInviteRoles.get(code);
	}

}
