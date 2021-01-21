package de.kittybot.kittybot.objects.session;

import com.jagrosh.jdautilities.oauth2.Scope;
import com.jagrosh.jdautilities.oauth2.session.Session;
import com.jagrosh.jdautilities.oauth2.session.SessionData;
import de.kittybot.kittybot.jooq.tables.records.SessionsRecord;
import de.kittybot.kittybot.modules.DashboardSessionModule;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public class DashboardSession implements Session{

	private final String accessToken, refreshToken, tokenType;
	private final OffsetDateTime expiration;
	private final Scope[] scopes;
	private long userId;

	public DashboardSession(SessionData sessionData){
		this.userId = -1;
		this.accessToken = sessionData.getAccessToken();
		this.refreshToken = sessionData.getRefreshToken();
		this.tokenType = sessionData.getTokenType();
		this.expiration = sessionData.getExpiration();
		this.scopes = sessionData.getScopes();
	}

	public DashboardSession(SessionsRecord record){
		this.userId = record.getUserId();
		this.accessToken = record.getAccessToken();
		this.refreshToken = record.getRefreshToken();
		this.tokenType = "Bearer";
		this.expiration = OffsetDateTime.of(record.getExpiration(), ZoneOffset.UTC);
		this.scopes = DashboardSessionModule.getScopes();
	}

	public long getUserId(){
		return this.userId;
	}

	public void setUserId(long userId){
		this.userId = userId;
	}

	@Override
	public String getAccessToken(){
		return this.accessToken;
	}

	@Override
	public String getRefreshToken(){
		return this.refreshToken;
	}

	@Override
	public Scope[] getScopes(){
		return this.scopes;
	}

	@Override
	public String getTokenType(){
		return this.tokenType;
	}

	@Override
	public OffsetDateTime getExpiration(){
		return this.expiration;
	}

	public LocalDateTime getExpirationTime(){
		return this.expiration.toLocalDateTime();
	}

}
