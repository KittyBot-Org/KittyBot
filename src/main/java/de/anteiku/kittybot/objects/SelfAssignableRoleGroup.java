package de.anteiku.kittybot.objects;

public class SelfAssignableRoleGroup{

	private final String guildId;
	private final String groupId;
	private final String groupName;

	public SelfAssignableRoleGroup(String guildId, String groupId, String groupName){
		this.guildId = guildId;
		this.groupId = groupId;
		this.groupName = groupName;
	}

	public String getGuildId(){
		return guildId;
	}

	public String getGroupId(){
		return groupId;
	}

	public String getGroupName(){
		return groupName;
	}

}
