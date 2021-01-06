package de.kittybot.kittybot.modules;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.github.benmanes.caffeine.cache.stats.CacheStats;
import com.jagrosh.jdautilities.oauth2.OAuth2Client;
import com.jagrosh.jdautilities.oauth2.Scope;
import de.kittybot.kittybot.module.Module;
import de.kittybot.kittybot.module.Modules;
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
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static de.kittybot.kittybot.jooq.Tables.SESSIONS;


public class DashboardSessionModule extends Module{

	private static final Logger LOG = LoggerFactory.getLogger(DashboardSessionModule.class);
	private static final Scope[] SCOPES = {Scope.IDENTIFY, Scope.GUILDS};

	private final Modules modules;
	private final SecretKey secretKey;
	private final LoadingCache<Long, DashboardSession> sessionCache;
	private final Map<Long, Boolean> userSessionCache;
	private final Map<Long, Set<Long>> userGuilds;
	private OAuth2Client oAuth2Client;

	public DashboardSessionModule(Modules modules){
		this.modules = modules;
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
		var dbModule = this.modules.getDatabaseModule();
		try(var con = dbModule.getCon(); var ctx = dbModule.getCtx(con).selectFrom(SESSIONS)){
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

	public static Scope[] getScopes(){
		return SCOPES;
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
		var dbModule = this.modules.getDatabaseModule();
		try(var con = dbModule.getCon()){
			dbModule.getCtx(con).insertInto(SESSIONS).columns(SESSIONS.fields()).values(
					session.getUserId(), session.getAccessToken(), session.getRefreshToken(), session.getExpiration()).onDuplicateKeyIgnore().execute();
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
		var dbModule = this.modules.getDatabaseModule();
		try(var con = dbModule.getCon()){
			dbModule.getCtx(con).deleteFrom(SESSIONS).where(SESSIONS.USER_ID.eq(userId)).execute();
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
