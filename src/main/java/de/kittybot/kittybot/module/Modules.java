package de.kittybot.kittybot.module;

import de.kittybot.kittybot.main.KittyBot;
import de.kittybot.kittybot.modules.*;
import de.kittybot.kittybot.web.WebService;
import net.dv8tion.jda.api.JDA;
import okhttp3.OkHttpClient;

import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.stream.Collectors;

public class Modules{

	private final KittyBot main;
	private final LavalinkModule lavalinkModule;
	private final PrometheusModule prometheusModule;
	private final CommandModule commandModule;
	private final EventLogModule eventLogModule;
	private final DatabaseModule databaseModule;
	private final InviteModule inviteModule;
	private final InviteRolesModule inviteRolesModule;
	private final StatusModule statusModule;
	private final MessageModule messageModule;
	private final BotListsModule botListModule;
	private final RequestModule requestModule;
	private final StreamAnnouncementModule streamAnnouncementModule;
	private final NotificationModule notificationModule;
	private final DashboardSessionModule dashboardSessionModule;
	private final SettingsModule settingsModule;
	private final CommandResponseModule commandResponseModule;
	private final ReactiveMessageModule reactiveMessageModule;
	private final TagModule tagModule;
	private final MusicModule musicModule;
	private final JoinModule joinModule;
	private final RoleSaverModule roleSaver;

	private final List<Object> modules;

	public Modules(KittyBot main){
		this.main = main;
		this.modules = new LinkedList<>();

		this.lavalinkModule = new LavalinkModule(this);
		this.tagModule = new TagModule(this);
		this.databaseModule = new DatabaseModule();
		this.reactiveMessageModule = new ReactiveMessageModule();
		this.requestModule = new RequestModule(this);

		this.settingsModule = new SettingsModule(this);
		this.commandModule = new CommandModule(this);
		this.eventLogModule = new EventLogModule();
		this.commandResponseModule = new CommandResponseModule();
		this.joinModule = new JoinModule();
		this.inviteModule = new InviteModule();
		this.inviteRolesModule = new InviteRolesModule(this);
		this.statusModule = new StatusModule(this);
		this.messageModule = new MessageModule();
		this.botListModule = new BotListsModule(this);
		this.roleSaver = new RoleSaverModule(this);
		this.musicModule = new MusicModule(this);
		this.dashboardSessionModule = new DashboardSessionModule(this);
		this.notificationModule = new NotificationModule(this);
		this.streamAnnouncementModule = new StreamAnnouncementModule(this);
		this.prometheusModule = new PrometheusModule(this);

		Collections.addAll(this.modules,
				this.settingsModule,
				this.lavalinkModule.getLavalink(),
				this.commandModule,
				this.eventLogModule,
				this.commandResponseModule,
				this.joinModule,
				this.inviteModule,
				this.inviteRolesModule,
				this.statusModule,
				this.messageModule,
				this.botListModule,
				this.roleSaver,
				this.musicModule,
				this.dashboardSessionModule,
				this.notificationModule,
				this.streamAnnouncementModule,
				this.prometheusModule
		);
	}

	public List<Object> getModules(){
		System.out.println(this.modules.stream().filter(module -> module instanceof EventListener).map(o -> o.getClass().getName()).collect(Collectors.joining(", ")));
		return this.modules;
	}

	public LavalinkModule getLavalinkModule(){
		return this.lavalinkModule;
	}

	public DashboardSessionModule getDashboardSessionModule(){
		return this.dashboardSessionModule;
	}

	public CommandModule getCommandModule(){
		return this.commandModule;
	}

	public DatabaseModule getDatabaseModule(){
		return this.databaseModule;
	}

	public MessageModule getMessageModule(){
		return this.messageModule;
	}

	public RequestModule getRequestModule(){
		return this.requestModule;
	}

	public SettingsModule getGuildSettingsModule(){
		return this.settingsModule;
	}

	public ReactiveMessageModule getReactiveMessageModule(){
		return this.reactiveMessageModule;
	}

	public CommandResponseModule getCommandResponseModule(){
		return this.commandResponseModule;
	}

	public NotificationModule getNotificationModule(){
		return this.notificationModule;
	}

	public InviteModule getInviteModule(){
		return this.inviteModule;
	}

	public TagModule getTagModule(){
		return this.tagModule;
	}

	public MusicModule getMusicModule(){
		return this.musicModule;
	}

	public StreamAnnouncementModule getStreamAnnouncementModule(){
		return this.streamAnnouncementModule;
	}

	public JDA getJDA(){
		return this.main.getJDA();
	}

	public OkHttpClient getHttpClient(){
		return this.main.getHttpClient();
	}

	public ScheduledExecutorService getScheduler(){
		return this.main.getScheduler();
	}

	public WebService getWebService(){
		return this.main.getWebService();
	}

}
