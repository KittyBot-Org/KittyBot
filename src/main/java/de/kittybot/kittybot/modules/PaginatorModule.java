package de.kittybot.kittybot.modules;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import de.kittybot.kittybot.module.Module;
import de.kittybot.kittybot.objects.Emoji;
import de.kittybot.kittybot.objects.Paginator;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

public class PaginatorModule extends Module{

	private Cache<Long, Paginator> paginators;

	@Override
	public void onEnable(){
		this.paginators = Caffeine.newBuilder()
				.expireAfterWrite(10, TimeUnit.MINUTES)
				.removalListener((messageId, value, cause) -> remove((Paginator) value))
				.recordStats()
				.build();
	}

	@Override
	public void onGuildMessageReactionAdd(@NotNull GuildMessageReactionAddEvent event){
		var paginator = this.paginators.getIfPresent(event.getMessageIdLong());
		if(paginator == null){
			return;
		}
		var code = event.getReactionEmote().getAsReactionCode();
		var currentPage = paginator.getCurrentPage();
		var maxPages = paginator.getMaxPages();

		if(Emoji.ARROW_LEFT.getAsMention().equals(code)){
			if(currentPage == 0){
				return;
			}
			paginator.previousPage();
			event.getChannel().editMessageById(event.getMessageIdLong(), paginator.constructEmbed()).queue();
		}
		else if(Emoji.ARROW_RIGHT.getAsMention().equals(code)){
			if(currentPage == maxPages - 1){
				return;
			}
			paginator.nextPage();
			event.getChannel().editMessageById(event.getMessageIdLong(), paginator.constructEmbed()).queue();
		}
		else if(Emoji.WASTEBASKET.getAsMention().equals(code)){
			this.paginators.invalidate(event.getMessageIdLong());
		}
		event.getReaction().removeReaction(event.getUser()).queue();
	}

	public void remove(Paginator paginator){
		var guild = this.modules.getGuildById(paginator.getGuildId());
		if(guild == null){
			return;
		}
		var channel = guild.getTextChannelById(paginator.getChannelId());
		if(channel == null){
			return;
		}
		channel.clearReactionsById(paginator.getMessageId()).queue();
	}

	public void create(TextChannel channel, int maxPages, BiFunction<Integer, EmbedBuilder, MessageEmbed> embedFunction){
		var embedBuilder = embedFunction.apply(0, new EmbedBuilder());
		channel.sendMessage(embedBuilder).queue(message -> {
			var paginator = new Paginator(message, maxPages, embedFunction);
			if(channel.getGuild().getSelfMember().hasPermission(Permission.MESSAGE_HISTORY, Permission.MESSAGE_ADD_REACTION)){
				message.addReaction(Emoji.ARROW_LEFT.getAsMention()).queue();
				message.addReaction(Emoji.ARROW_RIGHT.getAsMention()).queue();
				message.addReaction(Emoji.WASTEBASKET.getAsMention()).queue();
			}
			this.paginators.put(paginator.getMessageId(), paginator);
		});
	}

}
