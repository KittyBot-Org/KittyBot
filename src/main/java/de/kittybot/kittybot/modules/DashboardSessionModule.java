package de.kittybot.kittybot.modules;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.github.benmanes.caffeine.cache.stats.CacheStats;
import com.jagrosh.jdautilities.oauth2.OAuth2Client;
import com.jagrosh.jdautilities.oauth2.Scope;
import com.jagrosh.jdautilities.oauth2.entities.OAuth2Guild;
import de.kittybot.kittybot.module.Module;
import de.kittybot.kittybot.objects.DashboardSession;
import de.kittybot.kittybot.objects.DashboardSessionController;
import de.kittybot.kittybot.utils.Config;
import de.kittybot.kittybot.utils.exporters.Metrics;
import io.jsonwebtoken.security.Keys;
import net.dv8tion.jda.api.events.guild.member.GuildMemberUpdateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static de.kittybot.kittybot.jooq.Tables.SESSIONS;


public class DashboardSessionModule extends Module{

	private static final Logger LOG = LoggerFactory.getLogger(DashboardSessionModule.class);
	private static final Scope[] SCOPES = {Scope.IDENTIFY, Scope.GUILDS};

	private SecretKey secretKey;
	private LoadingCache<Long, DashboardSession> sessionCache;
	private Map<Long, Boolean> userSessionCache;
	private Map<Long, Set<Long>> userGuilds;
	private OAuth2Client oAuth2Client;

	public static Scope[] getScopes(){
		return SCOPES;
	}

	@Override
	public void onEnable(){
		this.secretKey = Keys.hmacShaKeyFor(Config.SIGNING_KEY.getBytes(StandardCharsets.UTF_8));
		this.sessionCache = Caffeine.newBuilder()
				.expireAfterAccess(15, TimeUnit.MINUTES)
				.recordStats()
				.build(this::retrieveDashboardSession);
		this.userSessionCache = new HashMap<>();
		this.userGuilds = new HashMap<>();
		init();
	}

	private DashboardSession retrieveDashboardSession(long userId){
		var dbModule = this.modules.get(DatabaseModule.class);
		try(var ctx = dbModule.getCtx().selectFrom(SESSIONS)){
			var res = ctx.where(SESSIONS.USER_ID.eq(userId)).fetchOne();
			if(res == null){
				return null;
			}
			return new DashboardSession(res);
		}
	}

	private void init(){
		if(Config.BOT_SECRET.isBlank() || Config.BOT_ID == -1){
			LOG.error("OAuth2 disabled because secret or id is missing");
			return;
		}
		this.oAuth2Client = new OAuth2Client.Builder()
				.setClientId(Config.BOT_ID)
				.setClientSecret(Config.BOT_SECRET)
				.setOkHttpClient(this.modules.getHttpClient())
				.setSessionController(new DashboardSessionController(this))
				.build();
	}

	@Override
	public void onGuildMemberUpdate(@Nonnull GuildMemberUpdateEvent event){

	}

	public List<OAuth2Guild> getGuilds(DashboardSession session){
		try{
			return this.oAuth2Client.getGuilds(session).complete();
		}
		catch(IOException e){
			LOG.error("Error retrieving guilds for user: {}", session.getUserId(), e);
		}
		return Collections.emptyList();
	}

	/*
	public List<GuildData> getGuilds(long userId){
		var guilds = this.userGuilds.get(userId);
		if(guilds == null){
			var session = get(userId);
			oAuth2Client.getGuilds(session).complete();
		}
	}*/

	public void add(DashboardSession session){
		Metrics.DASHBOARD_ACTIONS.labels("create").inc();
		saveDashboardSession(session);
		this.sessionCache.put(session.getUserId(), session);
		this.userSessionCache.put(session.getUserId(), true);
	}

	private void saveDashboardSession(DashboardSession session){
		this.modules.get(DatabaseModule.class).getCtx().insertInto(SESSIONS)
				.columns(SESSIONS.USER_ID, SESSIONS.ACCESS_TOKEN, SESSIONS.REFRESH_TOKEN, SESSIONS.EXPIRATION)
				.values(session.getUserId(), session.getAccessToken(), session.getRefreshToken(), session.getExpirationTime())
				.onDuplicateKeyIgnore()
				.execute();
	}

	public DashboardSession get(long userId){
		return this.sessionCache.get(userId);
	}

	public boolean has(long userId){
		var hasSession = this.userSessionCache.get(userId);
		this.userSessionCache.put(userId, hasSession);
		return hasSession;
	}

	public void delete(long userId){
		Metrics.DASHBOARD_ACTIONS.labels("delete").inc();
		//GuildCache.uncacheUser(userId);
		this.deleteDashboardSession(userId);
		this.sessionCache.invalidate(userId);
		this.userSessionCache.put(userId, false);
	}

	private void deleteDashboardSession(long userId){
		this.modules.get(DatabaseModule.class).getCtx().deleteFrom(SESSIONS).where(SESSIONS.USER_ID.eq(userId)).execute();
	}

	public CacheStats getStats(){
		return this.sessionCache.stats();
	}

	public OAuth2Client getOAuth2Client(){
		return this.oAuth2Client;
	}

	public SecretKey getSecretKey(){
		return this.secretKey;
	}

}
