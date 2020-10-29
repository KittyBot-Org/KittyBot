package de.kittybot.kittybot;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.sedmelluq.discord.lavaplayer.jdaudp.NativeAudioSendFactory;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.source.bandcamp.BandcampAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.http.HttpAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.twitch.TwitchStreamAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.vimeo.VimeoAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import de.kittybot.kittybot.cache.DashboardSessionCache;
import de.kittybot.kittybot.cache.MessageCache;
import de.kittybot.kittybot.database.Database;
import de.kittybot.kittybot.database.SQL;
import de.kittybot.kittybot.events.*;
import de.kittybot.kittybot.objects.Config;
import de.kittybot.kittybot.objects.StatusManager;
import de.kittybot.kittybot.objects.command.CommandManager;
import de.kittybot.kittybot.objects.requests.Requester;
import de.kittybot.kittybot.utils.Utils;
import lavalink.client.io.Link;
import lavalink.client.io.jda.JdaLavalink;
import net.dv8tion.jda.api.*;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import net.dv8tion.jda.internal.utils.config.ThreadingConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.net.URI;
import java.time.Instant;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class KittyBot{

	private static final Logger LOG = LoggerFactory.getLogger(KittyBot.class);
	private static final AudioPlayerManager AUDIO_PLAYER_MANAGER = new DefaultAudioPlayerManager();
	private static final ScheduledExecutorService SCHEDULER = Executors.newSingleThreadScheduledExecutor();
	private static final EventWaiter WAITER = new EventWaiter();
	private static JdaLavalink lavalink;
	private static JDA jda;

	public KittyBot(){
		LOG.info("\n" +
				"\n" +
				"         _   ___ _   _        ______       _   \n" +
				"        | | / (_) | | |       | ___ \\     | |  \n" +
				"        | |/ / _| |_| |_ _   _| |_/ / ___ | |_ \n" +
				"        |    \\| | __| __| | | | ___ \\/ _ \\| __|\n" +
				"        | |\\  \\ | |_| |_| |_| | |_/ / (_) | |_ \n" +
				"        \\_| \\_/_|\\__|\\__|\\__, \\____/ \\___/ \\__|\n" +
				"                          __/ |                \n" +
				"                         |___/                 \n" +
				"\n" +
				"            https://github.com/KittyBot-Org/KittyBot" +
				"\n");
		LOG.info("Starting...");

		try{
			lavalink = new JdaLavalink(Config.BOT_ID, 1, this::fuckLavalink);
			for(var node : Config.LAVALINK_NODES){
				lavalink.addNode(new URI("ws://" + node.host + ":" + node.port), node.password);
			}

			AUDIO_PLAYER_MANAGER.registerSourceManager(new YoutubeAudioSourceManager());
			AUDIO_PLAYER_MANAGER.registerSourceManager(new BandcampAudioSourceManager());
			AUDIO_PLAYER_MANAGER.registerSourceManager(new VimeoAudioSourceManager());
			AUDIO_PLAYER_MANAGER.registerSourceManager(new TwitchStreamAudioSourceManager());
			AUDIO_PLAYER_MANAGER.registerSourceManager(new HttpAudioSourceManager());
			AudioSourceManagers.registerRemoteSources(AUDIO_PLAYER_MANAGER);

			CommandManager.registerCommands();

			RestAction.setDefaultFailure(null);

			jda = JDABuilder.create(
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
							CacheFlag.CLIENT_STATUS
					)
					.setMemberCachePolicy(MemberCachePolicy.DEFAULT.or(member -> DashboardSessionCache.hasSession(member.getId()))) // voice, owner or a user with a web session
					.setChunkingFilter(ChunkingFilter.NONE)                                                                         // lazy loading
					.addEventListeners(
							new OnEmoteEvent(),
							new OnGuildEvent(),
							new OnGuildMemberEvent(),
							new OnGuildMessageEvent(),
							new OnGuildReadyEvent(),
							new OnGuildVoiceEvent(),
							new OnInviteEvent(),
							lavalink
					)
					.setVoiceDispatchInterceptor(lavalink.getVoiceInterceptor())
					.setActivity(Activity.playing("loading.."))
					.setStatus(OnlineStatus.DO_NOT_DISTURB)
					.setEventPool(ThreadingConfig.newScheduler(2, () -> "KittyBot", "Events"), true)
					.setHttpClient(Requester.getHttpClient())
					.setGatewayEncoding(GatewayEncoding.ETF)
					.setAudioSendFactory(new NativeAudioSendFactory())
					.setBulkDeleteSplittingEnabled(false)
					.build()
					.awaitReady();

			Utils.updateStats((int) jda.getGuildCache().size());

			Database.init(jda);

			new WebService(Config.BACKEND_PORT);

			SCHEDULER.scheduleAtFixedRate(MessageCache::pruneCache, 1, 1, TimeUnit.HOURS);
			SCHEDULER.scheduleAtFixedRate(StatusManager::newRandomStatus, 0, 2, TimeUnit.MINUTES);

			if(Config.isSet(Config.LOG_CHANNEL_ID)){
				sendToPublicLogChannel("I'm now online uwu");
			}
		}
		catch(Exception e){
			LOG.error("Error while initializing JDA", e);
			close();
		}
	}

	private JDA fuckLavalink(int id){ // TODO maybe get rid of this fucking shit in our fork
		return jda;
	}

	public static void sendToPublicLogChannel(String description){
		var guild = jda.getGuildById(Config.SUPPORT_GUILD_ID);
		if(guild == null){
			return;
		}
		var channel = guild.getTextChannelById(Config.LOG_CHANNEL_ID);
		if(channel != null){
			channel.sendMessage(new EmbedBuilder()
					.setDescription(description)
					.setColor(new Color(76, 80, 193))
					.setFooter(jda.getSelfUser().getName(), jda.getSelfUser().getAvatarUrl())
					.setTimestamp(Instant.now())
					.build()).queue();
		}
	}

	public void close(){
		lavalink.getLinks().forEach(Link::destroy);
		jda.shutdown();
		SQL.close();
		System.exit(0);
	}

	public static void main(String[] args){
		new KittyBot();
	}

	public static AudioPlayerManager getAudioPlayerManager(){
		return AUDIO_PLAYER_MANAGER;
	}

	public static JdaLavalink getLavalink(){
		return lavalink;
	}

	public static JDA getJda(){
		return jda;
	}

	public static ScheduledExecutorService getScheduler(){
		return SCHEDULER;
	}

	public static EventWaiter getWaiter(){
		return WAITER;
	}

}
