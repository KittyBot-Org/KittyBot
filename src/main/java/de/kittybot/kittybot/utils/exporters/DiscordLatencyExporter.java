package de.kittybot.kittybot.utils.exporters;

import de.kittybot.kittybot.main.KittyBot;
import de.kittybot.kittybot.managers.PrometheusManager;
import io.prometheus.client.Gauge;

import java.util.concurrent.TimeUnit;

public class DiscordLatencyExporter{

	// ty Natan 👀 https://github.com/Mantaro/MantaroBot/blob/master/src/main/java/net/kodehawa/mantarobot/utils/exporters/DiscordLatencyExports.java

	private static final Gauge GATEWAY_PING = Gauge.build()
			.name("kittybot_gateway_ping")
			.help("Gateway latency in ms")
			.create();

	private static final Gauge REST_PING = Gauge.build()
			.name("kittybot_rest_ping")
			.help("Rest latency in ms")
			.create();

	public void register() {
		GATEWAY_PING.register();
		REST_PING.register();
	}

	public static void start(KittyBot main){
		main.getScheduler().scheduleAtFixedRate(() -> {
			System.out.println("Writing ping");
			var jda = main.getJDA();
			var ping = jda.getGatewayPing();
			if (ping >= 0) {
				GATEWAY_PING.set(ping);
			}
			jda.getRestPing().queue(REST_PING::set);
		}, 0, PrometheusManager.UPDATE_PERIOD.toMillis(), TimeUnit.MILLISECONDS);
	}

}
