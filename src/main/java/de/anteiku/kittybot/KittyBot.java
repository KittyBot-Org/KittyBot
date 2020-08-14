package de.anteiku.kittybot;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.source.bandcamp.BandcampAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.http.HttpAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.twitch.TwitchStreamAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.vimeo.VimeoAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import de.anteiku.kittybot.commands.info.CommandsCommand;
import de.anteiku.kittybot.commands.info.HelpCommand;
import de.anteiku.kittybot.commands.info.InfoCommand;
import de.anteiku.kittybot.commands.info.TestCommand;
import de.anteiku.kittybot.commands.music.*;
import de.anteiku.kittybot.commands.neko.*;
import de.anteiku.kittybot.commands.utilities.*;
import de.anteiku.kittybot.database.Database;
import de.anteiku.kittybot.database.SQL;
import de.anteiku.kittybot.events.*;
import de.anteiku.kittybot.objects.Config;
import de.anteiku.kittybot.objects.LavalinkNode;
import de.anteiku.kittybot.objects.command.CommandManager;
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

import javax.security.auth.login.LoginException;
import java.awt.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.Random;

public class KittyBot{

	public static final OkHttpClient httpClient = new OkHttpClient();
	private static final Logger LOG = LoggerFactory.getLogger(KittyBot.class);
	public static JdaLavalink lavalink;
	public static AudioPlayerManager audioPlayerManager;
	public static CommandManager commandManager;
	public static Random rand = new Random();
	public JDA jda;

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
				"               https://github.com/TopiSenpai/KittyBot" +
				"\n");
		LOG.info("Starting...");

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

			commandManager = CommandManager.build(
					new HelpCommand(),
					new InfoCommand(),
					new CommandsCommand(),
					new EmoteStealCommand(),
					new DownloadEmotesCommand(),
					new RolesCommand(),
					new AssignCommand(),
					new UnassignCommand(),

					new PlayCommand(),
					new QueueCommand(),
					new StopCommand(),
					new ShuffleCommand(),
					new VolumeCommand(),
					new PauseCommand(),
					new SkipCommand(),

					new PatCommand(),
					new PokeCommand(),
					new HugCommand(),
					new CuddleCommand(),
					new KissCommand(),
					new TickleCommand(),
					new FeedCommand(),
					new SlapCommand(),
					new BakaCommand(),
					new SpankCommand(),

					new CatCommand(),
					new DogCommand(),
					new NekoCommand(),

					new OptionsCommand(),
					new EvalCommand(),
					new HastebinCommand(),
					new TestCommand()
			);

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
					.setMemberCachePolicy(MemberCachePolicy.ALL)
					.setChunkingFilter(ChunkingFilter.ALL)
					.setToken(Config.BOT_TOKEN)
					.addEventListeners(
							new OnGuildJoinEvent(),
							new OnGuildMemberEvent(),
							new OnEmoteEvent(),
							new OnGuildMessageEvent(),
							new OnGuildVoiceEvent(),
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

			Database.init(jda);

			new WebService(this, 6969);

			jda.getPresence().setStatus(OnlineStatus.ONLINE);
			jda.getPresence().setActivity(Activity.watching("you \uD83D\uDC40"));
			sendToPublicLogChannel(jda, Config.SUPPORT_GUILD, Config.LOG_CHANNEL, "me online now uwu");
		}
		catch(InterruptedException | URISyntaxException | LoginException e){
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
