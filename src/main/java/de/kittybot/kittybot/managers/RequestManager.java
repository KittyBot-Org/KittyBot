package de.kittybot.kittybot.managers;

import de.kittybot.kittybot.main.KittyBot;
import de.kittybot.kittybot.objects.API;
import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.api.utils.data.DataObject;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class RequestManager{

	private static final Logger LOG = LoggerFactory.getLogger(RequestManager.class);
	private final Request.Builder requestBuilder = new Request.Builder().header("user-agent", "de.kittybot");
	private final KittyBot main;
	private final OkHttpClient httpClient;

	public RequestManager(KittyBot main){
		this.main = main;
		this.httpClient = main.getHttpClient();
	}

	public String translateText(String text, String language){
		var url = String.format(API.GOOGLE_TRANSLATE_API.getUrl(), "auto", language, URLEncoder.encode(text, StandardCharsets.UTF_8));
		requestBuilder.url(url);
		var json = DataArray.fromJson(executeRequest(requestBuilder.build()));
		return json.getArray(0).getArray(0).getString(0);
	}

	public String executeRequest(Request request){
		return executeRequest(request, null);
	}

	public String executeRequest(Request request, API api){
		var requestUrl = request.url();
		try(var response = this.httpClient.newCall(request).execute()){
			var body = response.body();
			var code = response.code();
			if(code != 200 || body == null){
				var string = body == null ? null : body.string();
				LOG.warn("Failed to send a request to {} | code: {} | response: {}", requestUrl, code, string);
				return string;
			}
			if(api != null){
				LOG.info("Successfully executed a stats update request to {} API", api.getName());
			}
			return body.string();
		}
		catch(final Exception ex){
			LOG.error("There was an error while sending a request to {}", requestUrl, ex);
		}
		return "";
	}

	public String getNeko(boolean nsfw, String type, String imageType){
		var url = String.format(API.PURR_BOT.getUrl(), nsfw ? "nsfw" : "sfw", type, imageType);
		requestBuilder.url(url);
		requestBuilder.method("GET", null);
		var json = DataObject.fromJson(executeRequest(requestBuilder.build()));
		return json.getString("link");
	}

	public String postToHastebin(String content){
		var url = API.HASTEBIN.getUrl();
		var requestBody = RequestBody.create(MediaType.parse("text/html; charset=utf-8"), content);
		requestBuilder.url(url + "/documents");
		requestBuilder.post(requestBody);
		var json = DataObject.fromJson(executeRequest(requestBuilder.build()));
		return url + "/" + json.getString("key");
	}

	public void updateStats(API api, int guildCount, String token){
		var url = String.format(api.getUrl(), this.main.getJDA().getSelfUser().getId());
		var requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"),
				DataObject.empty()
						.put(api.getStatsParameter(), guildCount)
						.toString());
		requestBuilder.url(url);
		requestBuilder.header("Authorization", token);
		requestBuilder.post(requestBody);
		executeRequest(requestBuilder.build(), api);
	}

}