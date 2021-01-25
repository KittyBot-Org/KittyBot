package de.kittybot.kittybot.utils.exporters;

import io.prometheus.client.Counter;
import io.prometheus.client.Gauge;
import io.prometheus.client.Histogram;

public class Metrics{

	public static final Counter TRACK_EVENTS = Counter.build()
		.name("kittybot_track_event")
		.help("Music Track Events (failed/loaded/searched)")
		.labelNames("type")
		.register();

	public static final Gauge COMMAND_LATENCY = Gauge.build()
		.name("kittybot_command_latency")
		.help("Time it takes for a command to process.")
		.labelNames("command")
		.register();

	public static final Counter COMMAND_COUNTER = Counter.build()
		.name("kittybot_commands")
		.help("Amounts of commands ran by name")
		.labelNames("name")
		.register();

	public static final Gauge GUILD_COUNT = Gauge.build()
		.name("kittybot_guilds")
		.help("Guild Count")
		.register();

	public static final Gauge USER_COUNT = Gauge.build()
		.name("kittybot_users")
		.help("User Count")
		.register();

	public static final Counter HTTP_REQUESTS = Counter.build()
		.name("kittybot_http_requests")
		.help("Successful HTTP Requests (JDA)")
		.register();

	public static final Counter HTTP_429_REQUESTS = Counter.build()
		.name("kittybot_http_ratelimit_requests")
		.help("429 HTTP Requests (JDA)")
		.register();

	public static final Counter RECEIVED_MESSAGES = Counter.build()
		.name("kittybot_messages_received")
		.help("Received messages (all users + bots)")
		.register();

	public static final Counter BOT_EVENTS = Counter.build()
		.name("kittybot_events")
		.help("Bot Events")
		.labelNames("type")
		.register();

	public static final Counter ACTIONS = Counter.build()
		.name("kittybot_actions")
		.help("KittyBot Actions")
		.labelNames("type")
		.register();

	public static final Counter DASHBOARD_ACTIONS = Counter.build()
		.name("kittybot_dashboard_actions")
		.help("KittyBot Dashboard Actions")
		.labelNames("type")
		.register();

	public static final Counter GUILD_ACTIONS = Counter.build()
		.name("kittybot_guild_actions")
		.help("Guild Options")
		.labelNames("type")
		.register();

	private Metrics(){}

}

