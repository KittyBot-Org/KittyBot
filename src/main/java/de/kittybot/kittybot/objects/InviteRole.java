package de.kittybot.kittybot.objects;

public class InviteRole{

	private final String code;
	private final long roleId;

	public InviteRole(String code, long roleId){
		this.code = code;
		this.roleId = roleId;
	}

	public String getCode(){
		return this.code;
	}

	public long getRoleId(){
		return this.roleId;
	}

}
