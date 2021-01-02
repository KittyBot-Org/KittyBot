package de.kittybot.kittybot.objects;

public enum API{

	// stats APIs
	DISCORD_BOTS("discord.bots.gg", "https://discord.bots.gg/api/v1/bots/%s/stats", "guildCount"),
	TOP_GG("top.gg", "https://top.gg/api/bots/%s/stats", "server_count"),
	DISCORD_EXTREME_LIST("discordextremelist.xyz", "https://api.discordextremelist.xyz/v2/bot/%s/stats", "guildCount"),
	DISCORD_BOATS("discord.boats", "https://discord.boats/api/bot/%s", "server_count"),
	BOTS_FOR_DISCORD("botsfordiscord.com", "https://botsfordiscord.com/api/bot/%s", "server_count"),
	BOTLIST_SPACE("botlist.space", "https://botsfordiscord.com/api/bot/%s", "server_count"),

	// other
	PURR_BOT("purr bot", "https://purrbot.site/api/img/%s/%s/%s"),
	HASTEBIN("hastebin", ""),
	GOOGLE_TRANSLATE_API("google translate api", "https://translate.google.com/translate_a/single?client=gtx&sl=%s&tl=%s&dt=t&q=%s&ie=UTF-8&oe=UTF-8");

	private final String name;
	private final String url;
	private final String statsParameter;

	API(final String name, final String url, final String statsParameter){
		this.name = name;
		this.url = url;
		this.statsParameter = statsParameter;
	}

	API(final String name, final String url){
		this.name = name;
		this.url = url;
		this.statsParameter = null;
	}

	public String getName(){
		return this.name;
	}

	public String getUrl(){
		return this.url;
	}

	public String getStatsParameter(){
		return this.statsParameter;
	}
}