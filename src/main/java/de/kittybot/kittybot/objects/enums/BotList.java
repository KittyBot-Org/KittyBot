package de.kittybot.kittybot.objects.enums;

public enum BotList{

	DISCORD_BOATS("Discord Boats", "https://discord.boats"),
	BOTLIST_SPACE("botlist.space", "https://botlist.space/"),
	BOTS_FOR_DISCORD_COM("Bots For Discord", "https://botsfordiscord.com/"),
	DISCORD_BOTS_GG("Discord Bots", "https://discord.bots.gg/"),
	TOP_GG("Top.gg", "https://top.gg"),
	DISCORD_EXTREME_LIST_XYZ("Delly", "https://discordextremelist.xyz");

	private final String name, url;

	BotList(String name, String url){
		this.name = name;
		this.url = url;
	}

	public String getName(){
		return this.name;
	}

	public String getUrl(){
		return this.url;
	}
}
