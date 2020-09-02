package de.anteiku.kittybot.objects;

public class SelfAssignableRole{

	private final String guildId;
	private final String roleId;
	private final String emoteId;

	public SelfAssignableRole(String guildId, String roleId, String emoteId){
		this.guildId = guildId;
		this.roleId = roleId;
		this.emoteId = emoteId;
	}

	public String getGuildId(){
		return guildId;
	}

	public String getRoleId(){
		return roleId;
	}

	public String getEmoteId(){
		return emoteId;
	}

}
