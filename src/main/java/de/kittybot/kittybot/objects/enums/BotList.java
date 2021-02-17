package de.kittybot.kittybot.objects.enums;

import java.time.temporal.ChronoUnit;

public enum BotList{

	DISCORD_BOATS("Discord Boats", "https://discord.boats", 12, ChronoUnit.HOURS),
	BOTLIST_SPACE("botlist.space", "https://botlist.space/", 1, ChronoUnit.DAYS),
	BOTS_FOR_DISCORD_COM("Bots For Discord", "https://botsfordiscord.com/", 1, ChronoUnit.DAYS),
	DISCORD_BOTS_GG("Discord Bots", "https://discord.bots.gg/"),
	TOP_GG("Top.gg", "https://top.gg", 12, ChronoUnit.HOURS),
	DISCORD_EXTREME_LIST_XYZ("Delly", "https://discordextremelist.xyz"),
	DISCORD_BOT_LIST_COM("DISCORD BOT LIST ", "https://discordbotlist.com", 12, ChronoUnit.HOURS);

	private final String name, url;
	private final long voteCooldown;
	private final ChronoUnit timeUnit;

	BotList(String name, String url, int voteCooldown, ChronoUnit timeUnit){
		this.name = name;
		this.url = url;
		this.voteCooldown = voteCooldown;
		this.timeUnit = timeUnit;
	}

	BotList(String name, String url){
		this.name = name;
		this.url = url;
		this.voteCooldown = -1;
		this.timeUnit = null;
	}

	public String getName(){
		return this.name;
	}

	public String getUrl(){
		return this.url;
	}

	public long getVoteCooldown(){
		return this.voteCooldown;
	}

	public ChronoUnit getTimeUnit(){
		return this.timeUnit;
	}

}
