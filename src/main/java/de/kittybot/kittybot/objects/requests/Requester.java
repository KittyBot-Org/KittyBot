package de.kittybot.kittybot.objects.requests;

import de.kittybot.kittybot.KittyBot;
import net.dv8tion.jda.api.utils.data.DataObject;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Requester{
	private static final Logger LOG = LoggerFactory.getLogger(Requester.class);
	private static final OkHttpClient HTTP_CLIENT = new OkHttpClient();
	private static final Request.Builder REQUEST_BUILDER = new Request.Builder().header("user-agent", "de.kittybot");

	private Requester(){}

	public static DataObject executeRequest(final Request request){
		return executeRequest(request, null);
	}

	public static DataObject executeRequest(final Request request, final API api){
		final var requestUrl = request.url();
		try(final var response = HTTP_CLIENT.newCall(request).execute()){
			//noinspection ConstantConditions stfu :)
			final var json = DataObject.fromJson(response.body().string());
			final var code = response.code();
			if(code != 200){
				LOG.warn("Failed to send a request to {} | code: {} | response: {}", requestUrl, code, json);
				return json;
			}
			if(api != null){
				LOG.info("Successfully executed a stats update request to {} API", api.getName());
			}
			return json;
		}
		catch(final Exception ex){
			LOG.error("There was an error while sending a request to {}", requestUrl);
		}
		return DataObject.empty();
	}

	public static String getNeko(final String type){
		final var url = String.format(API.NEKOS_LIFE.getUrl(), type);
		REQUEST_BUILDER.url(url);
		final var json = executeRequest(REQUEST_BUILDER.build());
		return json.getString("url");
	}

	public static String postToHastebin(final String content){
		final var url = API.HASTEBIN.getUrl();
		final var requestBody = RequestBody.create(MediaType.parse("text/html; charset=utf-8"), content);
		REQUEST_BUILDER.url(url + "/documents");
		REQUEST_BUILDER.post(requestBody);
		final var json = executeRequest(REQUEST_BUILDER.build());
		return url + "/" + json.getString("key");
	}

	public static void updateStats(final API api, final int guildCount){
		final var url = String.format(api.getUrl(), KittyBot.getJda().getSelfUser().getId());
		final var requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"),
				DataObject.empty()
						.put(api.getStatsParameter(), guildCount)
						.toString());
		REQUEST_BUILDER.url(url);
		REQUEST_BUILDER.header("Authorization", api.getKey());
		REQUEST_BUILDER.post(requestBody);
		executeRequest(REQUEST_BUILDER.build(), api);
	}

	public static OkHttpClient getHttpClient(){
		return HTTP_CLIENT;
	}
}