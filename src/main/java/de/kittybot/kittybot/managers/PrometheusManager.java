package de.kittybot.kittybot.managers;

import de.kittybot.kittybot.main.KittyBot;
import de.kittybot.kittybot.utils.DiscordLatencyExports;
import de.kittybot.kittybot.utils.JFRExports;
import de.kittybot.kittybot.utils.MemoryUsageExports;
import io.prometheus.client.exporter.HTTPServer;
import io.prometheus.client.hotspot.BufferPoolsExports;
import io.prometheus.client.hotspot.MemoryPoolsExports;
import io.prometheus.client.hotspot.StandardExports;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.Duration;

public class PrometheusManager{

	// ty Natan 👀 https://github.com/Mantaro/MantaroBot/blob/master/src/main/java/net/kodehawa/mantarobot/utils/Prometheus.java
	private static final Logger LOG = LoggerFactory.getLogger(PrometheusManager.class);
	public static final Duration UPDATE_PERIOD = Duration.ofSeconds(5);

	private volatile HTTPServer server;

	public PrometheusManager(KittyBot main){
		new StandardExports().register();
		new MemoryPoolsExports().register();
		new BufferPoolsExports().register();
		new DiscordLatencyExports(main).register();
		new MemoryUsageExports(main).register();
		JFRExports.register();
		try{
			this.server = new HTTPServer(8080);
		}
		catch(IOException e){
			LOG.error("Error while initializing prometheus endpoint", e);
		}
	}

}
