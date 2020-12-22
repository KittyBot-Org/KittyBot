package de.kittybot.kittybot.objects;

import org.jooq.Record;

import static de.kittybot.kittybot.jooq.Tables.GUILD_INVITES;
import static de.kittybot.kittybot.jooq.Tables.GUILD_INVITE_ROLES;

public class InviteRole{

	private final String code;
	private final long roleId;

	public InviteRole(Record record){
		this.code = record.get(GUILD_INVITES.CODE);
		this.roleId = record.get(GUILD_INVITE_ROLES.ROLE_ID);
	}

	public String getCode(){
		return this.code;
	}

	public long getRoleId(){
		return this.roleId;
	}

}
