package de.kittybot.kittybot.modules;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.stats.CacheStats;
import de.kittybot.kittybot.objects.data.ReactiveMessage;
import de.kittybot.kittybot.objects.module.Module;
import de.kittybot.kittybot.slashcommands.interaction.GuildInteraction;

import java.util.concurrent.TimeUnit;

public class ReactiveMessageModule extends Module{

	private Cache<Long, ReactiveMessage> reactiveMessages;

	@Override
	public void onEnable(){
		this.reactiveMessages = Caffeine.newBuilder().expireAfterWrite(1, TimeUnit.HOURS).recordStats().build();
	}

	public void remove(long responseId){
		reactiveMessages.invalidate(responseId);
	}

	public void add(GuildInteraction ia, long responseId, long allowed){
		reactiveMessages.put(
			responseId,
			new ReactiveMessage(ia.getGuildId(), ia.getChannelId(), -1, responseId, ia.getUser().getIdLong(), ia.getData().getName(),
				allowed
			)
		);
	}

	public ReactiveMessage get(long responseId){
		return reactiveMessages.getIfPresent(responseId);
	}

	public CacheStats getStats(){
		return this.reactiveMessages.stats();
	}

}
