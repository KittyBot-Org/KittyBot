package de.kittybot.kittybot.streams.twitch;

import de.kittybot.kittybot.objects.Language;
import de.kittybot.kittybot.streams.BearerToken;
import de.kittybot.kittybot.streams.Game;
import de.kittybot.kittybot.streams.Stream;
import jdk.jfr.ContentType;
import net.dv8tion.jda.api.utils.data.DataObject;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.Normalizer;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class TwitchWrapper{

	private static final Logger LOG = LoggerFactory.getLogger(TwitchWrapper.class);
	private static final String BASE_URL = "https://api.twitch.tv/helix/";

	private final String clientId;
	private final String clientSecret;
	private BearerToken bearerToken;
	private final OkHttpClient httpClient;

	public TwitchWrapper(String clientId, String clientSecret, OkHttpClient httpClient){
		this.clientId = clientId;
		this.clientSecret = clientSecret;
		this.httpClient = httpClient;
		this.bearerToken = requestBearerToken();
		LOG.info("Bearer Token retrieved");
	}

	public List<Stream> getStreams(String userName){
		LOG.info("test1");
		try(var resp = newRequest("streams?user_login=%s", userName).execute()){
			LOG.info("test3");
			var body = resp.body();
			LOG.info("test4");
			if(body == null){
				LOG.info("test5");
				return null;
			}
			LOG.info("test6");
			var data = DataObject.fromJson(body.string()).getArray("data");
			LOG.info("test7");
			if(data.isEmpty()){
				LOG.info("BRUH");
				return new ArrayList<>();
			}
			LOG.info("test8");
			LOG.info(data.toString());
			var streams = data.toList().stream().map(o -> Stream.fromTwitchJSON((DataObject)o)).collect(Collectors.toList());
			/*var streams = new ArrayList<Stream>();
			for(var i = 0; i < data.length(); i++){
				var json = data.getObject(i);
				streams.add(Stream.fromTwitchJSON(json));
			}*/
			LOG.info("test9");
			LOG.info(streams.toString());
			LOG.info("BRUH2");
			return streams;
		}
		catch(IOException e){
			LOG.error("Error while unpacking request body", e);
		}
		return new ArrayList<>();
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

	private Call newRequest(String url, Object... params){
		/*if(this.bearerToken.isExpired()){
			this.bearerToken = requestBearerToken();
		}*/
		LOG.info("test2");
		return this.httpClient.newCall(new Request.Builder()
				.url(BASE_URL + String.format(url, params))
				.addHeader("Client-ID", this.clientId)
				.addHeader("Authorization", this.bearerToken.getAccessToken())
				.build()
		);
	}

}
