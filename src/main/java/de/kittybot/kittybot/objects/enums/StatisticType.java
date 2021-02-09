package de.kittybot.kittybot.objects.enums;

import de.kittybot.kittybot.jooq.tables.UserStatistics;
import org.jooq.Field;

public enum StatisticType{

	XP(UserStatistics.USER_STATISTICS.XP),
	BOT_CALLS(UserStatistics.USER_STATISTICS.BOT_CALLS),
	VOICE_TIME(UserStatistics.USER_STATISTICS.VOICE_TIME),
	MESSAGE_COUNT(UserStatistics.USER_STATISTICS.MESSAGE_COUNT),
	EMOTE_COUNT(UserStatistics.USER_STATISTICS.EMOTE_COUNT),
	LAST_ACTIVE(UserStatistics.USER_STATISTICS.LAST_ACTIVE);

	private final Field<?> field;

	StatisticType(Field<?> field){
		this.field = field;
	}

	public Field<?> getField(){
		return this.field;
	}
}
