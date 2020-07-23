package de.anteiku.kittybot.objects;

public class Invite{

	public String guildId;
	public int uses;

	public Invite(String guildId, int uses){
		this.guildId = guildId;
		this.uses = uses;
	}

	public void used(){
		this.uses++;
	}

}
