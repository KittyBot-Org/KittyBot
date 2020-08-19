package de.anteiku.kittybot.objects.command;

public enum Category{
	INFORMATIVE("\u2139 Informative"),
	UTILITIES("\uD83D\uDEE0 Utilities")
	MUSIC("\uD83C\uDFB6 Music"),
	NEKO("\uD83D\uDC31 Neko");

	private final String friendlyName;

	Category(final String friendlyName){
		this.friendlyName = friendlyName;
	}

	public String getFriendlyName(){
		return friendlyName;
	}
}
