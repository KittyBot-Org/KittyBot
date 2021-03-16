package de.kittybot.kittybot.slashcommands.application;

import de.kittybot.kittybot.objects.enums.Emoji;
import de.kittybot.kittybot.utils.Config;
import net.dv8tion.jda.api.utils.data.DataObject;

public enum Category{

	INFORMATION(Emoji.INFORMATION, "Information"),
	UTILITIES(Emoji.UTILITIES, "Utilities"),
	SNIPE(Emoji.SNIPE, "Snipe"),
	ROLES(Emoji.ROLES, "Roles"),
	MUSIC(Emoji.MUSIC, "Music"),
	NEKO(Emoji.NEKO, "Neko"),
	ADMIN(Emoji.ADMIN, "Admin"),
	NOTIFICATION(Emoji.NOTIFICATION, "Notification"),
	ANNOUNCEMENT(Emoji.ANNOUNCEMENT, "Stream Announcement"),
	TAGS(Emoji.TAGS, "Tags"),
	USER(Emoji.STATISTICS, "User"),
	DEV(Emoji.DEV, "Developer");

	private final Emoji emote;
	private final String name;

	Category(Emoji emote, String name){
		this.emote = emote;
		this.name = name;
	}

	public String getEmote(){
		return this.emote.get();
	}

	public String getName(){
		return this.name;
	}

	public DataObject toJSON(){
		return DataObject.empty()
			.put("name", this.name)
			.put("url", getUrl())
			.put("emote", getEmoteUrl());
	}

	public String getUrl(){
		return "http://" + Config.ORIGIN_URL + "/commands#" + this.name.toLowerCase();
	}

	public String getEmoteUrl(){
		return "https://cdn.discordapp.com/emojis/" + this.emote.getId() + ".png";
	}
}
