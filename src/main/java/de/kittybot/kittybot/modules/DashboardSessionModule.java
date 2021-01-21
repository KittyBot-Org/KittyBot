package de.kittybot.kittybot.modules;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.github.benmanes.caffeine.cache.stats.CacheStats;
import com.jagrosh.jdautilities.oauth2.OAuth2Client;
import com.jagrosh.jdautilities.oauth2.Scope;
import com.jagrosh.jdautilities.oauth2.entities.OAuth2Guild;
import com.jagrosh.jdautilities.oauth2.requests.OAuth2Action;
import de.kittybot.kittybot.objects.data.GuildData;
import de.kittybot.kittybot.objects.module.Module;
import de.kittybot.kittybot.objects.session.DashboardSession;
import de.kittybot.kittybot.objects.session.DashboardSessionController;
import de.kittybot.kittybot.utils.Config;
import de.kittybot.kittybot.utils.exporters.Metrics;
import io.jsonwebtoken.security.Keys;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.guild.member.GuildMemberUpdateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static de.kittybot.kittybot.jooq.Tables.SESSIONS;


public class DashboardSessionModule extends Module{

	private static final Logger LOG = LoggerFactory.getLogger(DashboardSessionModule.class);
	private static final Scope[] SCOPES = {Scope.IDENTIFY, Scope.GUILDS};

	private SecretKey secretKey;
	private LoadingCache<Long, DashboardSession> sessions;
	private LoadingCache<Long, Set<Long>> userGuilds;
	private LoadingCache<Long, GuildData> guilds;
	private OAuth2Client oAuth2Client;

	public static Scope[] getScopes(){
		return SCOPES;
	}

	@Override
	public void onEnable(){
		this.secretKey = Keys.hmacShaKeyFor(Config.SIGNING_KEY.getBytes(StandardCharsets.UTF_8));
		this.sessions = Caffeine.newBuilder()
			.expireAfterAccess(2, TimeUnit.HOURS)
			.recordStats()
			.build(this::retrieveDashboardSession);

		this.userGuilds = Caffeine.newBuilder()
			.expireAfterAccess(2, TimeUnit.HOURS)
			.recordStats()
			.build(this::retrieveUserGuilds);

		this.guilds = Caffeine.newBuilder()
			.expireAfterAccess(2, TimeUnit.HOURS)
			.recordStats()
			.build(this::retrieveGuild);
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

	private Set<Long> retrieveUserGuilds(long userId){
		var action = retrieveGuilds(userId);
		if(action == null){
			return null;
		}
		try{
			var guildCache = this.modules.getShardManager().getGuildCache();
			var userGuilds = action.complete().stream().filter(guild -> guild.hasPermission(Permission.ADMINISTRATOR) && guildCache.getElementById(guild.getIdLong()) != null).collect(Collectors.toSet());
			userGuilds.forEach(guild -> this.guilds.put(guild.getIdLong(), new GuildData(guild)));
			return userGuilds.stream().map(OAuth2Guild::getIdLong).collect(Collectors.toSet());
		}
		catch(IOException e){
			LOG.info("Failed to pull user guilds");
		}
		return null;
	}

	private GuildData retrieveGuild(long guildId){
		var guild = this.modules.getGuildById(guildId);
		if(guild == null){
			return null;
		}
		return new GuildData(guild);
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

	public OAuth2Action<List<OAuth2Guild>> retrieveGuilds(long userId){
		var session = get(userId);
		if(session == null){
			return null;
		}
		return this.oAuth2Client.getGuilds(session);
	}

	public DashboardSession get(long userId){
		return this.sessions.get(userId);
	}

	@Override
	public void onGuildMemberUpdate(@Nonnull GuildMemberUpdateEvent event){

	}

	public List<GuildData> getGuilds(long userId){
		var guilds = this.userGuilds.get(userId);
		if(guilds == null){
			return null;
		}
		return guilds.stream().map(guildId -> this.guilds.get(guildId)).filter(Objects::nonNull).collect(Collectors.toList());
	}

	public void add(DashboardSession session){
		Metrics.DASHBOARD_ACTIONS.labels("create").inc();
		insertDashboardSession(session);
		this.sessions.put(session.getUserId(), session);
	}

	private void insertDashboardSession(DashboardSession session){
		this.modules.get(DatabaseModule.class).getCtx().insertInto(SESSIONS)
			.columns(SESSIONS.USER_ID, SESSIONS.ACCESS_TOKEN, SESSIONS.REFRESH_TOKEN, SESSIONS.EXPIRATION)
			.values(session.getUserId(), session.getAccessToken(), session.getRefreshToken(), session.getExpirationTime())
			.onDuplicateKeyIgnore()
			.execute();
	}

	public boolean has(long userId){
		return get(userId) != null;
	}

	public void delete(long userId){
		Metrics.DASHBOARD_ACTIONS.labels("delete").inc();
		this.deleteDashboardSession(userId);
		this.sessions.invalidate(userId);
	}

	private void deleteDashboardSession(long userId){
		this.modules.get(DatabaseModule.class).getCtx().deleteFrom(SESSIONS).where(SESSIONS.USER_ID.eq(userId)).execute();
	}

	public CacheStats getStats(){
		return this.sessions.stats();
	}

	public OAuth2Client getOAuth2Client(){
		return this.oAuth2Client;
	}

	public SecretKey getSecretKey(){
		return this.secretKey;
	}

}
