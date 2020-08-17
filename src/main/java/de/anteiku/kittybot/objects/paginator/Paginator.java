package de.anteiku.kittybot.objects.paginator;

import de.anteiku.kittybot.KittyBot;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;

public class Paginator extends ListenerAdapter{ // thanks jda-utilities for your shitty paginator
    private static final Map<Long, List<Long>> PAGINATOR_MESSAGES = new HashMap<>();               // K = channelId, V = List<MessageId>
    private static final Map<Long, Integer> TOTAL_PAGES = new HashMap<>();                         // K = messageId, V = total pages
    private static final Map<Long, Long> INVOKERS = new HashMap<>();                               // K = messageId, V = invokerId
    private static final Map<Long, Long> ORIGINALS = new HashMap<>();                              // K = messageId, V = original messageId
    private static final Map<Long, Integer> CURRENT_PAGE = new HashMap<>();                        // K = messageId, V = current page
    private static final Map<Long, Map<Integer, String>> AUTHORS = new HashMap<>();                // K = messageId, V = Map<PageNumber, Author>
    private static final Map<Long, BiConsumer<Integer, EmbedBuilder>> CONSUMERS = new HashMap<>(); // K = messageId, V = BiConsumer<PageNumber, EmbedBuilder>

    private static final String LEFT_EMOJI = "\u25C0";
    private static final String RIGHT_EMOJI = "\u25B6";
    private static final String WASTEBASKET = "\uD83D\uDDD1\uFE0F";

    public static void createEmbedPaginator(final TextChannel channel, final Message message, final Map<Integer, String> authorPerPage, final int totalPages, final Map<Integer, ArrayList<MessageEmbed.Field>> fields){
        createPaginator(channel, message, authorPerPage, totalPages, (page, embedBuilder) -> fields.get(page).forEach(embedBuilder::addField));
    }

    public static void createPaginator(final TextChannel channel, final Message message, final Map<Integer, String> authorPerPage, final int totalPages, final BiConsumer<Integer, EmbedBuilder> consumer){
        final var embedBuilder = new EmbedBuilder();
        embedBuilder.setAuthor(authorPerPage.get(0));
        embedBuilder.setFooter("Page 1/" + totalPages);
        consumer.accept(0, embedBuilder);

        channel.sendMessage(embedBuilder.build()).queue(paginatorMessage ->{
            paginatorMessage.addReaction(RIGHT_EMOJI).queue();
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
            AUTHORS.put(messageId, authorPerPage);
            CONSUMERS.put(messageId, consumer);

            // TIMEOUT

            KittyBot.getWaiter().waitForEvent(GuildMessageReactionAddEvent.class,
                    ev -> ev.getMessageIdLong() == messageId && ev.getUserIdLong() == authorId,
                    ev -> {}, 1, TimeUnit.MINUTES, () ->{
                        message.delete().queue();
                        channel.deleteMessageById(messageId).queue();

                        removePaginator(channelId, messageId);
                    });
        });
    }

    @Override
    public void onGuildMessageReactionAdd(@NotNull final GuildMessageReactionAddEvent event){
        if (event.getUser().isBot())
            return;

        final var channel = event.getChannel();
        final var channelId = channel.getIdLong();
        final var messageId = event.getMessageIdLong();

        event.getReaction().removeReaction(event.getUser()).queue();
        final var paginators = PAGINATOR_MESSAGES.get(channelId);
        if (paginators == null || !paginators.contains(messageId))
            return;
        if (event.getUserIdLong() != INVOKERS.get(messageId))
            return;
        final var reactionEmote = event.getReactionEmote();
        if (!reactionEmote.isEmoji())
            return;
        final var newPageBuilder = new EmbedBuilder();
        final var currentPage = CURRENT_PAGE.get(messageId);
        final var total = TOTAL_PAGES.get(messageId);
        final var authors = AUTHORS.get(messageId);
        final var emoji = reactionEmote.getEmoji();

        switch (emoji){
            case WASTEBASKET:
                channel.deleteMessageById(ORIGINALS.get(messageId)).queue();
                channel.deleteMessageById(messageId).queue();
                removePaginator(channelId, messageId);
                return;
            case LEFT_EMOJI:
                if (currentPage == 0)
                    return;
                if (currentPage + 1 == total){
                    channel.removeReactionById(messageId, WASTEBASKET)
                           .flatMap(ignored -> channel.addReactionById(messageId, RIGHT_EMOJI))
                           .flatMap(ignored -> channel.addReactionById(messageId, WASTEBASKET))
                           .queue();
                }
                final var previousPage = currentPage - 1;
                newPageBuilder.setAuthor(authors.get(previousPage));
                CONSUMERS.get(messageId).accept(previousPage, newPageBuilder);
                newPageBuilder.setFooter("Page " + (previousPage + 1) + "/" + total); // yes, we could just use currentPage here but it would just bring confusion
                if (previousPage == 0)
                    channel.removeReactionById(messageId, LEFT_EMOJI).queue();
                CURRENT_PAGE.put(messageId, previousPage);
                break;
            case RIGHT_EMOJI:
                if (currentPage == total)
                    return;
                if (currentPage == 0){
                    channel.clearReactionsById(messageId)
                           .flatMap(ignored -> channel.addReactionById(messageId, LEFT_EMOJI))
                           .flatMap(ignored -> channel.addReactionById(messageId, RIGHT_EMOJI))
                           .flatMap(ignored -> channel.addReactionById(messageId, WASTEBASKET))
                           .queue();
                }
                final var nextPage = currentPage + 1;
                newPageBuilder.setAuthor(authors.get(nextPage));
                CONSUMERS.get(messageId).accept(nextPage, newPageBuilder);
                newPageBuilder.setFooter("Page " + (nextPage + 1) + "/" + total);
                if (nextPage + 1 == total)
                    channel.removeReactionById(messageId, RIGHT_EMOJI).queue();
                CURRENT_PAGE.put(messageId, nextPage);
                break;
            default: return;
        }
        channel.editMessageById(messageId, newPageBuilder.build()).queue();
    }

    private static void removePaginator(final long channelId, final long messageId){
        PAGINATOR_MESSAGES.get(channelId).remove(messageId);
        TOTAL_PAGES.remove(messageId);
        INVOKERS.remove(messageId);
        ORIGINALS.remove(messageId);
        CURRENT_PAGE.remove(messageId);
        AUTHORS.remove(messageId);
    }
}