package de.kittybot.kittybot.streams.twitch;

import de.kittybot.kittybot.streams.BearerToken;
import de.kittybot.kittybot.streams.Stream;
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

	private final String clientId;
	private final String clientSecret;
	private final OkHttpClient httpClient;
	private BearerToken bearerToken;

	public TwitchWrapper(String clientId, String clientSecret, OkHttpClient httpClient){
		this.clientId = clientId;
		this.clientSecret = clientSecret;
		this.httpClient = httpClient;
		this.bearerToken = requestBearerToken();
		LOG.info("Bearer Token retrieved");
	}

	private BearerToken requestBearerToken(){
		var formBody = new FormBody.Builder().add("client_id", this.clientId).add("client_secret", this.clientSecret).add("grant_type", "client_credentials");
		var request = new Request.Builder()
				.url("https://id.twitch.tv/oauth2/token")
				.method("POST", formBody.build())
				.build();
		try(var resp = this.httpClient.newCall(request).execute()){
			var body = resp.body();
			if(!resp.isSuccessful() || body == null){
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

	/*public Game getGame(long gameId){
		var resp = newRequest("games?id=%d", gameId);
		if(resp == null || resp.body() == null){
			return Game.getUnknown();
		}
		try{
			var json = DataObject.fromJson(resp.body().string());
			return Game.fromTwitchJSON(json.getObject("data"));
		}
		catch(IOException e){
			LOG.error("Error while unpacking request body", e);
		}
		return Game.getUnknown();
	} */

	public List<Stream> getStreams(List<String> userNames){
		var streams = new ArrayList<Stream>();
		do{
			var users = userNames.subList(0, Math.min(userNames.size(), 100));
			var query = users.stream().map(user -> "user_login=" + user).collect(Collectors.joining("&"));
			users.clear();
			try(var resp = newRequest("streams?" + query).execute()){
				var body = resp.body();
				if(body == null){
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
		while(!userNames.isEmpty());
		return streams;
	}

	private Call newRequest(String url, Object... params){
		LOG.info("Request to: " + url);
		if(this.bearerToken.isExpired()){
			this.bearerToken = requestBearerToken();
			LOG.info("New Bearer Token retrieved");
		}
		return this.httpClient.newCall(new Request.Builder()
				.url(BASE_URL + String.format(url, params))
				.addHeader("Client-ID", this.clientId)
				.addHeader("Authorization", this.bearerToken.getAccessToken())
				.build()
		);
	}

}
