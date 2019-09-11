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
	
	public static final OkHttpClient client = new OkHttpClient();
	public static final String ME = "170939974227591168";
	public static String IMGUR_CLIENT_ID;
	public static String UNSPLASH_CLIENT_ID;
	
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
		
		String discordtoken = config.get("discord_token");
		IMGUR_CLIENT_ID = config.get("imgur_client_id");
		UNSPLASH_CLIENT_ID = config.get("unsplash_client_id");
		if(discordtoken.equals("")){
			Logger.print("Please set the discord token in '" + config.getName() + "'!");
			close();
		}
		try{
			jda = new JDABuilder(discordtoken).build().awaitReady();
			rand = new Random();
			new Emotes(this);
			
			database = new Database(this);
			pollManager = new PollManager(this);
			
			commandManager = new CommandManager(this);
			commandManager.add(new HelpCommand(this));
			commandManager.add(new CommandsCommand(this));
			commandManager.add(new RolesCommand(this));
			commandManager.add(new PollCommand(this));
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
