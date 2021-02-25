package de.kittybot.kittybot.objects.data;

import de.kittybot.kittybot.jooq.tables.records.UserSettingsRecord;

public class UserSettings{

	private final long userId;
	private final String levelCardBackgroundUrl;
	private final int levelCardBackgroundColor, levelCardPrimaryColor, levelCardBorderColor, levelCardFontColor;

	public UserSettings(UserSettingsRecord record){
		this.userId = record.getUserId();
		this.levelCardBackgroundUrl = record.getLevelCardBackgroundUrl();
		this.levelCardBackgroundColor = record.getLevelCardBackgroundColor();
		this.levelCardPrimaryColor = record.getLevelCardPrimaryColor();
		this.levelCardBorderColor = record.getLevelCardBorderColor();
		this.levelCardFontColor = record.getLevelCardFontColor();
	}

	public long getUserId(){
		return this.userId;
	}

	public String getLevelCardBackgroundUrl(){
		return this.levelCardBackgroundUrl;
	}

	public int getLevelCardBackgroundColor(){
		return this.levelCardBackgroundColor;
	}

	public int getLevelCardPrimaryColor(){
		return this.levelCardPrimaryColor;
	}

	public int getLevelCardBorderColor(){
		return this.levelCardBorderColor;
	}

	public int getLevelCardFontColor(){
		return this.levelCardFontColor;
	}

}
