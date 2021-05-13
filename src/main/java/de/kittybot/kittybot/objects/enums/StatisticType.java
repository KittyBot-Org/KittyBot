package de.kittybot.kittybot.objects.enums;

import de.kittybot.kittybot.jooq.tables.UserStatistics;
import org.jooq.Field;

public enum StatisticType{

	XP(UserStatistics.USER_STATISTICS.XP),
	COMMANDS_USED(UserStatistics.USER_STATISTICS.COMMANDS_USED),
	VOICE_TIME(UserStatistics.USER_STATISTICS.VOICE_TIME),
	STREAM_TIME(UserStatistics.USER_STATISTICS.STREAM_TIME),
	MESSAGES_SENT(UserStatistics.USER_STATISTICS.MESSAGES_SENT),
	STICKERS_SENT(UserStatistics.USER_STATISTICS.STICKERS_SENT),
	EMOTES_SENT(UserStatistics.USER_STATISTICS.EMOTES_SENT),
	LAST_ACTIVE(UserStatistics.USER_STATISTICS.LAST_ACTIVE);

	private final Field<?> field;

	StatisticType(Field<?> field){
		this.field = field;
	}

	public Field<?> getField(){
		return this.field;
	}
}
