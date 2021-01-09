package de.kittybot.kittybot.main;

import de.kittybot.kittybot.exceptions.MissingConfigValuesException;
import de.kittybot.kittybot.module.Modules;
import de.kittybot.kittybot.modules.LavalinkModule;
import de.kittybot.kittybot.utils.Config;
import de.kittybot.kittybot.utils.ThreadFactoryHelper;
import net.dv8tion.jda.api.GatewayEncoding;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import net.dv8tion.jda.internal.utils.config.ThreadingConfig;
import okhttp3.OkHttpClient;

import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

public class KittyBot{

	private final OkHttpClient httpClient;
	private final ShardManager shardManager;
	private final ScheduledExecutorService scheduler;
	private final Modules modules;

	public KittyBot() throws IOException, MissingConfigValuesException, LoginException, InterruptedException{
		Config.init("./config.json");
		this.scheduler = new ScheduledThreadPoolExecutor(2, new ThreadFactoryHelper());
		this.httpClient = new OkHttpClient();
		this.modules = new Modules(this);

		RestAction.setDefaultFailure(null);
		this.shardManager = DefaultShardManagerBuilder.create(
				Config.BOT_TOKEN,
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
						CacheFlag.CLIENT_STATUS,
						CacheFlag.ROLE_TAGS
				)
				.setMemberCachePolicy(MemberCachePolicy.VOICE)
				.setChunkingFilter(ChunkingFilter.NONE)
				.addEventListeners(this.modules.getModules())
				.setHttpClient(this.httpClient)
				.setVoiceDispatchInterceptor(this.modules.get(LavalinkModule.class).getVoiceInterceptor())
				.setActivity(Activity.playing("loading..."))
				.setStatus(OnlineStatus.DO_NOT_DISTURB)
				.setEventPool(ThreadingConfig.newScheduler(1, () -> "KittyBot", "Events"), true)
				.setGatewayEncoding(GatewayEncoding.ETF)
				.setBulkDeleteSplittingEnabled(false)
				.setShardsTotal(-1)
				.build();
	}

	public OkHttpClient getHttpClient(){
		return this.httpClient;
	}

	public ShardManager getShardManager(){
		return this.shardManager;
	}

	public ScheduledExecutorService getScheduler(){
		return this.scheduler;
	}

}
