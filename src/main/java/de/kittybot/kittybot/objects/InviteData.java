package de.kittybot.kittybot.objects;

import net.dv8tion.jda.api.entities.Invite;

public class InviteData{

	private final long guildId, userId;
	private final String code;
	private int uses;

	public InviteData(Invite invite){
		this.guildId = invite.getGuild().getIdLong();
		var inviter = invite.getInviter();
		if(inviter != null){
			this.userId = inviter.getIdLong();
		}
		else{
			this.userId = -1L;
		}
		this.code = invite.getCode();
		this.uses = invite.getUses();
	}

	public long getGuildId(){
		return this.guildId;
	}

	public long getUserId(){
		return this.userId;
	}

	public String getCode(){
		return this.code;
	}

	public int getUses(){
		return this.uses;
	}

	public void used(){
		this.uses++;
	}

}
