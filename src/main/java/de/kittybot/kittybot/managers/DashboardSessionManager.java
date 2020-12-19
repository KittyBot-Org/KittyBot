package de.kittybot.kittybot.managers;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.github.benmanes.caffeine.cache.stats.CacheStats;
import com.jagrosh.jdautilities.oauth2.OAuth2Client;
import com.jagrosh.jdautilities.oauth2.Scope;
import de.kittybot.kittybot.main.KittyBot;
import de.kittybot.kittybot.objects.DashboardSession;
import de.kittybot.kittybot.objects.DashboardSessionController;
import de.kittybot.kittybot.utils.exporters.Metrics;
import io.jsonwebtoken.security.Keys;
import net.dv8tion.jda.api.events.guild.member.GuildMemberUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.crypto.SecretKey;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static de.kittybot.kittybot.jooq.Tables.SESSIONS;


public class DashboardSessionManager extends ListenerAdapter{

	private static final Logger LOG = LoggerFactory.getLogger(DashboardSessionManager.class);
	private static final Scope[] SCOPES = {Scope.IDENTIFY, Scope.GUILDS};

	private final KittyBot main;
	private final SecretKey secretKey;
	private final LoadingCache<Long, DashboardSession> sessionCache;
	private final Map<Long, Boolean> userSessionCache;
	private final Map<Long, Set<Long>> userGuilds;
	private OAuth2Client oAuth2Client;

	public DashboardSessionManager(KittyBot main){
		this.main = main;
		this.secretKey = Keys.hmacShaKeyFor(main.getConfig().getBytes("signing_key"));
		this.sessionCache = Caffeine.newBuilder()
				.expireAfterAccess(15, TimeUnit.MINUTES)
				.recordStats()
				.build(this::retrieveDashboardSession);
		this.userSessionCache = new HashMap<>();
		this.userGuilds = new HashMap<>();
	}

	private DashboardSession retrieveDashboardSession(long userId){
		var dbManager = this.main.getDatabaseManager();
		try(var con = dbManager.getCon(); var ctx = dbManager.getCtx(con).selectFrom(SESSIONS)){
			var res = ctx.where(SESSIONS.USER_ID.eq(userId)).fetchOne();
			if(res != null){
				return new DashboardSession(res);
			}
		}
		catch(SQLException e){
			LOG.error("Error while retrieving Dashboard session", e);
		}
		return null;
	}

	public static Scope[] getScopes(){
		return SCOPES;
	}

	public void init(long userId){
		this.oAuth2Client = new OAuth2Client.Builder()
				.setClientId(userId)
				.setClientSecret(this.main.getConfig().getString("bot_secret"))
				.setOkHttpClient(this.main.getHttpClient())
				.setSessionController(new DashboardSessionController(this))
				.build();
	}

	@Override
	public void onGuildMemberUpdate(@Nonnull GuildMemberUpdateEvent event){

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
		var dbManager = this.main.getDatabaseManager();
		try(var con = dbManager.getCon()){
			dbManager.getCtx(con).insertInto(SESSIONS).columns(SESSIONS.fields()).values(session.getUserId(), session.getAccessToken(), session.getRefreshToken(), session.getExpiration()).onDuplicateKeyIgnore().execute();
		}
		catch(SQLException e){
			LOG.error("Error while inserting Dashboard session", e);
		}
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
		var dbManager = this.main.getDatabaseManager();
		try(var con = dbManager.getCon()){
			dbManager.getCtx(con).deleteFrom(SESSIONS).where(SESSIONS.USER_ID.eq(userId)).execute();
		}
		catch(SQLException e){
			LOG.error("Error while deleting Dashboard session", e);
		}
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
