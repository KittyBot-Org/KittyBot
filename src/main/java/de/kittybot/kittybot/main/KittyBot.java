package de.kittybot.kittybot.main;

import de.kittybot.kittybot.exceptions.MissingConfigValuesException;
import de.kittybot.kittybot.module.Modules;
import de.kittybot.kittybot.modules.LavalinkModule;
import de.kittybot.kittybot.utils.Config;
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

import javax.security.auth.login.LoginException;
import java.io.IOException;

public class KittyBot{

	private final ShardManager shardManager;

	public KittyBot() throws IOException, MissingConfigValuesException, LoginException, InterruptedException{
		Config.init("./config.json");
		var modules = new Modules(this);

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
			.addEventListeners(modules.getModules())
			.setRawEventsEnabled(true)
			.setHttpClient(modules.getHttpClient())
			.setVoiceDispatchInterceptor(modules.get(LavalinkModule.class).getVoiceInterceptor())
			.setActivity(Activity.playing("loading..."))
			.setStatus(OnlineStatus.DO_NOT_DISTURB)
			.setEventPool(ThreadingConfig.newScheduler(1, () -> "KittyBot", "Events"), true)
			.setGatewayEncoding(GatewayEncoding.ETF)
			.setBulkDeleteSplittingEnabled(false)
			.build();
	}

	public ShardManager getShardManager(){
		return this.shardManager;
	}

}
