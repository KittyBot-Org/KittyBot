package de.kittybot.kittybot.objects.settings;

import de.kittybot.kittybot.jooq.tables.records.SelfAssignableRoleGroupsRecord;

public class SelfAssignableRoleGroup{

	private final long guildId;
	private final String groupName;
	private final int maxRoles;
	private long id;

	public SelfAssignableRoleGroup(long id, long guildId, String groupName, int maxRoles){
		this.id = id;
		this.guildId = guildId;
		this.groupName = groupName;
		this.maxRoles = maxRoles;
	}

	public SelfAssignableRoleGroup(SelfAssignableRoleGroupsRecord record){
		this.id = record.getId();
		this.guildId = record.getGuildId();
		this.groupName = record.getName();
		this.maxRoles = record.getMaxRoles();
	}

	public long getId(){
		return this.id;
	}

	public void setId(long id){
		this.id = id;
	}

	public long getGuildId(){
		return this.guildId;
	}

	public String getName(){
		return this.groupName;
	}

	public int getMaxRoles(){
		return this.maxRoles;
	}

	public String getFormattedMaxRoles(){
		return this.maxRoles == -1 ? "unlimited" : String.valueOf(this.maxRoles);
	}

}
