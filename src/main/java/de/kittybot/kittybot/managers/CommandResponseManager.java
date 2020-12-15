package de.kittybot.kittybot.managers;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.stats.CacheStats;

import java.util.concurrent.TimeUnit;

public class CommandResponseManager{

	private final Cache<Long, Long> commandResponses;

	public CommandResponseManager(){
		this.commandResponses = Caffeine.newBuilder().expireAfterWrite(1, TimeUnit.HOURS).recordStats().build();
	}

	public void add(long commandId, long responseId){
		this.commandResponses.put(commandId, responseId);
	}

	public void remove(long commandId){
		this.commandResponses.invalidate(commandId);
	}

	public long get(long commandId){
		var res = this.commandResponses.getIfPresent(commandId);
		return res == null ? -1 : res;
	}

	public CacheStats getStats(){
		return this.commandResponses.stats();
	}

}
