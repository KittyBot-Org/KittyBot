package de.kittybot.kittybot.objects.enums;

import de.kittybot.kittybot.utils.Config;

import java.time.temporal.ChronoUnit;

public enum BotList{

	DISCORD_BOATS("Discord Boats", "https://discord.boats", 12, ChronoUnit.HOURS, "/bot/%s"),
	BOTLIST_SPACE("botlist.space", "https://botlist.space", 1, ChronoUnit.DAYS, "/bot/%s"),
	BOTS_FOR_DISCORD_COM("Bots For Discord", "https://botsfordiscord.com", 1, ChronoUnit.DAYS, "/bot/%s"),
	DISCORD_BOTS_GG("Discord Bots", "https://discord.bots.gg"),
	TOP_GG("Top.gg", "https://top.gg", 12, ChronoUnit.HOURS, "/bot/%s"),
	DISCORD_EXTREME_LIST_XYZ("Delly", "https://discordextremelist.xyz"),
	DISCORD_BOT_LIST_COM("Discord Bot List ", "https://discordbotlist.com", 12, ChronoUnit.HOURS, "/bots/%s"),
	DISCORD_SERVICES_COM("Discord Services ", "https://discordservices.net");

	private final String name, url, botUrl;
	private final long voteCooldown;
	private final ChronoUnit timeUnit;

	BotList(String name, String url, int voteCooldown, ChronoUnit timeUnit, String botUrl){
		this.name = name;
		this.url = url;
		this.voteCooldown = voteCooldown;
		this.timeUnit = timeUnit;
		this.botUrl = botUrl;
	}

	BotList(String name, String url){
		this.name = name;
		this.url = url;
		this.voteCooldown = -1;
		this.timeUnit = null;
		this.botUrl = null;
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

	public boolean canVote(){
		return this.botUrl != null;
	}

	public String getBotUrl(){
		if(this.botUrl == null){
			return null;
		}
		return this.url + String.format(this.botUrl, Config.BOT_ID);
	}

}
