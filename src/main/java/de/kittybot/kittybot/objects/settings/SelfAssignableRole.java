package de.kittybot.kittybot.objects.settings;

import de.kittybot.kittybot.jooq.tables.records.SelfAssignableRolesRecord;

public class SelfAssignableRole{

	private final long groupId, guildId, roleId, emoteId;

	public SelfAssignableRole(long roleId, long emoteId, long guildId, long groupId){
		this.roleId = roleId;
		this.emoteId = emoteId;
		this.guildId = guildId;
		this.groupId = groupId;
	}

	public SelfAssignableRole(SelfAssignableRolesRecord record){
		this.roleId = record.getRoleId();
		this.emoteId = record.getEmoteId();
		this.guildId = record.getGuildId();
		this.groupId = record.getGroupId();
	}

	public long getRoleId(){
		return this.roleId;
	}

	public long getEmoteId(){
		return this.emoteId;
	}

	public long getGuildId(){
		return this.guildId;
	}

	public long getGroupId(){
		return this.groupId;
	}

}
