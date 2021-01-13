package de.kittybot.kittybot.modules;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.stats.CacheStats;
import de.kittybot.kittybot.command.ReactiveMessage;
import de.kittybot.kittybot.command.context.CommandContext;
import de.kittybot.kittybot.module.Module;

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

	public void add(CommandContext ctx, long responseId, long allowed){
		reactiveMessages.put(
				responseId,
				new ReactiveMessage(ctx.getGuildId(), ctx.getChannelId(), ctx.getMessageId(), responseId, ctx.getUser().getIdLong(), ctx.getFullPath(),
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
