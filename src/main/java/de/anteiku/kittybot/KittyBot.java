package de.anteiku.kittybot;

import de.anteiku.kittybot.commands.*;
import de.anteiku.kittybot.database.Database;
import de.anteiku.kittybot.events.*;
import de.anteiku.kittybot.tasks.TaskManager;
import de.anteiku.kittybot.utils.Logger;
import de.anteiku.kittybot.webservice.WebService;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import okhttp3.OkHttpClient;

import java.sql.SQLException;
import java.util.*;

public class KittyBot{
	
	public final OkHttpClient httpClient;
	
	public JDA jda;
	public Logger logger;
	public CommandManager commandManager;
	public TaskManager taskManager;
	public Database database;
	public WebService webService;
	public Random rand;
	
	public String DISCORD_BOT_TOKEN;
	public String DISCORD_BOT_SECRET;
	public String ADMIN_DISCORD_ID;
	
	public String MYSQL_HOST;
	public String MYSQL_PORT;
	public String MYSQL_DB;
	public String MYSQL_USER;
	public String MYSQL_PASSWORD;
	
	public String DEFAULT_PREFIX = ".";
	public String UNSPLASH_CLIENT_ID;
	
	public static void main(String[] args){
		new KittyBot();
	}
	
	public KittyBot(){
		setEnvVars();
		
		httpClient = new OkHttpClient();
		logger = new Logger(this);
		
		boolean connected = false;
		int tries = 0;
		while(!connected) {
			tries++;
			try{
				database = new Database(this);
				connected = true;
			}
			catch(SQLException e) {
				Logger.error(e);
				Logger.print("Could not connect to database...");
				Logger.print("Retrying in 5 seconds...");
				try {
					Thread.sleep(1000);
				} catch (InterruptedException ex) {
					Logger.error(ex);
				}
			}
			finally {
				if(tries > 20) {
					Logger.print("Too many retries...");
					Logger.print("Shutting down KittyBot...");
					System.exit(1);
				}
			}

		}
		
		rand = new Random();
		new ConsoleThread(this);
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
					new OnGuildMemberJoinEvent(this),
					new OnGuildMemberRemoveEvent(this),
					new OnGuildMemberUpdateBoostTimeEvent(this),
					new OnGuildMessageReactionAddEvent(this),
					new OnGuildMessageReceivedEvent(this),
					new OnGuildVoiceJoinEvent(this),
					new OnGuildVoiceLeaveEvent(this),
					new OnGuildVoiceMoveEvent(this)
				)
				.build()
				.awaitReady();

			database.init();

			commandManager = new CommandManager(this);
			commandManager.add(new HelpCommand(this));
			commandManager.add(new CommandsCommand(this));
			commandManager.add(new RolesCommand(this));
			commandManager.add(new QuokkaCommand(this));
			commandManager.add(new TurtleCommand(this));
			commandManager.add(new CatCommand(this));
			commandManager.add(new DogCommand(this));
			commandManager.add(new NekoCommand(this));
			commandManager.add(new PokeCommand(this));
			commandManager.add(new TickleCommand(this));
			commandManager.add(new HugCommand(this));
			commandManager.add(new FeedCommand(this));
			commandManager.add(new KissCommand(this));
			commandManager.add(new SlapCommand(this));
			commandManager.add(new PatCommand(this));
			commandManager.add(new CuddleCommand(this));
			commandManager.add(new KurapikaCommand(this));
			commandManager.add(new OptionsCommand(this));
			commandManager.add(new TestCommand(this));
			
			taskManager = new TaskManager(this);
		}
		catch(Exception e){
			Logger.error(e);
			close();
		}
	}
	
	private void setEnvVars() {
		DISCORD_BOT_TOKEN = System.getenv("DISCORD_BOT_TOKEN");
		DISCORD_BOT_SECRET = System.getenv("DISCORD_BOT_SECRET");
		ADMIN_DISCORD_ID = System.getenv("ADMIN_DISCORD_ID");
		
		MYSQL_HOST = System.getenv("MYSQL_HOST");
		MYSQL_PORT = System.getenv("MYSQL_PORT");
		MYSQL_DB = System.getenv("MYSQL_DATABASE");
		MYSQL_USER = System.getenv("MYSQL_USER");
		MYSQL_PASSWORD = System.getenv("MYSQL_PASSWORD");
		
		UNSPLASH_CLIENT_ID = System.getenv("UNSPLASH_CLIENT_ID");
	}
	
	public void close() {
		try{
			database.close();
			logger.close();
			System.exit(0);
		}
		catch(NullPointerException e){
			System.exit(0);
		}
	}
	
}
