package de.anteiku.kittybot;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.source.bandcamp.BandcampAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.http.HttpAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.local.LocalAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.soundcloud.SoundCloudAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.twitch.TwitchStreamAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.vimeo.VimeoAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import de.anteiku.kittybot.commands.CommandManager;
import de.anteiku.kittybot.commands.commands.*;
import de.anteiku.kittybot.database.Database;
import de.anteiku.kittybot.events.*;
import de.anteiku.kittybot.tasks.TaskManager;
import de.anteiku.kittybot.utils.Config;
import lavalink.client.io.jda.JdaLavalink;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
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
	public JdaLavalink lavalink;
	
	public AudioPlayerManager playerManager;
	
	public CommandManager commandManager;
	public TaskManager taskManager;
	public Database database;
	public Random rand;
	
	public String DISCORD_BOT_TOKEN;
	public String DISCORD_BOT_SECRET;
	public String DISCORD_BOT_ID;
	public String ADMIN_DISCORD_ID;
	
	public String DB_HOST;
	public String DB_PORT;
	public String DB_DB;
	public String DB_USER;
	public String DB_PASSWORD;
	
	public String LAVALINK_HOST;
	public String LAVALINK_PORT;
	public String LAVALINK_PASSWORD;
	
	public String DEFAULT_PREFIX = ".";
	
	public static void main(String[] args){
		new KittyBot();
	}
	
	private JDA getShardById(int id){
		return jda;
	}
	
	public KittyBot(){
		LOG.info("Starting KittyBot...");
		httpClient = new OkHttpClient();
		setEnvVars();
		
		database = Database.connect(this);
		rand = new Random();
		
		try{
			lavalink = new JdaLavalink(DISCORD_BOT_ID, 1, this::getShardById);
			lavalink.addNode(new URI("ws://" + LAVALINK_HOST + ":" + LAVALINK_PORT), LAVALINK_PASSWORD);
			
			playerManager = new DefaultAudioPlayerManager();
			playerManager.registerSourceManager(new YoutubeAudioSourceManager());
			playerManager.registerSourceManager(new SoundCloudAudioSourceManager());
			playerManager.registerSourceManager(new BandcampAudioSourceManager());
			playerManager.registerSourceManager(new VimeoAudioSourceManager());
			playerManager.registerSourceManager(new TwitchStreamAudioSourceManager());
			playerManager.registerSourceManager(new HttpAudioSourceManager());
			playerManager.registerSourceManager(new LocalAudioSourceManager());
			
			AudioSourceManagers.registerRemoteSources(playerManager);
			
			jda = JDABuilder.create(
				GatewayIntent.GUILD_MEMBERS,
				GatewayIntent.GUILD_VOICE_STATES,
				GatewayIntent.GUILD_MESSAGES,
				GatewayIntent.GUILD_MESSAGE_REACTIONS,
				GatewayIntent.GUILD_EMOJIS,
				
				GatewayIntent.DIRECT_MESSAGES,
				GatewayIntent.DIRECT_MESSAGE_REACTIONS
			)
	        .disableCache(CacheFlag.ACTIVITY, CacheFlag.CLIENT_STATUS)
	        .setToken(DISCORD_BOT_TOKEN)
	        .setActivity(Activity.listening("to you!"))
			.addEventListeners(
			    new OnGuildJoinEvent(this),
				new OnGuildMemberJoinEvent(this),
				new OnGuildMemberRemoveEvent(this),
				new OnGuildMemberUpdateBoostTimeEvent(this),
				new OnGuildMessageReactionAddEvent(this),
				new OnGuildMessageReceivedEvent(this),
				new OnGuildVoiceEvent(this),
				lavalink
			)
	        .setVoiceDispatchInterceptor(lavalink.getVoiceInterceptor())
	        .build().awaitReady();
			
			database.init();
			
			commandManager = new CommandManager(this)
				.addCommands(
					new HelpCommand(this),
					new CommandsCommand(this),
					new EmoteStealCommand(this),
					new RolesCommand(this),
					
					new PlayCommand(this),
					new QueueCommand(this),
					new StopCommand(this),
//                  new PlayingCommand(this),
//                  new ShuffleCommand(this),
//					new RepeatCommand(this),
//					new VolumeCommand(this),
//					new PauseCommand(this),
//					new ResumeCommand(this),
					
					new PatCommand(this),
					new PokeCommand(this),
					new HugCommand(this),
					new CuddleCommand(this),
					new KissCommand(this),
					new TickleCommand(this),
					new FeedCommand(this),
					new SlapCommand(this),
					new BakaCommand(this),
					
					new CatCommand(this),
					new DogCommand(this),
					new NekoCommand(this),
					
					new OptionsCommand(this),
					new EvalCommand(this),
					new TestCommand(this)
				);
			
			taskManager = new TaskManager(this);
			
			sendDMToOwnerAdmin(jda, jda.getSelfUser().getName(), "Hellowo I'm ready!");
		}
		catch(Exception e){
			LOG.error("Error while initializing JDA", e);
			close();
		}
	}
	
	private void setEnvVars(){
		Config cfg = new Config("config.env");
		if(cfg.exists()){
			LOG.debug("Loading env vars from file...");
		}
		else{
			LOG.debug("Loading env vars from system...");
		}
		DISCORD_BOT_TOKEN = loadEvnVar(cfg, "DISCORD_BOT_TOKEN");
		DISCORD_BOT_ID = loadEvnVar(cfg, "DISCORD_BOT_ID");
		DISCORD_BOT_SECRET = loadEvnVar(cfg, "DISCORD_BOT_SECRET");
		ADMIN_DISCORD_ID = loadEvnVar(cfg, "ADMIN_DISCORD_ID");
		
		DB_HOST = loadEvnVar(cfg, "POSTGRES_HOST");
		DB_PORT = loadEvnVar(cfg, "POSTGRES_PORT");
		DB_DB = loadEvnVar(cfg, "POSTGRES_DB");
		DB_USER = loadEvnVar(cfg, "POSTGRES_USER");
		DB_PASSWORD = loadEvnVar(cfg, "POSTGRES_PASSWORD");
		
		LAVALINK_HOST = loadEvnVar(cfg, "LAVALINK_HOST");
		LAVALINK_PORT = loadEvnVar(cfg, "LAVALINK_PORT");
		LAVALINK_PASSWORD = loadEvnVar(cfg, "LAVALINK_PASSWORD");
	}
	
	private String loadEvnVar(Config cfg, String var) {
		if(cfg.exists()){
			return cfg.get(var);
		}
		return System.getenv(var);
	}
	
	public void sendDMToOwnerAdmin(JDA jda, String title, String description){
		jda.openPrivateChannelById(ADMIN_DISCORD_ID).queue(
			privateChannel -> privateChannel.sendMessage(new EmbedBuilder()
				.setTitle(title)
				.setDescription(description)
				.setThumbnail(jda.getSelfUser().getAvatarUrl())
				.setColor(new Color(76, 80, 193))
				.setFooter(jda.getSelfUser().getName(), jda.getSelfUser().getAvatarUrl())
				.setTimestamp(Instant.now())
				.build()
			).queue()
		);
	}
	
	public void close(){
		database.close();
		System.exit(0);
	}
	
}
