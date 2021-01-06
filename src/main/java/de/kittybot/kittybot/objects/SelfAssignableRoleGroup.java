package de.kittybot.kittybot.objects;

import de.kittybot.kittybot.jooq.tables.records.SelfAssignableRoleGroupsRecord;
import org.jooq.Record;

public class SelfAssignableRoleGroup{

	private final long guildId;
	private final String groupName;
	private final int maxRoles;
	private long id;

	public SelfAssignableRoleGroup(long guildId, long id, String groupName, int maxRoles){
		this.guildId = guildId;
		this.id = id;
		this.groupName = groupName;
		this.maxRoles = maxRoles;
	}

	public SelfAssignableRoleGroup(SelfAssignableRoleGroupsRecord record){
		this.guildId = record.getGuildId();
		this.id = record.getId();
		this.groupName = record.getGroupName();
		this.maxRoles = record.getMaxRoles();
	}

	public long getGuildId(){
		return this.guildId;
	}

	public long getId(){
		return this.id;
	}

	public void setId(long id){
		this.id = id;
	}

	public String getName(){
		return this.groupName;
	}

	public int getMaxRoles(){
		return this.maxRoles;
	}

}
