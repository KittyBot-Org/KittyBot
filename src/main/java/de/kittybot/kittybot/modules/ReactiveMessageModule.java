package de.kittybot.kittybot.modules;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.stats.CacheStats;
import de.kittybot.kittybot.command.CommandContext;
import de.kittybot.kittybot.command.ReactiveMessage;
import de.kittybot.kittybot.module.Module;

import java.util.concurrent.TimeUnit;

public class ReactiveMessageModule extends Module{

	private final Cache<Long, ReactiveMessage> reactiveMessages;

	public ReactiveMessageModule(){
		this.reactiveMessages = Caffeine.newBuilder().expireAfterWrite(1, TimeUnit.HOURS).recordStats().build();
	}

	public void removeReactiveMessage(long responseId){
		reactiveMessages.invalidate(responseId);
	}

	public void addReactiveMessage(CommandContext ctx, long responseId, long allowed){
		reactiveMessages.put(
				responseId,
				new ReactiveMessage(ctx.getGuildId(), ctx.getChannelId(), ctx.getMessageId(), responseId, ctx.getUser().getIdLong(), ctx.getFullPath(),
						allowed
				)
		);
	}

	public ReactiveMessage getReactiveMessage(long responseId){
		return reactiveMessages.getIfPresent(responseId);
	}

	public CacheStats getStats(){
		return this.reactiveMessages.stats();
	}

}
