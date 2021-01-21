package de.kittybot.kittybot.modules;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.stats.CacheStats;
import de.kittybot.kittybot.objects.data.MessageData;
import de.kittybot.kittybot.objects.module.Module;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageDeleteEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageUpdateEvent;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

public class MessageModule extends Module{

	private Cache<Long, MessageData> messages;// messageId - messageData
	private Cache<Long, MessageData> editedMessages;// messageId - messageData
	private Cache<Long, Long> lastDeletedMessages;// channelId - messageId
	private Cache<Long, Long> lastEditedMessages;// channelId - messageId

	@Override
	public void onEnable(){
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
		if(event.getMessage().getContentRaw().isBlank()){
			return;
		}
		cacheMessage(event.getMessage());
	}

	@Override
	public void onGuildMessageUpdate(@NotNull GuildMessageUpdateEvent event){
		cacheMessage(event.getMessage());
	}

	@Override
	public void onGuildMessageDelete(@NotNull GuildMessageDeleteEvent event){
		this.lastDeletedMessages.put(event.getChannel().getIdLong(), event.getMessageIdLong());
	}

	private void cacheMessage(Message message){
		var messageId = message.getIdLong();
		var cachedMessage = messages.getIfPresent(messageId);
		var messageData = new MessageData(message);
		if(cachedMessage != null){
			this.editedMessages.put(messageId, cachedMessage.setTimeEdited(messageData.getTimeEdited()));
			this.lastEditedMessages.put(message.getChannel().getIdLong(), messageId);
		}
		this.messages.put(messageId, messageData);
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
