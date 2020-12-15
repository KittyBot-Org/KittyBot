package de.kittybot.kittybot.command;

import de.kittybot.kittybot.objects.Emoji;

public enum Category{

	INFORMATION(Emoji.INFORMATION, "Information"),
	UTILITIES(Emoji.UTILITIES, "Utilities"),
	SNIPE(Emoji.SNIPE, "Snipe"),
	ROLES(Emoji.ROLES, "Roles"),
	MUSIC(Emoji.MUSIC, "Music"),
	NEKO(Emoji.NEKO, "Neko"),
	ADMIN(Emoji.ADMIN, "Admin"),
	BOT_OWNER(Emoji.BOT_OWNER, "Bot Owner");

	private final Emoji emoji;
	private final String name;

	Category(Emoji emoji, String name){
		this.emoji = emoji;
		this.name = name;
	}

	public String getEmote(){
		return this.emoji.getAsMention();
	}

	public String getEmoteUrl(){
		return "https://cdn.discordapp.com/emojis/" + this.emoji.getId() + ".png";
	}

	public String getName(){
		return this.name;
	}
}
