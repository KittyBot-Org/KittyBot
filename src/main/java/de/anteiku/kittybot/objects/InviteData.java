package de.anteiku.kittybot.objects;

public class InviteData {

	public String guildId;
	public String user;
	public int uses;

	public InviteData(String guildId, String user, int uses){
		this.guildId = guildId;
		this.user = user;
		this.uses = uses;
	}

	public void used(){
		this.uses++;
	}

}
