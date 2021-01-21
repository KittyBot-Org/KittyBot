package de.kittybot.kittybot.modules;

import de.kittybot.kittybot.objects.module.Module;
import de.kittybot.kittybot.utils.Config;
import de.kittybot.kittybot.utils.Utils;
import de.kittybot.kittybot.utils.exporters.DiscordPingExporter;
import de.kittybot.kittybot.utils.exporters.MemoryUsageExporter;
import de.kittybot.kittybot.utils.exporters.Metrics;
import io.prometheus.client.exporter.HTTPServer;
import io.prometheus.client.hotspot.BufferPoolsExports;
import io.prometheus.client.hotspot.MemoryPoolsExports;
import io.prometheus.client.hotspot.StandardExports;
import lavalink.client.io.metrics.LavalinkCollector;
import net.dv8tion.jda.api.events.DisconnectEvent;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.ResumedEvent;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.events.http.HttpRequestEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.time.Duration;

public class PrometheusModule extends Module{

	public static final Duration UPDATE_PERIOD = Duration.ofSeconds(5);
	// ty Natan 👀 https://github.com/Mantaro/MantaroBot/blob/master/src/main/java/net/kodehawa/mantarobot/utils/Prometheus.java
	private static final Logger LOG = LoggerFactory.getLogger(PrometheusModule.class);

	@Override
	public void onEnable(){
		if(Config.PROMETHEUS_PORT == -1){
			return;
		}
		new StandardExports().register();
		new MemoryPoolsExports().register();
		new BufferPoolsExports().register();
		new DiscordPingExporter().register(modules);
		new MemoryUsageExporter().register(modules);
		new LavalinkCollector(modules.get(LavalinkModule.class).getLavalink()).register();
		try{
			new HTTPServer(Config.PROMETHEUS_PORT);
		}
		catch(IOException e){
			LOG.error("Error while initializing prometheus endpoint", e);
		}
	}

	@Override
	public void onReady(@Nonnull ReadyEvent event){
		Metrics.GUILD_COUNT.set(event.getJDA().getGuildCache().size());
		Metrics.USER_COUNT.set(Utils.getUserCount(this.modules.getShardManager()));
	}

	@Override
	public void onResumed(@Nonnull ResumedEvent event){
		Metrics.BOT_EVENTS.labels("resumed").inc();
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
		Metrics.USER_COUNT.set(Utils.getUserCount(this.modules.getShardManager()));
		Metrics.GUILD_ACTIONS.labels("join").inc();
	}

	@Override
	public void onGuildLeave(@Nonnull GuildLeaveEvent event){
		Metrics.GUILD_COUNT.set(event.getJDA().getGuildCache().size());
		Metrics.USER_COUNT.set(Utils.getUserCount(this.modules.getShardManager()));
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
