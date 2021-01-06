package de.kittybot.kittybot.objects;

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

	public SelfAssignableRole(Record record){
		this.guildId = record.get(SELF_ASSIGNABLE_ROLE_GROUPS.GUILD_ID);
		this.groupId = record.get(SELF_ASSIGNABLE_ROLES.GROUP_ID);
		this.roleId = record.get(SELF_ASSIGNABLE_ROLES.ROLE_ID);
		this.emoteId = record.get(SELF_ASSIGNABLE_ROLES.EMOTE_ID);
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
