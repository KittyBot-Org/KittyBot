package de.kittybot.kittybot.objects.session;

import com.jagrosh.jdautilities.oauth2.Scope;
import com.jagrosh.jdautilities.oauth2.session.Session;
import com.jagrosh.jdautilities.oauth2.session.SessionData;

import java.time.OffsetDateTime;

public class DashboardSession implements Session{

	private final String accessToken, refreshToken, tokenType;
	private final OffsetDateTime expiration;
	private final Scope[] scopes;
	private String userId;

	public DashboardSession(SessionData sessionData){
		this.userId = sessionData.getIdentifier();
		this.accessToken = sessionData.getAccessToken();
		this.refreshToken = sessionData.getRefreshToken();
		this.tokenType = sessionData.getTokenType();
		this.expiration = sessionData.getExpiration();
		this.scopes = sessionData.getScopes();
	}

	public String getUserId(){
		return this.userId;
	}

	public void setUserId(String userId){
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

}
