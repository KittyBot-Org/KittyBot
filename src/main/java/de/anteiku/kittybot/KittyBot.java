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
import de.anteiku.kittybot.events.*;
import de.anteiku.kittybot.objects.LavalinkNode;
import de.anteiku.kittybot.tasks.TaskManager;
import de.anteiku.kittybot.utils.Config;
import de.anteiku.kittybot.webservice.WebService;
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
	public WebService webService;
	
	public AudioPlayerManager playerManager;
	public CommandManager commandManager;
	public TaskManager taskManager;
	public Database database;
	public Random rand;
	
	public static void main(String[] args){
		new KittyBot();
	}
	
	private JDA getShardById(int id){
		return jda;
	}
	
	public KittyBot(){
		LOG.info("Starting KittyBot...");
		httpClient = new OkHttpClient();
		
		Config.load("config.yml");
		
		database = Database.connect(this);
		rand = new Random();
		
		try{
			lavalink = new JdaLavalink(Config.DISCORD_BOT_ID, 1, this::getShardById);
			for(LavalinkNode node : Config.LAVALINK_NODES){
				lavalink.addNode(new URI("ws://" + node.host + ":" + node.port), node.password);
			}
			
			playerManager = new DefaultAudioPlayerManager();
			playerManager.registerSourceManager(new YoutubeAudioSourceManager());
			playerManager.registerSourceManager(new BandcampAudioSourceManager());
			playerManager.registerSourceManager(new VimeoAudioSourceManager());
			playerManager.registerSourceManager(new TwitchStreamAudioSourceManager());
			playerManager.registerSourceManager(new HttpAudioSourceManager());
			
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
	        .setToken(Config.DISCORD_BOT_TOKEN)
	        .setActivity(Activity.listening("you!"))
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
					new EmoteCommand(this),
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
					new SpankCommand(this),

					new CatCommand(this),
					new DogCommand(this),
					new NekoCommand(this),
					
					new OptionsCommand(this),
					new EvalCommand(this),
					new HastebinCommand(this),
					new TestCommand(this)
				);
			
			webService = new WebService(this, 6969);
			
			taskManager = new TaskManager(this);
			
			sendDMToOwnerAdmin(jda, jda.getSelfUser().getName(), "Hellowo I'm ready!");
		}
		catch(Exception e){
			LOG.error("Error while initializing JDA", e);
			close();
		}
	}
	
	public void sendDMToOwnerAdmin(JDA jda, String title, String description){
		jda.openPrivateChannelById(Config.DISCORD_ADMIN_ID).queue(
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
