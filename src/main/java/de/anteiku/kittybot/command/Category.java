package de.anteiku.kittybot.command;

import de.anteiku.kittybot.objects.Config;

public enum Category{
	INFORMATIVE("745709333127626762", "Informative"),
	UTILITIES("745709332519321620", "Utilities"),
	ROLES("745709332519321620", "Roles"),
	MUSIC("745710566571638885", "Music"),
	NEKO("609028855289872386", "Neko"),
	ADMIN("745715466349314049", "Admin");

	private final String emoteId;
	private final String friendlyName;

	Category(final String emoteId, final String friendlyName){
		this.emoteId = emoteId;
		this.friendlyName = friendlyName;
	}

	public String getEmote(){
		return "<:bla:" + emoteId + ">";
	}

	public String getEmoteUrl(){
		return "https://cdn.discordapp.com/emojis/" + emoteId + ".png";
	}

	public String getFriendlyName(){
		return friendlyName;
	}

	public String getUrl(){
		return Config.ORIGIN_URL + "/commands#" + friendlyName.toLowerCase();
	}
}
