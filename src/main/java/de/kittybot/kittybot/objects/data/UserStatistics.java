package de.kittybot.kittybot.objects.data;

import de.kittybot.kittybot.jooq.tables.records.UserStatisticsRecord;
import de.kittybot.kittybot.objects.enums.StatisticType;

import java.time.Duration;
import java.time.LocalDateTime;

public class UserStatistics{

	private final long userId, guildId, xp;
	private final int botCalls, messageCount, emoteCount;
	private final Duration voiceTime;
	private final LocalDateTime lastActive;
	private long lastXpGain;

	public UserStatistics(UserStatisticsRecord record){
		this.userId = record.getUserId();
		this.guildId = record.getGuildId();
		this.xp = record.getXp();
		this.botCalls = record.getBotCalls();
		this.messageCount = record.getMessageCount();
		this.emoteCount = record.getEmoteCount();
		this.voiceTime = record.getVoiceTime().toDuration();
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

	public int getBotCalls(){
		return this.botCalls;
	}

	public int getMessageCount(){
		return this.messageCount;
	}

	public int getEmoteCount(){
		return this.emoteCount;
	}

	public Duration getVoiceTime(){
		return this.voiceTime;
	}

	public LocalDateTime getLastActive(){
		return this.lastActive;
	}

	public long getLastXpGain(){
		return this.lastXpGain;
	}

	public UserStatistics setLastXpGain(long xpGain){
		this.lastXpGain = xpGain;
		return this;
	}

	public String get(StatisticType type){
		switch(type){
			case XP:
				return "Level " + getLevel() + " Xp: " + getRestXp();
			case BOT_CALLS:
				return Integer.toString(this.botCalls);
			case MESSAGE_COUNT:
				return Integer.toString(this.messageCount);
			case EMOTE_COUNT:
				return Integer.toString(this.emoteCount);
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

	@Override
	public String toString(){
		return "UserStatistics{" +
			"userId=" + userId +
			", guildId=" + guildId +
			", xp=" + xp +
			", botCalls=" + botCalls +
			", messageCount=" + messageCount +
			", emoteCount=" + emoteCount +
			", voiceTime=" + voiceTime +
			", lastActive=" + lastActive +
			'}';
	}

	public boolean checkIfLevelUp(){
		return this.xp - this.lastXpGain < getThisLevelXp();
	}

}
