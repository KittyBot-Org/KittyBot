package de.anteiku.kittybot.objects;

public class SelfAssignableRoleGroup{

	private final String guildId;
	private final String groupId;
	private final String groupName;
	private final boolean onlyOne;

	public SelfAssignableRoleGroup(String guildId, String groupId, String groupName, boolean onlyOne){
		this.guildId = guildId;
		this.groupId = groupId;
		this.groupName = groupName;
		this.onlyOne = onlyOne;
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

	public boolean getOnlyOne(){
		return onlyOne;
	}

}
