package de.kittybot.kittybot.objects.requests;

import de.kittybot.kittybot.objects.Config;

public enum API{
	// stats APIs
	DISCORD_BOTS("discord.bots.gg", Config.DISCORD_BOTS_TOKEN, "https://discord.bots.gg/api/v1/bots/%s/stats", "guildCount"),
	TOP_GG("top.gg", Config.TOP_GG_TOKEN, "https://top.gg/api/bots/%s/stats", "server_count"),
	DISCORD_EXTREME_LIST("discordextremelist.xyz", Config.DISCORD_EXTREME_LIST_TOKEN, "https://api.discordextremelist.xyz/v2/bot/%s/stats", "guildCount"),
	DISCORD_BOATS("discord.boats", Config.DISCORD_BOATS_TOKEN, "https://discord.boats/api/bot/%s", "server_count"),

	// other
	NEKOS_LIFE("nekos life", "https://nekos.life/api/v2/img/%s"),
	HASTEBIN("hastebin", Config.HASTEBIN_URL),
	GOOGLE_TRANSLATE_API("google translate api", "https://translate.google.com/translate_a/single?client=gtx&sl=%s&tl=%s&dt=t&q=%s&ie=UTF-8&oe=UTF-8");

	private final String name;
	private final String key;
	private final String url;
	private final String statsParameter;

	API(final String name, final String key, final String url, final String statsParameter){
		this.name = name;
		this.key = key;
		this.url = url;
		this.statsParameter = statsParameter;
	}

	API(final String name, final String url){
		this.name = name;
		this.key = null;
		this.url = url;
		this.statsParameter = null;
	}

	public String getName(){
		return this.name;
	}

	public String getKey(){
		return this.key;
	}

	public String getUrl(){
		return this.url;
	}

	public String getStatsParameter(){
		return this.statsParameter;
	}
}