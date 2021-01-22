package de.kittybot.kittybot.modules;

import de.kittybot.kittybot.objects.enums.API;
import de.kittybot.kittybot.objects.enums.Language;
import de.kittybot.kittybot.objects.module.Module;
import de.kittybot.kittybot.utils.Config;
import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.api.utils.data.DataObject;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class RequestModule extends Module{

	private static final Logger LOG = LoggerFactory.getLogger(RequestModule.class);

	private final Request.Builder requestBuilder = new Request.Builder().header("user-agent", "de.kittybot");

	public void translateText(String text, Language from, Language to, Consumer<String> callback){
		this.requestBuilder.url(String.format(API.GOOGLE_TRANSLATE_API.getUrl(), from.getShortname(), to.getShortname(), URLEncoder.encode(text, StandardCharsets.UTF_8)));
		executeAsync(this.requestBuilder.build(), (call, response) -> {
			var body = response.body();
			String newText = null;
			if(body != null){
				try{
					newText = DataArray.fromJson(body.string()).getArray(0).getArray(0).getString(0);
				}
				catch(IOException e){
					LOG.error("Error while reading body", e);
				}
			}
			callback.accept(newText);
		}, (call, response) -> callback.accept(null));
	}

	public void executeAsync(Request request, BiConsumer<Call, Response> success, BiConsumer<Call, Response> error){
		executeAsync(request, null, success, error);
	}

	public void executeAsync(Request request, API api, BiConsumer<Call, Response> success, BiConsumer<Call, Response> error){
		this.modules.getHttpClient().newCall(request).enqueue(new Callback(){

			@Override
			public void onFailure(@NotNull Call call, @NotNull IOException e){
				LOG.error("There was an error while sending a request to {}", call.request().url(), e);
				if(error != null){
					error.accept(call, null);

				}
			}

			@Override
			public void onResponse(@NotNull Call call, @NotNull Response response){
				var code = response.code();
				if(code != 200){
					var stringBody = "null";
					try(var body = response.body()){
						if(body != null){
							stringBody = body.string();
						}
					}
					catch(IOException ignored){
					}
					LOG.warn("Failed to send a request to {} | code: {} | response: {}", call.request().url(), code, stringBody);
					if(error != null){
						error.accept(call, response);
					}
					response.close();
					return;
				}
				if(api != null){
					LOG.info("Successfully executed a stats update request to {} API", api.getName());
				}
				if(success != null){
					success.accept(call, response);
				}
				response.close();
			}

		});
	}

	public String getNeko(boolean nsfw, String type, String imageType){
		var url = String.format(API.PURR_BOT.getUrl(), nsfw ? "nsfw" : "sfw", type, imageType);
		this.requestBuilder.url(url);
		this.requestBuilder.method("GET", null);
		var json = DataObject.fromJson(executeRequest(this.requestBuilder.build()));
		return json.getString("link");
	}

	public String executeRequest(Request request){
		return executeRequest(request, null);
	}

	public String executeRequest(Request request, API api){
		var requestUrl = request.url();
		try(var response = this.modules.getHttpClient().newCall(request).execute()){
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
		catch(Exception e){
			LOG.error("There was an error while sending a request to {}", requestUrl, e);
		}
		return "";
	}

	public void postToHastebin(String content, Consumer<String> callback){
		this.requestBuilder.url(API.HASTEBIN.getUrl() + "/documents");
		this.requestBuilder.post(RequestBody.create(content, MediaType.parse("text/html; charset=utf-8")));
		executeAsync(this.requestBuilder.build(), (call, response) -> {
			var body = response.body();
			String key = null;
			if(body != null){
				try{
					key = DataObject.fromJson(body.string()).getString("key");
				}
				catch(IOException e){
					LOG.error("Error while reading body", e);
				}
			}
			callback.accept(key);
		}, (call, response) -> callback.accept(null));
	}

	public void updateStats(API api, int guildCount, String token){
		var requestBody = RequestBody.create(
			DataObject.empty()
				.put(api.getStatsParameter(), guildCount)
				.toString(),
			MediaType.parse("application/json; charset=utf-8")
		);
		this.requestBuilder.url(String.format(api.getUrl(), Config.BOT_ID));
		this.requestBuilder.header("Authorization", token);
		this.requestBuilder.post(requestBody);
		executeAsync(requestBuilder.build(), api);
	}

	public void retrieveUrlContent(String url, BiConsumer<Call, Response> success, BiConsumer<Call, Response> error){
		this.requestBuilder.url(url).get();
		executeAsync(this.requestBuilder.build(), success, error);
	}

	public void executeAsync(Request request, API api){
		executeAsync(request, api, null, null);
	}

}