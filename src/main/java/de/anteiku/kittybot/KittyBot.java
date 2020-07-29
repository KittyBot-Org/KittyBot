package de.anteiku.kittybot;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.source.bandcamp.BandcampAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.http.HttpAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.twitch.TwitchStreamAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.vimeo.VimeoAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import de.anteiku.kittybot.commands.CommandManager;
import de.anteiku.kittybot.commands.commands.*;
import de.anteiku.kittybot.database.Database;
import de.anteiku.kittybot.database.SQL;
import de.anteiku.kittybot.events.*;
import de.anteiku.kittybot.objects.Cache;
import de.anteiku.kittybot.objects.Config;
import de.anteiku.kittybot.objects.LavalinkNode;
import de.anteiku.kittybot.webservice.WebService;
import lavalink.client.io.Link;
import lavalink.client.io.jda.JdaLavalink;
import net.dv8tion.jda.api.*;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import okhttp3.OkHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.net.URI;
import java.time.Instant;
import java.util.Random;

public class KittyBot{

	private static final Logger LOG = LoggerFactory.getLogger(KittyBot.class);

	public final OkHttpClient httpClient;
	public JDA jda;
	public static JdaLavalink lavalink;
	public static AudioPlayerManager audioPlayerManager;
	public static CommandManager commandManager;
	public static Random rand = new Random();

	public KittyBot(){
		LOG.info("Starting KittyBot...");
		httpClient = new OkHttpClient();

		Config.load("config.yml");

		try{
			lavalink = new JdaLavalink(Config.BOT_ID, 1, this::getShardById);
			for(LavalinkNode node : Config.LAVALINK_NODES){
				lavalink.addNode(new URI("ws://" + node.host + ":" + node.port), node.password);
			}

			audioPlayerManager = new DefaultAudioPlayerManager();
			audioPlayerManager.registerSourceManager(new YoutubeAudioSourceManager());
			audioPlayerManager.registerSourceManager(new BandcampAudioSourceManager());
			audioPlayerManager.registerSourceManager(new VimeoAudioSourceManager());
			audioPlayerManager.registerSourceManager(new TwitchStreamAudioSourceManager());
			audioPlayerManager.registerSourceManager(new HttpAudioSourceManager());
			AudioSourceManagers.registerRemoteSources(audioPlayerManager);

			jda = JDABuilder.create(
				GatewayIntent.GUILD_MEMBERS,
				GatewayIntent.GUILD_VOICE_STATES,
				GatewayIntent.GUILD_MESSAGES,
				GatewayIntent.GUILD_MESSAGE_REACTIONS,
				GatewayIntent.GUILD_EMOJIS,
				GatewayIntent.GUILD_INVITES,

				GatewayIntent.DIRECT_MESSAGES,
				GatewayIntent.DIRECT_MESSAGE_REACTIONS
			)
			.disableCache(CacheFlag.MEMBER_OVERRIDES, CacheFlag.ACTIVITY, CacheFlag.CLIENT_STATUS)
			.setToken(Config.BOT_TOKEN)
            .addEventListeners(
	            new OnGuildJoinEvent(this),
	            new OnGuildMemberEvent(this),
	            new OnEmoteEvent(this),
	            new OnGuildMessageEvent(this),
	            new OnGuildVoiceEvent(this),
	            new OnGuildReadyEvent(),
	            new OnInviteEvent(),
	            lavalink
            )
            .setVoiceDispatchInterceptor(lavalink.getVoiceInterceptor())
			.setActivity(Activity.playing("loading..."))
            .setStatus(OnlineStatus.DO_NOT_DISTURB)
            .setGatewayEncoding(GatewayEncoding.ETF)
			.build().awaitReady();

			RestAction.setDefaultFailure(null);

			Database.init(this);

			commandManager = CommandManager.build(
				new HelpCommand(this),
				new CommandsCommand(this),
				new EmoteStealCommand(this),
				new DownloadEmotesCommand(this),
				new RolesCommand(this),

				new PlayCommand(this),
				new QueueCommand(this),
				new StopCommand(this),
				new ShuffleCommand(this),
				new VolumeCommand(this),
				new PauseCommand(this),
				new SkipCommand(this),

				new PatCommand(this),
				new PokeCommand(this),
				new HugCommand(this),
				new CuddleCommand(this),
				new KissCommand(this),
				new TickleCommand(this),
				new FeedCommand(this),
				new SlapCommand(this),
				new BakaCommand(this),
				new SpankCommand(this),

				new CatCommand(this),
				new DogCommand(this),
				new NekoCommand(this),

				new OptionsCommand(this),
				new EvalCommand(this),
				new HastebinCommand(this),
				new TestCommand(this)
			);

			new WebService(this, 6969);

			jda.getPresence().setActivity(Activity.watching("you \uD83D\uDC40"));
			jda.getPresence().setStatus(OnlineStatus.ONLINE);
		}
		catch(Exception e){
			LOG.error("Error while initializing JDA", e);
			close();
		}
	}

	private JDA getShardById(int id){
		return jda;
	}

	public void sendToPublicLogChannel(JDA jda, String guildId, String channelId, String description){
		var guild = jda.getGuildById(guildId);
		if(guild == null){
			return;
		}
		guild.getTextChannelById(channelId).sendMessage(new EmbedBuilder()
			.setTitle("Log")
			.setDescription(description)
			.setThumbnail(jda.getSelfUser().getAvatarUrl())
			.setColor(new Color(76, 80, 193))
			.setFooter(jda.getSelfUser().getName(), jda.getSelfUser().getAvatarUrl())
			.setTimestamp(Instant.now())
			.build()
		).queue();
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

}
