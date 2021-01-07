package de.kittybot.kittybot.objects;

import de.kittybot.kittybot.jooq.tables.records.SelfAssignableRolesRecord;
import org.jooq.Record;

import static de.kittybot.kittybot.jooq.Tables.*;

public class SelfAssignableRole{

	private final long guildId, groupId, roleId, emoteId;

	public SelfAssignableRole(long guildId, long groupId, long roleId, long emoteId){
		this.guildId = guildId;
		this.groupId = groupId;
		this.roleId = roleId;
		this.emoteId = emoteId;
	}

	public SelfAssignableRole(SelfAssignableRolesRecord record){
		this.guildId = record.getGuildId();
		this.groupId = record.getGroupId();
		this.roleId = record.getRoleId();
		this.emoteId = record.getEmoteId();
	}

	public long getGuildId(){
		return this.guildId;
	}

	public long getGroupId(){
		return this.groupId;
	}

	public long getRoleId(){
		return this.roleId;
	}

	public long getEmoteId(){
		return this.emoteId;
	}

}
