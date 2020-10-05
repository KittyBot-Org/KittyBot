package de.kittybot.kittybot.objects;

public class SelfAssignableRoleGroup{

	private final String guildId;
	private final String groupId;
	private final String groupName;
	private final int maxRoles;

	public SelfAssignableRoleGroup(String guildId, String groupId, String groupName, int maxRoles){
		this.guildId = guildId;
		this.groupId = groupId;
		this.groupName = groupName;
		this.maxRoles = maxRoles;
	}

	public String getGuildId(){
		return guildId;
	}

	public String getId(){
		return groupId;
	}

	public String getName(){
		return groupName;
	}

	public int getMaxRoles(){
		return maxRoles;
	}

}
