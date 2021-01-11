package de.kittybot.kittybot.objects;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

public class Paginator{

	private final long guildId, channelId, messageId;
	private int currentPage;
	private final int maxPages;
	private final BiFunction<Integer, EmbedBuilder, MessageEmbed> embedFunction;

	public Paginator(Message message, int maxPages, BiFunction<Integer, EmbedBuilder, MessageEmbed> embedFunction){
		this.guildId = message.getGuild().getIdLong();
		this.channelId = message.getChannel().getIdLong();
		this.messageId = message.getIdLong();
		this.currentPage = 0;
		this.maxPages = maxPages;
		this.embedFunction = embedFunction;
	}

	public long getGuildId(){
		return this.guildId;
	}

	public long getChannelId(){
		return this.channelId;
	}

	public long getMessageId(){
		return this.messageId;
	}

	public int getCurrentPage(){
		return this.currentPage;
	}

	public int getMaxPages(){
		return this.maxPages;
	}

	public void previousPage(){
		this.currentPage--;
	}

	public void nextPage(){
		this.currentPage++;
	}

	public MessageEmbed constructEmbed(){
		return this.embedFunction.apply(this.currentPage, new EmbedBuilder());
	}

}
