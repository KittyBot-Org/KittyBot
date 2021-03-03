package de.kittybot.kittybot.objects.streams.twitch;

import de.kittybot.kittybot.objects.streams.BearerToken;
import de.kittybot.kittybot.objects.streams.Stream;
import de.kittybot.kittybot.utils.Config;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.requests.Method;
import net.dv8tion.jda.internal.requests.Requester;
import net.dv8tion.jda.internal.requests.Route;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class TwitchWrapper{

	private static final Logger LOG = LoggerFactory.getLogger(TwitchWrapper.class);
	private static final String BASE_URL = "https://api.twitch.tv/helix/";
	private static final String OAUTH2_URL = "https://id.twitch.tv/oauth2/token";
	private static final Route CREATE_SUBSCRIPTION = Route.custom(Method.POST, "eventsub/subscriptions");
	private static final Route DELETE_SUBSCRIPTION = Route.custom(Method.DELETE, "eventsub/subscriptions?id={subscription.id}");
	private static final Route GET_SUBSCRIPTIONS = Route.custom(Method.GET, "eventsub/subscriptions");

	private final String clientId;
	private final String clientSecret;
	private final String webhookCallback;
	private final String webhookSecret;
	private final OkHttpClient httpClient;
	private BearerToken bearerToken;
	private final Map<String, Subscription> subscriptions;

	public TwitchWrapper(String clientId, String clientSecret, String webhookCallback, String webhookSecret, OkHttpClient httpClient){
		this.clientId = clientId;
		this.clientSecret = clientSecret;
		this.webhookCallback = webhookCallback;
		this.webhookSecret = webhookSecret;
		this.httpClient = httpClient;
		this.bearerToken = requestBearerToken();
		this.subscriptions = new HashMap<>();
		if(this.bearerToken == null){
			LOG.error("Could not retrieve Bearer Token. Please check your twitch client id & token");
			return;
		}
		LOG.info("Bearer Token retrieved");
		LOG.info("Bearer Token: {}", bearerToken.getAccessToken());
		var subscriptions = retrieveSubscriptions();
		if(subscriptions == null){
			LOG.error("Error while retrieving active subscriptions");
			return;
		}
		this.subscriptions.putAll(subscriptions);
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

	public boolean subscribe(Subscription.Type type, Subscription.Condition condition){
		var rqBody = DataObject.empty()
			.put("type", type.getText())
			.put("version", "1")
			.put("condition", condition.toJSON())
			.put("transport", DataObject.empty()
				.put("method", "webhook")
				.put("callback", this.webhookCallback)
				.put("secret", this.webhookSecret)
			);
		LOG.info("Body: {}", rqBody.toString());
		try(var response = newCall(CREATE_SUBSCRIPTION.compile(), RequestBody.create(rqBody.toJson(), MediaType.parse("application/json"))).execute()){
			var body = response.body();
			if(body == null){
				return false;
			}
			var json = DataObject.fromJson(body.byteStream());
			if(response.isSuccessful()){
				var data = json.getArray("data");
				this.subscriptions.putAll(data.stream((array, i) -> Subscription.fromJSON(array.getObject(i))).collect(Collectors.toMap(Subscription::getId, Function.identity())));
				return true;
			}
			LOG.error("Error while subscribing to events Body: {}", json.toString());
		}
		catch(IOException e){
			LOG.error("Error while subscribing to events");
		}
		return false;
	}

	public boolean unsubscribe(String id){
		try(var response = newCall(DELETE_SUBSCRIPTION.compile(id), null).execute()){
			if(response.isSuccessful()){
				return true;
			}
		}
		catch(IOException e){
			LOG.error("Error while unsubscribing to events");
		}
		return false;
	}

	public Map<String, Subscription> retrieveSubscriptions(){
		try(var response = newCall(GET_SUBSCRIPTIONS.compile(), null).execute()){
			if(response.isSuccessful()){
				var body = response.body();
				if(body == null){
					return null;
				}
				var json = DataObject.fromJson(body.byteStream());
				var data = json.getArray("data");
				return data.stream((array, i) -> Subscription.fromJSON(array.getObject(i))).collect(Collectors.toMap(Subscription::getId, Function.identity()));
			}
		}
		catch(IOException e){
			LOG.error("Error while unsubscribing to events");
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

	public Stream getStream(long userId, boolean reTry){
		try(var resp = newRequest("streams?user_id=" + userId).execute()){
			var body = resp.body();
			if(!resp.isSuccessful() || body == null){
				LOG.error("Url: {} Code: {} Body: {}", resp.request().url(), resp.code(), body == null ? "null" : body.string());
				if(resp.code() == 401){
					this.bearerToken = null;
					if(!reTry){
						return getStream(userId, true);
					}
				}
				return null;
			}
			var data = DataObject.fromJson(body.byteStream()).getArray("data");
			return Stream.fromTwitchJSON(data.getObject(0));
		}
		catch(IOException e){
			LOG.error("Error while unpacking request body", e);
		}
		return null;
	}

	private Call newCall(Route.CompiledRoute route, RequestBody body){
		return this.httpClient.newCall(newBuilder(route).method(route.getMethod().name(), body).build());
	}

	private Request.Builder newBuilder(Route.CompiledRoute route){
		if(this.bearerToken == null || this.bearerToken.isExpired()){
			this.bearerToken = requestBearerToken();
			LOG.info("New Bearer Token retrieved");
		}
		if(this.bearerToken == null){
			throw new NullPointerException("bearerToken is null");
		}
		return new Request.Builder()
			.url(BASE_URL + route.getCompiledRoute())
			.addHeader("Client-ID", this.clientId)
			.addHeader("Authorization", this.bearerToken.getAccessToken());
	}

	public Map<String, Subscription> getSubscriptions(){
		return this.subscriptions;
	}

}
