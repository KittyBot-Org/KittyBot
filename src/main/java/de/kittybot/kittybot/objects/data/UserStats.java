package de.kittybot.kittybot.objects.data;

import de.kittybot.kittybot.jooq.tables.records.UserStatisticsRecord;
import de.kittybot.kittybot.objects.enums.StatisticType;

import java.time.Duration;
import java.time.LocalDateTime;

public class UserStats{

	private final long userId, guildId, xp;
	private final int commandsUsed, messagesSent, emotesSent, stickersSent;
	private final Duration voiceTime, streamTime;
	private final LocalDateTime lastActive;
	private long lastXpGain;

	public UserStats(UserStatisticsRecord record){
		this.userId = record.getUserId();
		this.guildId = record.getGuildId();
		this.xp = record.getXp();
		this.commandsUsed = record.getCommandsUsed();
		this.messagesSent = record.getMessagesSent();
		this.emotesSent = record.getEmotesSent();
		this.stickersSent = record.getStickersSent();
		this.voiceTime = record.getVoiceTime().toDuration();
		this.streamTime = record.getStreamTime().toDuration();
		this.lastActive = record.getLastActive();
		this.lastXpGain = 0;
	}

	public long getUserId(){
		return this.userId;
	}

	public long getGuildId(){
		return this.guildId;
	}

	public long getXp(){
		return this.xp;
	}

	public int getCommandsUsed(){
		return this.commandsUsed;
	}

	public int getMessagesSent(){
		return this.messagesSent;
	}

	public int getEmotesSent(){
		return this.emotesSent;
	}

	public int getStickersSent(){
		return this.stickersSent;
	}

	public Duration getVoiceTime(){
		return this.voiceTime;
	}

	public Duration getStreamTime(){
		return this.streamTime;
	}

	public LocalDateTime getLastActive(){
		return this.lastActive;
	}

	public long getLastXpGain(){
		return this.lastXpGain;
	}

	public UserStats setLastXpGain(long xpGain){
		this.lastXpGain = xpGain;
		return this;
	}

	public String get(StatisticType type){
		switch(type){
			case XP:
				return "Level " + getLevel() + " Xp: " + getRestXp();
			case COMMANDS_USED:
				return Integer.toString(this.commandsUsed);
			case MESSAGES_SENT:
				return Integer.toString(this.messagesSent);
			case EMOTES_SENT:
				return Integer.toString(this.emotesSent);
			case STICKERS_SENT:
				return Integer.toString(this.stickersSent);
			case VOICE_TIME:
				return this.voiceTime.toString();
			case LAST_ACTIVE:
				return this.lastActive.toString();
			default:
				return "undefined stat type";
		}
	}

	public int getLevel(){
		return (int) Math.sqrt(this.xp) / 10;
	}

	public long getRestXp(){
		return this.xp - getRequiredXp(getLevel());
	}

	public long getRequiredXp(int level){
		return (long) level * level * 100;
	}

	public long getNextLevelXp(){
		var level = getLevel() + 1;
		return (long) level * level * 100;
	}

	public long getThisLevelXp(){
		var level = getLevel();
		return (long) level * level * 100;
	}

	public long getPreviousLevelXp(){
		var level = getLevel() - 1;
		return (long) level * level * 100;
	}

	public long getNeededXp(){
		return getNextLevelXp() - getThisLevelXp();
	}

	@Override
	public String toString(){
		return "UserStatistics{" +
			"userId=" + userId +
			", guildId=" + guildId +
			", xp=" + xp +
			", commandsUsed=" + commandsUsed +
			", messagesSent=" + messagesSent +
			", emotesSent=" + emotesSent +
			", voiceTime=" + voiceTime +
			", streamTime=" + streamTime +
			", lastActive=" + lastActive +
			'}';
	}

	public boolean checkIfLevelUp(){
		return this.xp - this.lastXpGain < getThisLevelXp();
	}

}
