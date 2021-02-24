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

	public UserSettings(long userId){
		this.userId = userId;
		this.levelCardBackgroundUrl = "https://i.imgur.com/q4ueQpS.png";
		this.levelCardBackgroundColor = 3289650;
		this.levelCardPrimaryColor = 6053866;
		this.levelCardBorderColor = 16711679;
		this.levelCardFontColor = 16711679;
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
