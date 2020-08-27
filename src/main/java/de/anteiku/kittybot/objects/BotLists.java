package de.anteiku.kittybot.objects;

import de.anteiku.kittybot.KittyBot;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.utils.data.DataObject;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class BotLists{

	private static final Logger LOG = LoggerFactory.getLogger(BotLists.class);

	public static void update(JDA jda, int totalGuilds){
		if(Config.isSet(Config.DISCORD_BOTS_TOKEN)){
			var shardInfo = jda.getShardInfo();
			Request request = new Request.Builder().url("https://discord.bots.gg/api/v1/bots/" + Config.BOT_ID + "/stats")
					.header("authorization", Config.DISCORD_BOTS_TOKEN)
					.post(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), DataObject.empty()
							.put("shardCount", shardInfo.getShardTotal())
							.put("shardId", shardInfo.getShardId())
							.put("guildCount", totalGuilds)
							.toString()))
					.build();
			KittyBot.getHttpClient().newCall(request).enqueue(new Callback(){
				@Override public void onFailure(@NotNull Call call, @NotNull IOException e){
					LOG.error("Error while publishing bot stats to https://discord.bots.gg/", e);
				}

				@Override public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException{
					if(response.code() == 200){
						LOG.info("Published serverCount to https://discord.bots.gg/");
					}
					else{
						LOG.error("Error while publishing bot stats to https://discord.bots.gg/ code: '" + response.code() + "'" + (response.body() == null ? "" : "body: '" + response
								.body()
								.string() + "'"));
					}
					response.close();
				}
			});
		}
		if(Config.isSet(Config.DISCORD_BOT_LIST_TOKEN)){
			var shardInfo = jda.getShardInfo();
			KittyBot.getDiscordBotListAPI().setStats(shardInfo.getShardId(), shardInfo.getShardTotal(), totalGuilds);
			LOG.info("Published serverCount to https://top.gg/");
		}
	}

}
