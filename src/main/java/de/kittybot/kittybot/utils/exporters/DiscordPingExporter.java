package de.kittybot.kittybot.utils.exporters;

import de.kittybot.kittybot.modules.PrometheusModule;
import de.kittybot.kittybot.objects.module.Modules;
import io.prometheus.client.Gauge;

import java.util.concurrent.TimeUnit;

public class DiscordPingExporter{

	// ty Natan 👀 https://github.com/Mantaro/MantaroBot/blob/master/src/main/java/net/kodehawa/mantarobot/utils/exporters/DiscordLatencyExports.java

	private static final Gauge GATEWAY_PING = Gauge.build()
		.name("kittybot_gateway_ping")
		.help("Gateway ping per shard in ms")
		.labelNames("shard")
		.create();

	private static final Gauge REST_PING = Gauge.build()
		.name("kittybot_rest_ping")
		.help("Rest latency in ms")
		.create();

	public void register(Modules modules){
		GATEWAY_PING.register();
		REST_PING.register();
		modules.getScheduler().scheduleAtFixedRate(() -> {
			var shardManager = modules.getShardManager();
			if(shardManager == null){
				return;
			}
			var shards = shardManager.getShardCache();
			shards.forEach(shard -> {
				var ping = shard.getGatewayPing();

				if(ping >= 0){
					GATEWAY_PING.labels(String.valueOf(shard.getShardInfo().getShardId())).set(ping);
				}
			});
			if(shards.size() > 0){
				shards.iterator().next().getRestPing().queue(REST_PING::set);
			}
		}, 0, PrometheusModule.UPDATE_PERIOD.toMillis(), TimeUnit.MILLISECONDS);
	}

}
