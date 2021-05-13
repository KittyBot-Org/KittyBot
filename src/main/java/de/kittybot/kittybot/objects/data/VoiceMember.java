package de.kittybot.kittybot.objects.data;

import net.dv8tion.jda.api.entities.GuildVoiceState;
import org.jooq.types.YearToSecond;

public class VoiceMember{

	private final long guildId;
	private long voiceTime, streamTime;

	public VoiceMember(GuildVoiceState voiceState){
		this.guildId = voiceState.getGuild().getIdLong();
		setVoice(!voiceState.isDeafened() && !voiceState.isMuted() && !voiceState.isSuppressed());
		setStreaming(voiceState.isStream());
	}

	public long getGuildId(){
		return guildId;
	}

	public YearToSecond getVoiceTime(){
		return YearToSecond.valueOf(System.currentTimeMillis() - this.voiceTime);
	}

	public void setVoice(boolean voice){
		if(voice){
			this.voiceTime = System.currentTimeMillis();
			return;
		}
		this.voiceTime = -1L;
	}

	public boolean isVoice(){
		return this.voiceTime != -1L;
	}

	public YearToSecond getStreamTime(){
		return YearToSecond.valueOf(System.currentTimeMillis() - this.streamTime);
	}

	public void setStreaming(boolean streaming){
		if(streaming){
			this.streamTime = System.currentTimeMillis();
			return;
		}
		this.streamTime = -1L;
	}

	public boolean isStreaming(){
		return this.streamTime != -1L;
	}

}
