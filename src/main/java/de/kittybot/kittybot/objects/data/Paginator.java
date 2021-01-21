package de.kittybot.kittybot.objects.data;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.util.function.BiFunction;

public class Paginator{

	private final long guildId, channelId, messageId, authorId;
	private final int maxPages;
	private final BiFunction<Integer, EmbedBuilder, EmbedBuilder> embedFunction;
	private int currentPage;

	public Paginator(Message message, long authorId, int maxPages, BiFunction<Integer, EmbedBuilder, EmbedBuilder> embedFunction){
		this.guildId = message.getGuild().getIdLong();
		this.channelId = message.getChannel().getIdLong();
		this.messageId = message.getIdLong();
		this.authorId = authorId;
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

	public long getAuthorId(){
		return this.authorId;
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
		return this.embedFunction.apply(this.currentPage, new EmbedBuilder().setFooter("Page " + (this.currentPage + 1) + "/" + this.maxPages)).build();
	}

}
