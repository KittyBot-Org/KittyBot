package de.kittybot.kittybot.managers;

import de.kittybot.kittybot.main.KittyBot;
import de.kittybot.kittybot.utils.Utils;
import de.kittybot.kittybot.utils.exporters.DiscordLatencyExporter;
import de.kittybot.kittybot.utils.exporters.MemoryUsageExporter;
import de.kittybot.kittybot.utils.exporters.Metrics;
import io.prometheus.client.exporter.HTTPServer;
import io.prometheus.client.hotspot.BufferPoolsExports;
import io.prometheus.client.hotspot.MemoryPoolsExports;
import io.prometheus.client.hotspot.StandardExports;
import net.dv8tion.jda.api.events.DisconnectEvent;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.ResumedEvent;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.events.http.HttpRequestEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.time.Duration;

public class PrometheusManager extends ListenerAdapter{

	public static final Duration UPDATE_PERIOD = Duration.ofSeconds(5);
	// ty Natan 👀 https://github.com/Mantaro/MantaroBot/blob/master/src/main/java/net/kodehawa/mantarobot/utils/Prometheus.java
	private static final Logger LOG = LoggerFactory.getLogger(PrometheusManager.class);
	private final KittyBot main;
	private volatile HTTPServer server;

	public PrometheusManager(KittyBot main){
		this.main = main;
		new StandardExports().register();
		new MemoryPoolsExports().register();
		new BufferPoolsExports().register();
		new DiscordLatencyExporter().register();
		new MemoryUsageExporter(main).register();
		try{
			this.server = new HTTPServer(main.getConfig().getInt("prometheus_port"));
		}
		catch(IOException e){
			LOG.error("Error while initializing prometheus endpoint", e);
		}
	}

	@Override
	public void onReady(@Nonnull ReadyEvent event){
		Metrics.GUILD_COUNT.set(event.getJDA().getGuildCache().size());
		Metrics.USER_COUNT.set(Utils.getUserCount(event.getJDA()));
		DiscordLatencyExporter.start(this.main);
	}

	@Override
	public void onResume(@Nonnull ResumedEvent event){
		Metrics.BOT_EVENTS.labels("resume").inc();
	}

	@Override
	public void onDisconnect(@Nonnull DisconnectEvent event){
		Metrics.BOT_EVENTS.labels("disconnect").inc();
	}

	@Override
	public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event){
		Metrics.RECEIVED_MESSAGES.inc();
	}

	@Override
	public void onGuildJoin(@Nonnull GuildJoinEvent event){
		Metrics.GUILD_COUNT.set(event.getJDA().getGuildCache().size());
		Metrics.USER_COUNT.set(Utils.getUserCount(event.getJDA()));
		Metrics.GUILD_ACTIONS.labels("join").inc();
	}

	@Override
	public void onGuildLeave(@Nonnull GuildLeaveEvent event){
		Metrics.GUILD_COUNT.set(event.getJDA().getGuildCache().size());
		Metrics.USER_COUNT.set(Utils.getUserCount(event.getJDA()));
		Metrics.GUILD_ACTIONS.labels("leave").inc();
	}

	@Override
	public void onHttpRequest(@NotNull HttpRequestEvent event){
		if(event.isRateLimit()){
			LOG.error("Reached 429 on: {}", event.getRoute());
			Metrics.HTTP_429_REQUESTS.inc();
		}
		Metrics.HTTP_REQUESTS.inc();
	}

}
