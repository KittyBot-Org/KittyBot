package de.kittybot.kittybot.slashcommands.application;

import de.kittybot.kittybot.objects.enums.Emoji;

public enum Category{

	INFORMATION(Emoji.INFORMATION, "Information"),
	UTILITIES(Emoji.UTILITIES, "Utilities"),
	SNIPE(Emoji.SNIPE, "Snipe"),
	ROLES(Emoji.ROLES, "Roles"),
	MUSIC(Emoji.MUSIC, "Music"),
	NEKO(Emoji.NEKO, "Neko"),
	ADMIN(Emoji.ADMIN, "Admin"),
	NOTIFICATION(Emoji.NOTIFICATION, "Notification"),
	ANNOUNCEMENT(Emoji.ANNOUNCEMENT, "Announcement"),
	TAGS(Emoji.TAGS, "Tags"),
	DEV(Emoji.DEV, "Developer");

	private final Emoji emoji;
	private final String name;

	Category(Emoji emoji, String name){
		this.emoji = emoji;
		this.name = name;
	}

	public String getEmote(){
		return this.emoji.get();
	}

	public String getEmoteUrl(){
		return "https://cdn.discordapp.com/emojis/" + this.emoji.getId() + ".png";
	}

	public String getName(){
		return this.name;
	}

	public String getUrl(){
		return "http://localhost/commands#" + this.name.toLowerCase();
	}
}
