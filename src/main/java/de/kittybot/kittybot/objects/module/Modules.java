package de.kittybot.kittybot.objects.module;

import de.kittybot.kittybot.main.KittyBot;
import de.kittybot.kittybot.objects.exceptions.ModuleNotFoundException;
import de.kittybot.kittybot.utils.ThreadFactoryHelper;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.api.utils.MiscUtil;
import okhttp3.OkHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class Modules{

	private static final String MODULE_PACKAGE = "de.kittybot.kittybot.modules";
	private static final Logger LOG = LoggerFactory.getLogger(Modules.class);

	private final KittyBot main;
	private final OkHttpClient httpClient;
	private final ScheduledExecutorService scheduler;
	private final List<Module> modules;

	public Modules(KittyBot main){
		this.main = main;
		this.httpClient = new OkHttpClient();
		this.scheduler = new ScheduledThreadPoolExecutor(2, new ThreadFactoryHelper());
		this.modules = new LinkedList<>();
		loadModules();
	}

	private void loadModules(){
		LOG.info("Loading modules...");
		try(var result = new ClassGraph().acceptPackages(MODULE_PACKAGE).scan()){
			var queue = result.getSubclasses(Module.class.getName()).stream()
				.map(ClassInfo::loadClass)
				.filter(Module.class::isAssignableFrom)
				.map(clazz -> {
					try{
						return ((Module) clazz.getDeclaredConstructor().newInstance()).init(this);
					}
					catch(Exception e){
						LOG.info("WTF?!?!?!?! Horsti what did u do to me", e);
					}
					return null;
				})
				.filter(Objects::nonNull)
				.collect(Collectors.toCollection(LinkedList::new));

			while(!queue.isEmpty()){
				var instance = queue.remove();
				var dependencies = instance.getDependencies();
				if(dependencies != null && !dependencies.stream().allMatch(mod -> this.modules.stream().anyMatch(module -> mod == module.getClass()))){
					queue.add(instance);
					LOG.info("Added '{}' back to the queue. Dependencies: {} (Dependency circle jerk incoming!)", instance.getClass().getSimpleName(), dependencies.toString());
					continue;
				}
				instance.onEnable();
				this.modules.add(instance);
			}
		}
		LOG.info("Finished loading {} modules", this.modules.size());
	}

	public Object[] getModules(){
		return this.modules.toArray();
	}

	@SuppressWarnings("unchecked")
	public <T extends Module> T get(Class<T> clazz){
		var module = this.modules.stream().filter(mod -> mod.getClass().equals(clazz)).findFirst();
		if(module.isEmpty()){
			throw new ModuleNotFoundException(clazz);
		}
		return (T) module.get();
	}

	public JDA getJDA(long guildId){
		var shardManager = this.main.getShardManager();
		return shardManager.getShardById(MiscUtil.getShardForGuild(guildId, shardManager.getShardsTotal()));
	}

	public JDA getJDA(int shardId){
		return this.main.getShardManager().getShardById(shardId);
	}

	public Guild getGuildById(long guildId){
		return getShardManager().getGuildById(guildId);
	}

	public ShardManager getShardManager(){
		return this.main.getShardManager();
	}

	public Guild getGuildById(String guildId){
		return getShardManager().getGuildById(guildId);
	}

	public ScheduledFuture<?> scheduleAtFixedRate(Runnable runnable, long initDelay, long delay, TimeUnit timeUnit){
		return this.scheduler.scheduleAtFixedRate(() -> {
			try{
				runnable.run();
			}
			catch(Exception e){
				LOG.error("Unexpected error in scheduler", e);
			}
			}, initDelay, delay, timeUnit);
	}

	public ScheduledFuture<?> schedule(Runnable runnable, long delay, TimeUnit timeUnit){
		return this.scheduler.schedule(() -> {
			try{
				runnable.run();
			}
			catch(Exception e){
				LOG.error("Unexpected error in scheduler", e);
			}
		}, delay, timeUnit);
	}

	public OkHttpClient getHttpClient(){
		return this.httpClient;
	}

}
