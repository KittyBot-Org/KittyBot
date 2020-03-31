package de.anteiku.kittybot;

import de.anteiku.kittybot.commands.*;
import de.anteiku.kittybot.config.Config;
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
import java.sql.Time;
import java.util.*;

public class KittyBot{
	
	public final OkHttpClient httpClient;
	public String unsplashClientId;
	public String host;
	public String defaultPrefix;
	
	public JDA jda;
	public Logger logger;
	public Config config;
	public CommandManager commandManager;
	public TaskManager taskManager;
	public Database database;
	public WebService webService;
	public Random rand;
	
	public static void main(String[] args){
		new KittyBot();
	}
	
	public KittyBot(){
		httpClient = new OkHttpClient();
		logger = new Logger(this);
		
		config = new Config("options.cfg");

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
		
		String discordToken = config.get("discord_token");
		defaultPrefix = config.get("default_prefix");
		host = config.get("host");
		unsplashClientId = config.get("unsplash_client_id");
		if(discordToken.equals("") || unsplashClientId.equals("")){
			Logger.print("Please set the api token in '" + config.getName() + "'!");
			close();
		}
		rand = new Random();
		new ConsoleThread(this);
		try{
			jda = JDABuilder.create(
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
				.setToken(discordToken)
				.setActivity(Activity.of(Activity.ActivityType.LISTENING, "to you!", "https://github.com/TopiSenpai/KittyBot"))
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
	
	public void close(){
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
