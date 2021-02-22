package de.kittybot.kittybot.main;

import de.kittybot.kittybot.modules.LavalinkModule;
import de.kittybot.kittybot.objects.enums.Environment;
import de.kittybot.kittybot.objects.exceptions.MissingConfigValuesException;
import de.kittybot.kittybot.objects.module.Modules;
import de.kittybot.kittybot.utils.Config;
import dev.mlnr.blh.api.BLHBuilder;
import dev.mlnr.blh.api.BLHEventListener;
import dev.mlnr.blh.api.BotList;
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
		var botListHandler = new BLHBuilder()
			.setUnavailableEventsEnabled(false)
			.setDevModePredicate(jda -> Environment.is(Environment.DEVELOPMENT))
			.addBotList(BotList.TOP_GG, Config.TOP_GG_TOKEN)
			.addBotList(BotList.DBOATS, Config.DISCORD_BOATS_TOKEN)
			.addBotList(BotList.BOTLIST_SPACE, Config.BOTLIST_SPACE_TOKEN)
			.addBotList(BotList.BOTS_FOR_DISCORD, Config.BOTS_FOR_DISCORD_TOKEN)
			.addBotList(BotList.DSERVICES, Config.DISCORD_SERVICES_TOKEN)
			.addBotList(BotList.DBL, Config.DISCORD_BOT_LIST_TOKEN)
			.addBotList(BotList.DEL, Config.DISCORD_EXTREME_LIST_TOKEN)
			.addBotList(BotList.DBOTS_GG, Config.DISCORD_BOTS_TOKEN)
			.build();

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
			.addEventListeners(new BLHEventListener(botListHandler))
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
