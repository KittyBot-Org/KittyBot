package de.anteiku.kittybot;

import de.anteiku.kittybot.commands.*;
import de.anteiku.kittybot.config.Config;
import de.anteiku.kittybot.database.Database;
import de.anteiku.kittybot.events.OnGuildMemberJoinEvent;
import de.anteiku.kittybot.events.OnGuildMemberLeaveEvent;
import de.anteiku.kittybot.events.OnGuildMessageReactionAddEvent;
import de.anteiku.kittybot.events.OnGuildMessageReceivedEvent;
import de.anteiku.kittybot.poll.PollManager;
import de.anteiku.kittybot.tasks.PollTask;
import de.anteiku.kittybot.tasks.TaskManager;
import de.anteiku.kittybot.webservice.WebService;
import net.aksingh.owmjapis.core.OWM;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Game;
import okhttp3.OkHttpClient;

import javax.security.auth.login.LoginException;
import java.util.Random;

public class KittyBot{
	
	public static final String UNSPLASHTOKEN = "6e24263ff3b94bac27daa47d540f289cf1cf7887e16d0457a6df1c3c74b4af26";
	public static final String CLIENTID = "0e455fd386d593c";
	public static final String CLIENTSECRET = "cd302f8a0943f725b36301ded01e524d298f0610";
	public static final String UNSPLASHURL = "https://api.unsplash.com/photos/random/?client_id=6e24263ff3b94bac27daa47d540f289cf1cf7887e16d0457a6df1c3c74b4af26&query=";
	public static final String IMGURURL = "https://api.imgur.com/3/gallery/search/{{top}}/{{all}}/{{1}}?q=";
	public static final String TENORURL = "https://api.tenor.com/v1/";
	public static final String NEKOSURL = "https://nekos.life/api/v2/img/";
	public static final String ANTEIKUURL = "http://anteiku.de:9000/";
	public static final OkHttpClient client = new OkHttpClient();
	public static final String MYGUILD = "592763557515362324";
	public static final String DEFAULTPREFIX = ".";
	public static final String ME = "170939974227591168";
	
	public JDA jda;
	public Logger logger;
	public Config config;
	public CommandManager commandManager;
	public PollManager pollManager;
	public TaskManager taskManager;
	public Database database;
	public OWM owm;
	public WebService webService;
	public static Random rand;
	
	public static void main(String[] args){
		new KittyBot();
	}
	
	public KittyBot(){
		logger = new Logger(this);
		
		config = new Config("options.cfg");
		
		String discordtoken = config.get("discordtoken");
		
		if(discordtoken.equals("")){
			Logger.print("Please set the discord token in '" + config.getName() + "'!");
			close();
		}
		try{
			jda = new JDABuilder(discordtoken).build().awaitReady();
			rand = new Random();
			new Emotes(this);
			
			owm = new OWM("aa97a5b91d53f36c47ae133b6ba8c892");
			owm.setUnit(OWM.Unit.METRIC);
			database = new Database(this);
			pollManager = new PollManager(this);
			
			commandManager = new CommandManager(this);
			commandManager.add(new HelpCommand(this));
			commandManager.add(new CommandsCommand(this));
			commandManager.add(new RolesCommand(this));
			commandManager.add(new PollCommand(this));
			//commandManager.add(new WeatherCommand(this));
			commandManager.add(new ScreenShareCommand(this));
			commandManager.add(new SearchCommand(this));
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
			commandManager.add(new LoginCommand(this));
			commandManager.add(new OptionsCommand(this));
			commandManager.add(new DeleteCommand(this));
			commandManager.add(new TestCommand(this));
			
			jda.getPresence().setStatus(OnlineStatus.ONLINE);
			jda.getPresence().setGame(Game.listening("you!"));
			
			jda.addEventListener(new OnGuildMessageReceivedEvent(this));
			jda.addEventListener(new OnGuildMemberJoinEvent(this));
			jda.addEventListener(new OnGuildMemberLeaveEvent(this));
			jda.addEventListener(new OnGuildMessageReactionAddEvent(this));
			
			new ConsoleThread(this);
			
			taskManager = new TaskManager(this);
			taskManager.registerTask(new PollTask(this));
			webService = new WebService(this, 80);
		}
		catch(LoginException | InterruptedException e){
			Logger.error(e);
			close();
		}
	}
	
	public void close(){
		try{
			pollManager.close();
			database.close();
			logger.close();
			System.exit(0);
		}
		catch(NullPointerException e){
			Logger.error(e);
			System.exit(0);
		}
		
	}
	
}
