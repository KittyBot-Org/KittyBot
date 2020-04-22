package de.anteiku.kittybot;

import de.anteiku.kittybot.commands.*;
import de.anteiku.kittybot.commands.commands.*;
import de.anteiku.kittybot.database.Database;
import de.anteiku.kittybot.events.*;
import de.anteiku.kittybot.tasks.TaskManager;
import de.anteiku.kittybot.utils.Config;
import de.anteiku.kittybot.utils.Emotes;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import okhttp3.OkHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Random;

public class KittyBot{
	
	private static final Logger LOG = LoggerFactory.getLogger(KittyBot.class);

	public JDA jda;
	public final OkHttpClient httpClient;
	public CommandManager commandManager;
	public TaskManager taskManager;
	public Database database;
	public Random rand;
	
	public DateTimeFormatter dateFormatter;
	
	public String DISCORD_BOT_TOKEN;
	public String DISCORD_BOT_SECRET;
	public String ADMIN_DISCORD_ID;
	
	public String MYSQL_HOST;
	public String MYSQL_PORT;
	public String MYSQL_DB;
	public String MYSQL_USER;
	public String MYSQL_PASSWORD;
	
	public String DEFAULT_PREFIX = ".";
	
	public static void main(String[] args){
		new KittyBot();
	}
	
	public KittyBot(){

		httpClient = new OkHttpClient();
		setEnvVars();
		
		dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy-HH:mm");
		
		database = Database.connect(this);
		
		rand = new Random();
		try{
			jda = JDABuilder
				.create(
					GatewayIntent.GUILD_MEMBERS,
					GatewayIntent.GUILD_VOICE_STATES,
					GatewayIntent.GUILD_PRESENCES,
					GatewayIntent.GUILD_MESSAGES,
					GatewayIntent.GUILD_MESSAGE_TYPING,
					GatewayIntent.GUILD_MESSAGE_REACTIONS,
					GatewayIntent.GUILD_EMOJIS,
					
					GatewayIntent.DIRECT_MESSAGES,
					GatewayIntent.DIRECT_MESSAGE_REACTIONS,
					GatewayIntent.DIRECT_MESSAGE_TYPING
				)
				.setToken(DISCORD_BOT_TOKEN)
				.setActivity(Activity.listening("to you!"))
				.addEventListeners(
					new OnGuildJoinEvent(this),
					new OnGuildMemberJoinEvent(this),
					new OnGuildMemberRemoveEvent(this),
					new OnGuildMemberUpdateBoostTimeEvent(this),
					new OnGuildMessageReactionAddEvent(this),
					new OnGuildMessageReceivedEvent(this),
					new OnGuildVoiceEvent(this)
				)
				.build()
				.awaitReady();

			database.init();

			commandManager = new CommandManager(this)
				.addCommands(
					new HelpCommand(this),
					new CommandsCommand(this),
					new EmoteStealCommand(this),
					new RolesCommand(this),
					
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
			
			sendDMToOwnerAdmin(jda.getSelfUser().getName(), "Hellowo I'm ready!");
		}
		catch(Exception e){
			LOG.error("Error while initializing JDA", e);
			close();
		}
	}
	
	public void sendDMToOwnerAdmin(String title, String description){
		jda.openPrivateChannelById(ADMIN_DISCORD_ID).queue(
			privateChannel -> privateChannel.sendMessage(
				new EmbedBuilder()
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
	
	private void setEnvVars() {
		Config cfg = new Config("config.env");
		if(cfg.exists()){
			LOG.debug("Loading env vars from file...");
			DISCORD_BOT_TOKEN = cfg.get("DISCORD_BOT_TOKEN");
			DISCORD_BOT_SECRET = cfg.get("DISCORD_BOT_SECRET");
			ADMIN_DISCORD_ID =cfg.get("ADMIN_DISCORD_ID");
			MYSQL_HOST = cfg.get("MYSQL_HOST");
			MYSQL_PORT = cfg.get("MYSQL_PORT");
			MYSQL_DB = cfg.get("MYSQL_DATABASE");
			MYSQL_USER = cfg.get("MYSQL_USER");
			MYSQL_PASSWORD = cfg.get("MYSQL_PASSWORD");
		}
		else {
			LOG.debug("Loading env vars from system...");
			DISCORD_BOT_TOKEN = System.getenv("DISCORD_BOT_TOKEN");
			DISCORD_BOT_SECRET = System.getenv("DISCORD_BOT_SECRET");
			ADMIN_DISCORD_ID = System.getenv("ADMIN_DISCORD_ID");
			MYSQL_HOST = System.getenv("MYSQL_HOST");
			MYSQL_PORT = System.getenv("MYSQL_PORT");
			MYSQL_DB = System.getenv("MYSQL_DATABASE");
			MYSQL_USER = System.getenv("MYSQL_USER");
			MYSQL_PASSWORD = System.getenv("MYSQL_PASSWORD");
		}
	}
	
	public void close() {
		database.close();
		System.exit(0);
	}
	
}
