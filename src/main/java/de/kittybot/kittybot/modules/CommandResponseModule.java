package de.kittybot.kittybot.modules;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.stats.CacheStats;
import de.kittybot.kittybot.module.Module;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.guild.GuildMessageDeleteEvent;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

@SuppressWarnings("unused")
public class CommandResponseModule extends Module{

	private Cache<Long, Long> commandResponses;

	@Override
	public void onEnable(){
		this.commandResponses = Caffeine.newBuilder().expireAfterWrite(1, TimeUnit.HOURS).recordStats().build();
	}

	@Override
	public void onGuildMessageDelete(@NotNull GuildMessageDeleteEvent event){
		var commandResponse = get(event.getMessageIdLong());
		if(commandResponse != -1 && event.getGuild().getSelfMember().hasPermission(Permission.MESSAGE_MANAGE)){
			remove(event.getMessageIdLong());
			event.getChannel().deleteMessageById(commandResponse).reason("deleted due to command deletion").queue();
		}
	}

	public long get(long commandId){
		var res = this.commandResponses.getIfPresent(commandId);
		return res == null ? -1 : res;
	}

	public void remove(long commandId){
		this.commandResponses.invalidate(commandId);
	}

	public void add(long commandId, long responseId){
		if(commandId == -1 || responseId == -1){
			return;
		}
		this.commandResponses.put(commandId, responseId);
	}

	public CacheStats getStats(){
		return this.commandResponses.stats();
	}

}
