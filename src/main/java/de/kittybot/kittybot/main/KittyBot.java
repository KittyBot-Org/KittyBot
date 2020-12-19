package de.kittybot.kittybot.main;

import de.kittybot.kittybot.events.OnGuildEvent;
import de.kittybot.kittybot.events.OnGuildMemberEvent;
import de.kittybot.kittybot.events.OnGuildVoiceEvent;
import de.kittybot.kittybot.exceptions.MissingConfigValuesException;
import de.kittybot.kittybot.managers.*;
import de.kittybot.kittybot.utils.Config;
import de.kittybot.kittybot.web.WebService;
import net.dv8tion.jda.api.GatewayEncoding;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import net.dv8tion.jda.internal.utils.config.ThreadingConfig;
import okhttp3.OkHttpClient;

import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class KittyBot{

	private final Config config;
	private final OkHttpClient httpClient;
	private final JDA jda;
	private final LavalinkManager lavalinkManager;
	private final PrometheusManager prometheusManager;
	private final CommandManager commandManager;
	private final DatabaseManager databaseManager;
	private final InviteManager inviteManager;
	private final StatusManager statusManager;
	private final MessageManager messageManager;
	private final BotListsManager botListManager;
	private final RequestManager requestManager;
	private final StreamNotificationManager streamNotificationManager;
	private final NotificationManager notificationManager;
	private final DashboardSessionManager dashboardSessionManager;
	private final WebService webService;
	private final ScheduledExecutorService scheduler;
	private final GuildSettingsManager guildSettingsManager;
	private final CommandResponseManager commandResponseManager;
	private final ReactiveMessageManager reactiveMessageManager;

	public KittyBot() throws IOException, MissingConfigValuesException, LoginException, InterruptedException{
		this.config = new Config("./config.json");
		this.config.checkMandatoryValues("bot_token", "default_prefix", "owner_ids", "db_host", "db_port", "db_database", "db_user", "db_password", "signing_key", "backend_port", "origin_url", "redirect_url");
		this.scheduler = Executors.newSingleThreadScheduledExecutor();
		this.prometheusManager = new PrometheusManager(this);
		this.httpClient = new OkHttpClient();
		this.lavalinkManager = new LavalinkManager(this);
		this.databaseManager = new DatabaseManager(this);
		this.commandManager = new CommandManager(this);
		this.commandResponseManager = new CommandResponseManager();
		this.reactiveMessageManager = new ReactiveMessageManager();
		this.guildSettingsManager = new GuildSettingsManager(this);
		this.inviteManager = new InviteManager();
		this.statusManager = new StatusManager(this);
		this.messageManager = new MessageManager();
		this.botListManager = new BotListsManager(this);
		this.requestManager = new RequestManager(this);
		this.dashboardSessionManager = new DashboardSessionManager(this);
		this.notificationManager = new NotificationManager(this);
		this.streamNotificationManager = new StreamNotificationManager(this);
		this.webService = new WebService(this);

		RestAction.setDefaultFailure(null);
		jda = JDABuilder.create(
				this.config.getString("bot_token"),
				GatewayIntent.GUILD_MEMBERS,
				GatewayIntent.GUILD_VOICE_STATES,
				GatewayIntent.GUILD_MESSAGES,
				GatewayIntent.GUILD_MESSAGE_REACTIONS,
				GatewayIntent.GUILD_EMOJIS,
				GatewayIntent.GUILD_INVITES
		)
				.disableCache(
						CacheFlag.MEMBER_OVERRIDES,
						CacheFlag.ACTIVITY,
						CacheFlag.CLIENT_STATUS
				)
				.setMemberCachePolicy(MemberCachePolicy.VOICE)
				.setChunkingFilter(ChunkingFilter.NONE)
				.addEventListeners(
						this.lavalinkManager.getLavalink(),
						this.commandManager,
						this.inviteManager,
						this.statusManager,
						this.messageManager,
						this.botListManager,
						this.commandResponseManager,
						this.prometheusManager,
						new OnGuildEvent(this),
						new OnGuildMemberEvent(this),
						new OnGuildVoiceEvent(this)
				)
				.setHttpClient(this.httpClient)
				.setVoiceDispatchInterceptor(this.lavalinkManager.getLavalink().getVoiceInterceptor())
				.setActivity(Activity.playing("loading..."))
				.setStatus(OnlineStatus.DO_NOT_DISTURB)
				.setEventPool(ThreadingConfig.newScheduler(2, () -> "KittyBot", "Events"), true)
				.setGatewayEncoding(GatewayEncoding.ETF)
				.setBulkDeleteSplittingEnabled(false)
				.build()
				.awaitReady();

		this.lavalinkManager.connect(jda.getSelfUser().getId());
		this.dashboardSessionManager.init(jda.getSelfUser().getIdLong());
	}

	public Config getConfig(){
		return this.config;
	}

	public OkHttpClient getHttpClient(){
		return this.httpClient;
	}

	public JDA getJDA(){
		return this.jda;
	}

	public LavalinkManager getLavalinkManager(){
		return this.lavalinkManager;
	}

	public DashboardSessionManager getDashboardSessionManager(){
		return this.dashboardSessionManager;
	}

	public CommandManager getCommandManager(){
		return this.commandManager;
	}

	public DatabaseManager getDatabaseManager(){
		return this.databaseManager;
	}

	public ScheduledExecutorService getScheduler(){
		return this.scheduler;
	}

	public MessageManager getMessageManager(){
		return this.messageManager;
	}

	public RequestManager getRequestManager(){
		return this.requestManager;
	}

	public WebService getWebService(){
		return this.webService;
	}

	public GuildSettingsManager getGuildSettingsManager(){
		return this.guildSettingsManager;
	}

	public ReactiveMessageManager getReactiveMessageManager(){
		return this.reactiveMessageManager;
	}

	public CommandResponseManager getCommandResponseManager(){
		return this.commandResponseManager;
	}

}
