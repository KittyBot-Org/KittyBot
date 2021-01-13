package de.kittybot.kittybot.module;

import de.kittybot.kittybot.exceptions.ModuleNotFoundException;
import de.kittybot.kittybot.main.KittyBot;
import io.github.classgraph.ClassGraph;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.api.utils.MiscUtil;
import okhttp3.OkHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;

public class Modules{

	private static final String MODULE_PACKAGE = "de.kittybot.kittybot.modules";
	private static final Logger LOG = LoggerFactory.getLogger(Modules.class);

	private final KittyBot main;
	private final List<Module> modules;

	public Modules(KittyBot main){
		this.main = main;
		this.modules = new LinkedList<>();

		LOG.info("Loading modules...");
		try(var result = new ClassGraph().acceptPackages(MODULE_PACKAGE).scan()){
			for(var cls : result.getSubclasses(Module.class.getName())){
				var instance = cls.loadClass().getDeclaredConstructors()[0].newInstance();
				if(!(instance instanceof Module)){
					continue;
				}
				this.modules.add(((Module) instance).init(this));
			}
		}
		catch(IllegalAccessException | InvocationTargetException | InstantiationException e){
			LOG.error("Error while loading modules", e);
		}
		this.modules.forEach(Module::onEnable);
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

	public ScheduledExecutorService getScheduler(){
		return this.main.getScheduler();
	}

	public OkHttpClient getHttpClient(){
		return this.main.getHttpClient();
	}

}
