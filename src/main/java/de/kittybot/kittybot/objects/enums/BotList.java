package de.kittybot.kittybot.objects.enums;

import de.kittybot.kittybot.utils.Config;
import io.javalin.http.Context;

import java.time.temporal.ChronoUnit;

public enum BotList{

	DISCORD_BOATS("Discord Boats", "https://discord.boats", Config.DISCORD_BOATS_TOKEN, 12, ChronoUnit.HOURS, "/bot/%s"),
	BOTLIST_SPACE("botlist.space", "https://botlist.space", Config.BOTLIST_SPACE_TOKEN, 1, ChronoUnit.DAYS, "/bot/%s"),
	BOTS_FOR_DISCORD_COM("Bots For Discord", "https://botsfordiscord.com", Config.BOTS_FOR_DISCORD_WEBHOOK_TOKEN, 1, ChronoUnit.DAYS, "/bot/%s"),
	DISCORD_BOTS_GG("Discord Bots", "https://discord.bots.gg"),
	TOP_GG("Top.gg", "https://top.gg", Config.TOP_GG_TOKEN, 12, ChronoUnit.HOURS, "/bot/%s"),
	DISCORD_EXTREME_LIST_XYZ("Delly", "https://discordextremelist.xyz"),
	DISCORD_BOT_LIST_COM("Discord Bot List ", "https://discordbotlist.com", Config.DISCORD_BOT_LIST_TOKEN, 12, ChronoUnit.HOURS, "/bots/%s"),
	DISCORD_SERVICES_COM("Discord Services ", "https://discordservices.net");

	private final String name, url, token, botUrl;
	private final long voteCooldown;
	private final ChronoUnit timeUnit;

	BotList(String name, String url, String token, int voteCooldown, ChronoUnit timeUnit, String botUrl){
		this.name = name;
		this.url = url;
		this.token = token;
		this.voteCooldown = voteCooldown;
		this.timeUnit = timeUnit;
		this.botUrl = botUrl;
	}

	BotList(String name, String url){
		this.name = name;
		this.url = url;
		this.token = null;
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

	public boolean verify(Context ctx){
		var header = ctx.header("Authorization");
		if(header == null){
			return false;
		}
		return header.equals(this.token);
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
