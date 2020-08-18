package de.anteiku.kittybot.objects.command;

public enum Category{
	INFORMATIVE("\u2139 Informative"),
	MUSIC("\uD83C\uDFB6 Music"),
	NEKO("\uD83D\uDCA9 Neko"),
	UTILITIES("\uD83D\uDEE0 Utilities");

	private final String friendlyName;

	Category(final String friendlyName){
		this.friendlyName = friendlyName;
	}

	public String getFriendlyName(){
		return friendlyName;
	}
}