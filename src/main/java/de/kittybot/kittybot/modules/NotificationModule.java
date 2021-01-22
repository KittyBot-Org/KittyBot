package de.kittybot.kittybot.modules;

import de.kittybot.kittybot.jooq.tables.records.NotificationsRecord;
import de.kittybot.kittybot.objects.module.Module;
import de.kittybot.kittybot.objects.settings.Notification;
import de.kittybot.kittybot.slashcommands.application.Category;
import de.kittybot.kittybot.utils.Colors;
import de.kittybot.kittybot.utils.MessageUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.ReadyEvent;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static de.kittybot.kittybot.jooq.Tables.NOTIFICATIONS;

public class NotificationModule extends Module{

	private static final Logger LOG = LoggerFactory.getLogger(NotificationModule.class);

	private Map<Long, Notification> notifications;

	@Override
	public void onEnable(){
		this.notifications = new HashMap<>();
		this.modules.getScheduler().scheduleAtFixedRate(this::update, 0, 30, TimeUnit.MINUTES);
	}

	private void update(){
		this.notifications.putAll(retrieveNotifications(LocalDateTime.now().plus(60, ChronoUnit.MINUTES)));
	}

	private Map<Long, Notification> retrieveNotifications(LocalDateTime to){
		try(var ctx = this.modules.get(DatabaseModule.class).getCtx().selectFrom(NOTIFICATIONS)){
			return ctx.where(NOTIFICATIONS.NOTIFICATION_TIME.lessOrEqual(to)).fetch().stream().collect(
				Collectors.toMap(NotificationsRecord::getId, Notification::new)
			);
		}
	}

	@Override
	public void onReady(@NotNull ReadyEvent event){
		this.modules.getScheduler().scheduleAtFixedRate(this::scheduleNext, 0, 5, TimeUnit.MINUTES);
	}

	private void scheduleNext(){
		schedule(getAndRemoveNext(LocalDateTime.now().plus(5, ChronoUnit.MINUTES)));
	}

	private void schedule(Set<Notification> notifs){
		var now = LocalDateTime.now();
		for(var notification : notifs){
			long scheduleIn;
			if(notification.getNotificationTime().isBefore(now)){
				scheduleIn = 1L;
			}
			else{
				scheduleIn = now.until(notification.getNotificationTime(), ChronoUnit.MILLIS);
			}
			this.modules.getScheduler().schedule(() -> {
				var guild = this.modules.getGuildById(notification.getGuildId());
				if(guild == null){
					return;
				}
				var channel = guild.getTextChannelById(notification.getChannelId());
				if(channel == null){
					return;
				}
				guild.retrieveMemberById(notification.getUserId()).flatMap(member ->
					channel.sendMessage(member.getAsMention()).embed(
						new EmbedBuilder()
							.setAuthor("Notification", Category.NOTIFICATION.getUrl(), Category.NOTIFICATION.getEmoteUrl())
							.setColor(Colors.NOTIFICATION)
							.setDescription(notification.getContent())
							.addField("Message", MessageUtils.maskLink("click here", MessageUtils.getMessageLink(notification.getGuildId(), notification.getChannelId(), notification.getMessageId())), false)
							.setFooter(member.getEffectiveName(), member.getUser().getEffectiveAvatarUrl())
							.build()
					)
				).queue();
				delete(notification.getId(), notification.getUserId());
			}, scheduleIn, TimeUnit.MILLISECONDS);
		}
	}

	private Set<Notification> getAndRemoveNext(LocalDateTime to){
		var nextNotifs = this.notifications.values().stream().filter(
			notification -> notification.getNotificationTime().isBefore(to)
		).collect(Collectors.toSet());
		this.notifications.entrySet().removeIf(entry -> nextNotifs.stream().anyMatch(notification -> notification.getId() == entry.getValue().getId()));
		return nextNotifs;
	}

	public boolean delete(long id, long userId){
		this.notifications.entrySet().removeIf(entry -> entry.getValue().getId() == id && entry.getValue().getUserId() == userId);
		return deleteNotifications(id, userId);
	}

	private boolean deleteNotifications(long id, long userId){
		return this.modules.get(DatabaseModule.class).getCtx().deleteFrom(NOTIFICATIONS)
			.where(NOTIFICATIONS.ID.eq(id).and((NOTIFICATIONS.USER_ID.eq(userId))))
			.execute() == 1;
	}

	public Notification create(long guildId, long channelId, long messageId, long userId, String content, LocalDateTime notificationTime){
		var now = LocalDateTime.now();
		var notif = insertNotification(guildId, channelId, messageId, userId, content, now, notificationTime);
		if(notif == null || notificationTime.isAfter(now.plusHours(1))){
			return notif;
		}
		if(notificationTime.isBefore(now.plusMinutes(5))){
			schedule(Collections.singleton(notif));
		}
		else{
			this.notifications.put(notif.getGuildId(), notif);
		}
		return notif;
	}

	private Notification insertNotification(long guildId, long channelId, long messageId, long userId, String content, LocalDateTime creationTime, LocalDateTime notificationTime){
		var res = this.modules.get(DatabaseModule.class).getCtx().insertInto(NOTIFICATIONS)
			.columns(
				NOTIFICATIONS.GUILD_ID,
				NOTIFICATIONS.CHANNEL_ID,
				NOTIFICATIONS.MESSAGE_ID,
				NOTIFICATIONS.USER_ID,
				NOTIFICATIONS.CONTENT,
				NOTIFICATIONS.CREATED_AT,
				NOTIFICATIONS.NOTIFICATION_TIME
			).values(
				guildId,
				channelId,
				messageId,
				userId,
				content,
				creationTime,
				notificationTime
			).returningResult(NOTIFICATIONS.ID).fetchOne();
		if(res == null){
			return null;
		}
		return new Notification(res.get(NOTIFICATIONS.ID), guildId, channelId, messageId, userId, content, creationTime, notificationTime);
	}

	public List<Notification> get(long userId){
		return retrieveNotifications(userId);
	}

	private List<Notification> retrieveNotifications(long userId){
		var dbModule = this.modules.get(DatabaseModule.class);
		try(var ctx = dbModule.getCtx().selectFrom(NOTIFICATIONS)){
			return ctx.where(NOTIFICATIONS.USER_ID.eq(userId)).fetch().map(Notification::new);
		}
	}

}
