package de.kittybot.kittybot.objects.data;

import de.kittybot.kittybot.jooq.tables.records.UserSettingsRecord;

public class UserSettings{

	private final long userId;
	private final int levelCardColor, levelFontColor;

	public UserSettings(UserSettingsRecord record){
		this.userId = record.getUserId();
		this.levelCardColor = record.getLevelCardColor();
		this.levelFontColor = record.getLevelCardFontColor();
	}

	public UserSettings(long userId, int levelCardColor, int levelFontColor){
		this.userId = userId;
		this.levelCardColor = levelCardColor;
		this.levelFontColor = levelFontColor;
	}

	public long getUserId(){
		return this.userId;
	}

	public int getLevelCardColor(){
		return this.levelCardColor;
	}

	public int getLevelCardBorderColor(){
		return 16777215;
	}

	public int getLevelCardPrimaryColor(){
		return 6053866;
	}

	public int getLevelCardFontColor(){
		return this.levelFontColor;
	}

}
