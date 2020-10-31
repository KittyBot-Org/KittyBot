package de.kittybot.kittybot.objects.data;

import net.dv8tion.jda.api.entities.Invite;

public class InviteData{

	private final String guildId;
	private final String user;
	private final String code;
	private int uses;

	public InviteData(Invite invite){
		this.guildId = invite.getGuild().getId();
		var inviter = invite.getInviter();
		if(inviter != null){
			this.user = inviter.getId();
		}
		else{
			this.user = "-1";
		}
		this.code = invite.getCode();
		this.uses = invite.getUses();
	}

	public String getGuildId(){
		return this.guildId;
	}

	public String getUser(){
		return this.user;
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
