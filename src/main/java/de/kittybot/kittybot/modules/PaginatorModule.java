package de.kittybot.kittybot.modules;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import de.kittybot.kittybot.slashcommands.context.CommandContext;
import de.kittybot.kittybot.module.Module;
import de.kittybot.kittybot.objects.Emoji;
import de.kittybot.kittybot.objects.Paginator;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;
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

	@Override
	public void onGuildMessageReactionAdd(@NotNull GuildMessageReactionAddEvent event){
		if(event.getUser().isBot()){
			return;
		}
		var paginator = this.paginators.getIfPresent(event.getMessageIdLong());
		if(paginator == null){
			return;
		}
		var code = event.getReactionEmote().getAsReactionCode();
		var currentPage = paginator.getCurrentPage();
		var maxPages = paginator.getMaxPages();

		if(Emoji.ARROW_LEFT.get().equals(code)){
			if(currentPage != 0){
				paginator.previousPage();
				event.getChannel().editMessageById(event.getMessageIdLong(), paginator.constructEmbed()).queue();
			}
		}
		else if(Emoji.ARROW_RIGHT.get().equals(code)){
			if(currentPage != maxPages - 1){
				paginator.nextPage();
				event.getChannel().editMessageById(event.getMessageIdLong(), paginator.constructEmbed()).queue();
			}
		}
		else if(Emoji.WASTEBASKET.get().equals(code)){
			if(paginator.getAuthorId() == event.getUserIdLong()){
				event.getChannel().deleteMessageById(event.getMessageIdLong()).queue();
				this.paginators.invalidate(event.getMessageIdLong());
				return;
			}
		}
		event.getReaction().removeReaction(event.getUser()).queue();
	}

	public void create(TextChannel channel, long authorId, int maxPages, BiFunction<Integer, EmbedBuilder, EmbedBuilder> embedFunction){
		var embedBuilder = embedFunction.apply(0, new EmbedBuilder().setFooter("Page: 1/" + maxPages)).build();
		create(maxPages, embedFunction, embedBuilder, channel, authorId);
	}

	public void create(int maxPages, BiFunction<Integer, EmbedBuilder, EmbedBuilder> embedFunction, MessageEmbed embedBuilder, TextChannel channel, long userId){
		channel.sendMessage(embedBuilder).queue(message -> {
			var paginator = new Paginator(message, userId, maxPages, embedFunction);
			if(channel.getGuild().getSelfMember().hasPermission(Permission.MESSAGE_HISTORY, Permission.MESSAGE_ADD_REACTION)){
				if(maxPages > 1){
					message.addReaction(Emoji.ARROW_LEFT.get()).queue();
					message.addReaction(Emoji.ARROW_RIGHT.get()).queue();
				}
				message.addReaction(Emoji.WASTEBASKET.get()).queue();
			}
			this.paginators.put(paginator.getMessageId(), paginator);
		});
	}

	public void create(CommandContext ctx, int maxPages, BiFunction<Integer, EmbedBuilder, EmbedBuilder> embedFunction){
		ctx.acknowledge(true).queue(success -> {
			var embedBuilder = embedFunction.apply(0, new EmbedBuilder().setFooter("Page: 1/" + maxPages)).build();
			var channel = ctx.getChannel();
			create(maxPages, embedFunction, embedBuilder, channel, ctx.getUserId());
		});

	}

}
