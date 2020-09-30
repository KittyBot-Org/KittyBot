package de.kittybot.kittybot.objects.paginator;

import de.kittybot.kittybot.KittyBot;
import de.kittybot.kittybot.objects.Emojis;
import de.kittybot.kittybot.objects.TitleInfo;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;

/**
 * @author caneleex
 */
public class Paginator extends ListenerAdapter{ // thanks jda-utilities for your shitty paginator

	private static final Map<Long, List<Long>> PAGINATOR_MESSAGES = new ConcurrentHashMap<>();                       // K = channelId, V = List<MessageId>
	private static final Map<Long, Integer> TOTAL_PAGES = new ConcurrentHashMap<>();                                 // K = messageId, V = total pages
	private static final Map<Long, Long> INVOKERS = new ConcurrentHashMap<>();                                       // K = messageId, V = invokerId
	private static final Map<Long, Long> ORIGINALS = new ConcurrentHashMap<>();                                      // K = messageId, V = original messageId
	private static final Map<Long, Integer> CURRENT_PAGE = new ConcurrentHashMap<>();                                // K = messageId, V = current page
	private static final Map<Long, BiConsumer<Integer, EmbedBuilder>> CONTENT_CONSUMERS = new ConcurrentHashMap<>(); // K = messageId, V = BiConsumer<PageNumber, EmbedBuilder>

	public static void createCommandsPaginator(final Message message, final int totalPages, final Map<Integer, TitleInfo> titlePerPage, final Map<Integer, ArrayList<MessageEmbed.Field>> fields){
		createPaginator(message, totalPages, (page, embedBuilder) -> {
			var titleInfo = titlePerPage.get(page);
			embedBuilder.setTitle(titleInfo.getTitle(), titleInfo.getUrl());
			fields.get(page).forEach(embedBuilder::addField);
			embedBuilder.setTimestamp(Instant.now());
		});
	}

	public static void createPaginator(final Message message, final int totalPages, final BiConsumer<Integer, EmbedBuilder> contentConsumer){
		final var channel = message.getTextChannel();
		final var embedBuilder = new EmbedBuilder();
		embedBuilder.setFooter("Page 1/" + totalPages);
		contentConsumer.accept(0, embedBuilder);

		channel.sendMessage(embedBuilder.build()).queue(paginatorMessage -> {
			paginatorMessage.addReaction(Emojis.ARROW_RIGHT).queue();
			paginatorMessage.addReaction(Emojis.WASTEBASKET).queue();

			// CACHING

			final var authorId = message.getAuthor().getIdLong();
			final var channelId = channel.getIdLong();
			final var messageId = paginatorMessage.getIdLong();

			PAGINATOR_MESSAGES.computeIfAbsent(channelId, k -> new ArrayList<>()).add(messageId);
			TOTAL_PAGES.put(messageId, totalPages);
			INVOKERS.put(messageId, authorId);
			ORIGINALS.put(messageId, message.getIdLong());
			CURRENT_PAGE.put(messageId, 0);
			CONTENT_CONSUMERS.put(messageId, contentConsumer);

			// TIMEOUT

			KittyBot.getWaiter()
					.waitForEvent(GuildMessageReactionAddEvent.class, ev -> ev.getMessageIdLong() == messageId && ev.getUserIdLong() == authorId, ev -> {}, 3, TimeUnit.MINUTES,
							() -> {
								message.delete().queue();
								channel.deleteMessageById(messageId).queue();

								removePaginator(channelId, messageId);
							});
		});
	}

	private static void removePaginator(final long channelId, final long messageId){
		PAGINATOR_MESSAGES.get(channelId).remove(messageId);
		TOTAL_PAGES.remove(messageId);
		INVOKERS.remove(messageId);
		ORIGINALS.remove(messageId);
		CURRENT_PAGE.remove(messageId);
		CONTENT_CONSUMERS.remove(messageId);
	}

	@Override
	public void onGuildMessageReactionAdd(final GuildMessageReactionAddEvent event){
		if(event.getUser().isBot()){
			return;
		}

		final var channel = event.getChannel();
		final var channelId = channel.getIdLong();
		final var messageId = event.getMessageIdLong();

		final var paginators = PAGINATOR_MESSAGES.get(channelId);
		if(paginators == null || !paginators.contains(messageId)){
			return;
		}
		event.getReaction().removeReaction(event.getUser()).queue();
		if(event.getUserIdLong() != INVOKERS.get(messageId)){
			return;
		}
		final var reactionEmote = event.getReactionEmote();
		if(!reactionEmote.isEmoji()){
			return;
		}
		final var newPageBuilder = new EmbedBuilder();
		final var currentPage = CURRENT_PAGE.get(messageId);
		final var total = TOTAL_PAGES.get(messageId);
		final var contentConsumer = CONTENT_CONSUMERS.get(messageId);
		final var emoji = reactionEmote.getEmoji();

		switch(emoji){
			case Emojis.WASTEBASKET:
				channel.deleteMessageById(ORIGINALS.get(messageId)).queue();
				channel.deleteMessageById(messageId).queue();
				removePaginator(channelId, messageId);
				return;
			case Emojis.ARROW_LEFT:
				if(currentPage == 0){
					return;
				}
				if(currentPage + 1 == total){
					channel.removeReactionById(messageId, Emojis.WASTEBASKET)
							.flatMap(ignored -> channel.addReactionById(messageId, Emojis.ARROW_RIGHT))
							.flatMap(ignored -> channel.addReactionById(messageId, Emojis.WASTEBASKET))
							.queue();
				}
				final var previousPage = currentPage - 1;
				contentConsumer.accept(previousPage, newPageBuilder);
				newPageBuilder.setFooter("Page " + (previousPage + 1) + "/" + total); // yes, we could just use currentPage here but it would just bring confusion
				if(previousPage == 0){
					channel.removeReactionById(messageId, Emojis.ARROW_LEFT).queue();
				}
				CURRENT_PAGE.put(messageId, previousPage);
				break;
			case Emojis.ARROW_RIGHT:
				final var nextPage = currentPage + 1;
				if(nextPage == total){
					return;
				}
				if(currentPage == 0){
					channel.clearReactionsById(messageId)
							.flatMap(ignored -> channel.addReactionById(messageId, Emojis.ARROW_LEFT))
							.flatMap(ignored -> channel.addReactionById(messageId, Emojis.ARROW_RIGHT))
							.flatMap(ignored -> channel.addReactionById(messageId, Emojis.WASTEBASKET))
							.queue();
				}
				contentConsumer.accept(nextPage, newPageBuilder);
				newPageBuilder.setFooter("Page " + (nextPage + 1) + "/" + total);
				if(nextPage + 1 == total){
					channel.removeReactionById(messageId, Emojis.ARROW_RIGHT).queue();
				}
				CURRENT_PAGE.put(messageId, nextPage);
				break;
			default:
				return;
		}
		channel.editMessageById(messageId, newPageBuilder.build()).queue();
	}

}