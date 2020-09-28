package de.kittybot.kittybot.objects;

public class SelfAssignableRole{

	private final String guildId;
	private final String groupId;
	private final String roleId;
	private final String emoteId;

	public SelfAssignableRole(String guildId, String groupId, String roleId, String emoteId){
		this.guildId = guildId;
		this.groupId = groupId;
		this.roleId = roleId;
		this.emoteId = emoteId;
	}

	public String getGuildId(){
		return guildId;
	}

	public String getGroupId(){
		return groupId;
	}

	public String getRoleId(){
		return roleId;
	}

	public String getEmoteId(){
		return emoteId;
	}

}
