package de.kittybot.kittybot.objects;

import de.kittybot.kittybot.KittyBot;
import net.dv8tion.jda.api.utils.data.DataObject;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class BotLists{

	private static final Logger LOG = LoggerFactory.getLogger(BotLists.class);

	private BotLists(){}

	public static void update(int totalGuilds){
		if(Config.isSet(Config.DISCORD_BOTS_TOKEN)){
			Request request = new Request.Builder().url("https://discord.bots.gg/api/v1/bots/" + Config.BOT_ID + "/stats")
					.header("authorization", Config.DISCORD_BOTS_TOKEN)
					.post(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), DataObject.empty()
							.put("guildCount", totalGuilds)
							.toString()))
					.build();
			KittyBot.getHttpClient().newCall(request).enqueue(new Callback(){
				@Override
				public void onFailure(@NotNull Call call, @NotNull IOException e){
					LOG.error("Error while publishing bot stats to https://discord.bots.gg/", e);
				}

				@Override
				public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException{
					if(response.code() == 200){
						LOG.info("Published serverCount to https://discord.bots.gg/");
					}
					else{
						LOG.error("Error while publishing bot stats to https://discord.bots.gg - code: '{}' body: '{}'", response.code(), response.body() == null ? "" : response.body().string());
					}
					response.close();
				}
			});
		}
		if(Config.isSet(Config.DISCORD_BOT_LIST_TOKEN)){
			KittyBot.getDiscordBotListAPI().setStats(totalGuilds);
			LOG.info("Published serverCount to https://top.gg/");
		}
	}

}
