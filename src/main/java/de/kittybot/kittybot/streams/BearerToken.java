package de.kittybot.kittybot.streams;

import java.time.Instant;

public class BearerToken{

	private final String accessToken;
	private final long expires;

	public BearerToken(String accessToken, long expiresIn){
		this.accessToken = accessToken;
		this.expires = Instant.now().getEpochSecond() + expiresIn;
	}

	public String getAccessToken(){
		return "Bearer " + this.accessToken;
	}

	public boolean isExpired(){
		return Instant.now().getEpochSecond() >= expires;
	}

}
