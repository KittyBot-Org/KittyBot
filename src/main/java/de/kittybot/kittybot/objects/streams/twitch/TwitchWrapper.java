package de.kittybot.kittybot.objects.streams.twitch;

import de.kittybot.kittybot.objects.streams.BearerToken;
import de.kittybot.kittybot.objects.streams.Stream;
import net.dv8tion.jda.api.utils.data.DataObject;
import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TwitchWrapper{

	private static final Logger LOG = LoggerFactory.getLogger(TwitchWrapper.class);
	private static final String BASE_URL = "https://api.twitch.tv/helix/";
	private static final String OAUTH2_URL = "https://id.twitch.tv/oauth2/token";

	private final String clientId;
	private final String clientSecret;
	private final OkHttpClient httpClient;
	private BearerToken bearerToken;

	public TwitchWrapper(String clientId, String clientSecret, OkHttpClient httpClient){
		this.clientId = clientId;
		this.clientSecret = clientSecret;
		this.httpClient = httpClient;
		this.bearerToken = requestBearerToken();
		if(this.bearerToken == null){
			LOG.error("Could not retrieve Bearer Token. Please check your twitch client id & token");
			return;
		}
		LOG.info("Bearer Token retrieved");
	}

	private BearerToken requestBearerToken(){
		var formBody = new FormBody.Builder().add("client_id", this.clientId).add("client_secret", this.clientSecret).add("grant_type", "client_credentials");
		var request = new Request.Builder()
			.url(OAUTH2_URL)
			.method("POST", formBody.build())
			.build();
		try(var resp = this.httpClient.newCall(request).execute()){
			var body = resp.body();
			if(!resp.isSuccessful() || body == null){
				LOG.error("Url: {} Code: {} Body: {}", resp.request().url(), resp.code(), body == null ? "null" : body.string());
				return null;
			}
			var json = DataObject.fromJson(body.string());
			return new BearerToken(json.getString("access_token"), json.getLong("expires_in"));
		}
		catch(IOException e){
			LOG.error("Error while reading body", e);
		}
		return null;
	}

	public TwitchUser getUserByUsername(String username, boolean reTry){
		try(var resp = newRequest("users?login=%s", username).execute()){
			var body = resp.body();
			if(!resp.isSuccessful() || body == null){
				LOG.error("Url: {} Code: {} Body: {}", resp.request().url(), resp.code(), body == null ? "null" : body.string());
				if(resp.code() == 401){
					this.bearerToken = null;
					if(!reTry){
						return getUserByUsername(username, true);
					}
				}
				return null;
			}
			var data = DataObject.fromJson(body.string()).getArray("data");
			if(data.isEmpty()){
				return null;
			}
			return new TwitchUser(data.getObject(0));
		}
		catch(IOException e){
			LOG.error("Error while unpacking request body", e);
		}
		return null;
	}

	private Call newRequest(String url, Object... params){
		if(this.bearerToken == null || this.bearerToken.isExpired()){
			this.bearerToken = requestBearerToken();
			LOG.info("New Bearer Token retrieved");
		}
		if(this.bearerToken == null){
			throw new NullPointerException("bearerToken is null");
		}
		return this.httpClient.newCall(new Request.Builder()
			.url(BASE_URL + String.format(url, params))
			.addHeader("Client-ID", this.clientId)
			.addHeader("Authorization", this.bearerToken.getAccessToken())
			.build()
		);
	}

	public List<Stream> getStreams(List<Long> userIds, boolean reTry){
		var streams = new ArrayList<Stream>();
		do{
			var users = userIds.subList(0, Math.min(userIds.size(), 100));
			var query = users.stream().map(user -> "user_id=" + user).collect(Collectors.joining("&"));
			users.clear();
			try(var resp = newRequest("streams?" + query).execute()){
				var body = resp.body();
				if(!resp.isSuccessful() || body == null){
					LOG.error("Url: {} Code: {} Body: {}", resp.request().url(), resp.code(), body == null ? "null" : body.string());
					if(resp.code() == 401){
						this.bearerToken = null;
						if(!reTry){
							return getStreams(userIds, true);
						}
					}
					continue;
				}
				var data = DataObject.fromJson(body.string()).getArray("data");
				for(var o = 0; o < data.length(); o++){
					streams.add(Stream.fromTwitchJSON(data.getObject(o)));
				}
			}
			catch(IOException e){
				LOG.error("Error while unpacking request body", e);
			}
		}
		while(!userIds.isEmpty());
		return streams;
	}

}
