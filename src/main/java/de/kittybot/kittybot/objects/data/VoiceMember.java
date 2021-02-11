package de.kittybot.kittybot.objects.data;

import org.jooq.types.YearToSecond;

public class VoiceMember{

	private final long guildId, joinTime;

	public VoiceMember(long guildId){
		this.guildId = guildId;
		this.joinTime = System.currentTimeMillis();
	}

	public long getGuildId(){
		return guildId;
	}

	public YearToSecond getVoiceTime(){
		return YearToSecond.valueOf(System.currentTimeMillis() - this.joinTime);
	}

}
