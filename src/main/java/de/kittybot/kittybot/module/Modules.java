package de.kittybot.kittybot.module;

import de.kittybot.kittybot.main.KittyBot;
import de.kittybot.kittybot.modules.*;
import de.kittybot.kittybot.web.WebService;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.hooks.EventListener;
import okhttp3.OkHttpClient;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.stream.Collectors;

public class Modules{

	private final KittyBot main;
	private final List<Module> modules;

	public Modules(KittyBot main){
		this.main = main;
		this.modules = new LinkedList<>();

		addAll(new LavalinkModule(this),
				new TagModule(this),
				new DatabaseModule(),
				new ReactiveMessageModule(),
				new RequestModule(this),
				new SettingsModule(this),
				new CommandModule(this),
				new EventLogModule(),
				new CommandResponseModule(),
				new JoinModule(),
				new InviteModule(),
				new InviteRolesModule(this),
				new StatusModule(this),
				new MessageModule(),
				new BotListsModule(this),
				new RoleSaverModule(this),
				new MusicModule(this),
				new DashboardSessionModule(this),
				new NotificationModule(this),
				new StreamAnnouncementModule(this),
				new PrometheusModule(this),
				new WebService(this)
		);
	}

	private void addAll(Module... modules){
		for(var module : modules){
			this.modules.add(module);
		}
	}

	public Object[] getModules(){
		return this.modules.toArray();
	}

	public <T extends Module> T get(Class<T> clazz){
		System.out.println("Clazz: " + clazz.getSimpleName());
		return (T) this.modules.stream().filter(module -> {
			System.out.println("Module: " + module.getClass().getSimpleName());
			return module.getClass().getSimpleName().equals(clazz.getSimpleName());
		}).findFirst().orElse(null);
	}

	public JDA getJDA(){
		return this.main.getJDA();
	}

	public ScheduledExecutorService getScheduler(){
		return this.main.getScheduler();
	}

	public OkHttpClient getHttpClient(){
		return this.main.getHttpClient();
	}

}
