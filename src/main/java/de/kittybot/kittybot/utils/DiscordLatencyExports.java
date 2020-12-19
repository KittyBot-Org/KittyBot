package de.kittybot.kittybot.utils;

import de.kittybot.kittybot.main.KittyBot;
import de.kittybot.kittybot.managers.PrometheusManager;
import io.prometheus.client.Gauge;

import java.util.concurrent.TimeUnit;

public class DiscordLatencyExports{

	// ty Natan 👀 https://github.com/Mantaro/MantaroBot/blob/master/src/main/java/net/kodehawa/mantarobot/utils/exporters/DiscordLatencyExports.java
	private static final double MILLISECONDS_PER_SECOND = 1000;

	private static final Gauge GATEWAY_LATENCY = Gauge.build()
			.name("mantaro_bot_latency")
			.help("Gateway latency in seconds, per shard")
			.labelNames("bot")
			.create();
	private static final Gauge REST_LATENCY = Gauge.build()
			.name("mantaro_rest_latency")
			.help("Rest latency in seconds")
			.create();

	private final KittyBot main;

	public DiscordLatencyExports(KittyBot main){
		this.main = main;
	}

	public void register() {
		GATEWAY_LATENCY.register();
		REST_LATENCY.register();

		this.main.getScheduler().scheduleAtFixedRate(() -> {
			var jda = this.main.getJDA();
			var ping = jda.getGatewayPing();
			if (ping >= 0) {
				GATEWAY_LATENCY.labels("bot").set(ping / MILLISECONDS_PER_SECOND);
			}
			jda.getRestPing().queue(restPing -> REST_LATENCY.set(restPing / MILLISECONDS_PER_SECOND));
		}, 0, PrometheusManager.UPDATE_PERIOD.toMillis(), TimeUnit.MILLISECONDS);
	}
}
