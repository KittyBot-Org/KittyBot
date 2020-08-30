package de.anteiku.kittybot.objects.paginator;

import de.anteiku.kittybot.KittyBot;
import de.anteiku.kittybot.objects.TitleInfo;
import de.anteiku.kittybot.objects.command.CommandContext;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;

import static de.anteiku.kittybot.objects.Emojis.*;

/**
 * @author caneleex
 */
public class Paginator extends ListenerAdapter{ // thanks jda-utilities for your shitty paginator

	private static final Map<Long, List<Long>> PAGINATOR_MESSAGES = new HashMap<>();                       // K = channelId, V = List<MessageId>
	private static final Map<Long, Integer> TOTAL_PAGES = new HashMap<>();                                 // K = messageId, V = total pages
	private static final Map<Long, Long> INVOKERS = new HashMap<>();                                       // K = messageId, V = invokerId
	private static final Map<Long, Long> ORIGINALS = new HashMap<>();                                      // K = messageId, V = original messageId
	private static final Map<Long, Integer> CURRENT_PAGE = new HashMap<>();                                // K = messageId, V = current page
	private static final Map<Long, BiConsumer<Integer, EmbedBuilder>> CONTENT_CONSUMERS = new HashMap<>(); // K = messageId, V = BiConsumer<PageNumber, EmbedBuilder>

	public static void createCommandsPaginator(final CommandContext ctx, final int totalPages, final Map<Integer, TitleInfo> titlePerPage, final Map<Integer, ArrayList<MessageEmbed.Field>> fields){
		createPaginator(ctx, totalPages, (page, embedBuilder) -> {
			var titleInfo = titlePerPage.get(page);
			embedBuilder.setTitle(titleInfo.getTitle(), titleInfo.getUrl());
			fields.get(page).forEach(embedBuilder::addField);
			embedBuilder.setTimestamp(Instant.now());
		});
	}

	public static void createPaginator(final CommandContext ctx, final int totalPages, final BiConsumer<Integer, EmbedBuilder> contentConsumer){
		final var channel = ctx.getChannel();
		final var message = ctx.getMessage();
		final var selfMember = channel.getGuild().getSelfMember();
		if(!channel.canTalk()){
			if(selfMember.hasPermission(channel, Permission.MESSAGE_ADD_REACTION, Permission.MESSAGE_HISTORY)){
				message.addReaction(X).queue();
			}
			return;
		}
		if(!selfMember.hasPermission(channel, Permission.MESSAGE_HISTORY) || !selfMember.hasPermission(channel, Permission.MESSAGE_ADD_REACTION) || !selfMember.hasPermission(channel, Permission.MESSAGE_MANAGE)){
			channel.sendMessage(new EmbedBuilder().setColor(Color.RED)
					.addField("Error:", "I'm missing required permissions for paginator to work. Ensure that i can read the message history, add reactions and manage messages in this channel.", true)
					.setFooter(ctx.getMember().getEffectiveName(), ctx.getUser().getEffectiveAvatarUrl())
					.setTimestamp(Instant.now())
					.build()).queue(); // TODO improve checks
			return;
		}
		final var embedBuilder = new EmbedBuilder();
		embedBuilder.setFooter("Page 1/" + totalPages);
		contentConsumer.accept(0, embedBuilder);

		channel.sendMessage(embedBuilder.build()).queue(paginatorMessage -> {
			paginatorMessage.addReaction(ARROW_RIGHT).queue();
			paginatorMessage.addReaction(WASTEBASKET).queue();

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
			case WASTEBASKET:
				channel.deleteMessageById(ORIGINALS.get(messageId)).queue();
				channel.deleteMessageById(messageId).queue();
				removePaginator(channelId, messageId);
				return;
			case ARROW_LEFT:
				if(currentPage == 0){
					return;
				}
				if(currentPage + 1 == total){
					channel.removeReactionById(messageId, WASTEBASKET)
							.flatMap(ignored -> channel.addReactionById(messageId, ARROW_RIGHT))
							.flatMap(ignored -> channel.addReactionById(messageId, WASTEBASKET))
							.queue();
				}
				final var previousPage = currentPage - 1;
				contentConsumer.accept(previousPage, newPageBuilder);
				newPageBuilder.setFooter("Page " + (previousPage + 1) + "/" + total); // yes, we could just use currentPage here but it would just bring confusion
				if(previousPage == 0){
					channel.removeReactionById(messageId, ARROW_LEFT).queue();
				}
				CURRENT_PAGE.put(messageId, previousPage);
				break;
			case ARROW_RIGHT:
				if(currentPage == total){
					return;
				}
				if(currentPage == 0){
					channel.clearReactionsById(messageId)
							.flatMap(ignored -> channel.addReactionById(messageId, ARROW_LEFT))
							.flatMap(ignored -> channel.addReactionById(messageId, ARROW_RIGHT))
							.flatMap(ignored -> channel.addReactionById(messageId, WASTEBASKET))
							.queue();
				}
				final var nextPage = currentPage + 1;
				contentConsumer.accept(nextPage, newPageBuilder);
				newPageBuilder.setFooter("Page " + (nextPage + 1) + "/" + total);
				if(nextPage + 1 == total){
					channel.removeReactionById(messageId, ARROW_RIGHT).queue();
				}
				CURRENT_PAGE.put(messageId, nextPage);
				break;
			default:
				return;
		}
		channel.editMessageById(messageId, newPageBuilder.build()).queue();
	}

}