package de.kittybot.kittybot.modules;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.stats.CacheStats;
import de.kittybot.kittybot.module.Module;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import de.kittybot.kittybot.objects.MessageData;
import net.dv8tion.jda.api.events.message.guild.GuildMessageDeleteEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageUpdateEvent;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

public class MessageModule extends Module{

	private final Cache<Long, MessageData> messages;// messageId - messageData
	private final Cache<Long, MessageData> editedMessages;// messageId - messageData
	private final Cache<Long, Long> lastDeletedMessages;// channelId - messageId
	private final Cache<Long, Long> lastEditedMessages;// channelId - messageId

	public MessageModule(){
		this.messages = Caffeine.newBuilder()
				.expireAfterWrite(1, TimeUnit.HOURS)
				.recordStats()
				.build();
		this.lastDeletedMessages = Caffeine.newBuilder()
				.expireAfterWrite(1, TimeUnit.HOURS)
				.recordStats()
				.build();
		this.editedMessages = Caffeine.newBuilder()
				.expireAfterWrite(1, TimeUnit.HOURS)
				.recordStats()
				.build();
		this.lastEditedMessages = Caffeine.newBuilder()
				.expireAfterWrite(1, TimeUnit.HOURS)
				.recordStats()
				.build();
	}

	@Override
	public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event){
		this.messages.put(event.getMessageIdLong(), new MessageData(event.getMessage()));
	}

	@Override
	public void onGuildMessageUpdate(@NotNull GuildMessageUpdateEvent event){
		this.editedMessages.put(event.getMessageIdLong(), new MessageData(event.getMessage()));
		this.lastEditedMessages.put(event.getChannel().getIdLong(), event.getMessageIdLong());
	}

	@Override
	public void onGuildMessageDelete(@NotNull GuildMessageDeleteEvent event){
		this.lastDeletedMessages.put(event.getChannel().getIdLong(), event.getMessageIdLong());
	}

	public MessageData getLastDeletedMessage(long channelId){
		var latest = this.lastDeletedMessages.getIfPresent(channelId);
		return latest == null ? null : messages.getIfPresent(latest);
	}

	public MessageData getLastEditedMessage(long channelId){
		var latest = this.lastEditedMessages.getIfPresent(channelId);
		return latest == null ? null : editedMessages.getIfPresent(latest);
	}

	public CacheStats getStats1(){
		return this.messages.stats();
	}

	public CacheStats getStats2(){
		return this.editedMessages.stats();
	}

	public CacheStats getStats3(){
		return this.lastDeletedMessages.stats();
	}

	public CacheStats getStats4(){
		return this.lastEditedMessages.stats();
	}

}
