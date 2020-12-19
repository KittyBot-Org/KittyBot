package de.kittybot.kittybot.managers;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.stats.CacheStats;
import de.kittybot.kittybot.command.ReactiveMessage;
import de.kittybot.kittybot.command.ctx.CommandContext;
import de.kittybot.kittybot.main.KittyBot;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.concurrent.TimeUnit;

public class ReactiveMessageManager{

	private final Cache<Long, ReactiveMessage> reactiveMessages;

	public ReactiveMessageManager(){
		this.reactiveMessages = Caffeine.newBuilder().expireAfterWrite(1, TimeUnit.HOURS).recordStats().build();
	}

	public void removeReactiveMessage(long responseId){
		reactiveMessages.invalidate(responseId);
	}

	public void addReactiveMessage(CommandContext ctx, long responseId, long allowed){
		reactiveMessages.put(responseId, new ReactiveMessage(ctx.getGuild().getIdLong(), ctx.getChannel().getIdLong(), ctx.getMessage().getIdLong(), responseId, ctx.getUser().getIdLong(), ctx.getFullPath(), allowed));
	}

	public ReactiveMessage getReactiveMessage(long responseId){
		return reactiveMessages.getIfPresent(responseId);
	}

	public CacheStats getStats(){
		return this.reactiveMessages.stats();
	}

}
