package de.kittybot.kittybot.managers;

import de.kittybot.kittybot.jooq.tables.records.NotificationsRecord;
import de.kittybot.kittybot.main.KittyBot;
import de.kittybot.kittybot.objects.Notification;
import de.kittybot.kittybot.utils.MessageUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static de.kittybot.kittybot.jooq.Tables.NOTIFICATIONS;

public class NotificationManager extends ListenerAdapter{

	private static final Logger LOG = LoggerFactory.getLogger(NotificationManager.class);

	private final KittyBot main;
	private final Map<Long, Notification> notifications;

	public NotificationManager(KittyBot main){
		this.main = main;
		this.notifications = new HashMap<>();
		this.main.getScheduler().scheduleAtFixedRate(this::update, 0, 30, TimeUnit.MINUTES);
	}

	@Override
	public void onReady(@NotNull ReadyEvent event){
		this.main.getScheduler().scheduleAtFixedRate(this::scheduleNext, 0, 5, TimeUnit.MINUTES);
	}

	private void update(){
		this.notifications.putAll(retrieveNotifications(LocalDateTime.now().plus(60, ChronoUnit.MINUTES)));
	}

	private void scheduleNext(){
		schedule(getAndRemoveNext(LocalDateTime.now().plus(5, ChronoUnit.MINUTES)));
	}

	private Map<Long, Notification> retrieveNotifications(LocalDateTime to){
		var dbManager = this.main.getDatabaseManager();
		try(var con = dbManager.getCon(); var ctx = dbManager.getCtx(con).selectFrom(NOTIFICATIONS)){
			return ctx.where(NOTIFICATIONS.NOTIFICATION_TIME.lessOrEqual(to)).fetch().stream().collect(
					Collectors.toMap(
							NotificationsRecord::getNotificationId,
							record -> new Notification(record.getNotificationId(), record.getGuildId(), record.getChannelId(), record.getMessageId(), record.getUserId(), record.getContent(), record.getCreationTime(), record.getNotificationTime())
					)
			);
		}
		catch(SQLException e){
			LOG.error("Error while retrieving notifications", e);
		}
		return Collections.emptyMap();
	}

	private void schedule(Set<Notification> notifs){
		var now = LocalDateTime.now();
		for(var notification : notifs){
			var scheduleIn = 0L;
			if(notification.getNotificationTime().isBefore(now)){
				scheduleIn = 1L;
			}
			else{
				scheduleIn = now.until(notification.getNotificationTime(), ChronoUnit.MILLIS);
			}
			LOG.info("Scheduled in " + scheduleIn);
			this.main.getScheduler().schedule(() -> {
				var guild = this.main.getJDA().getGuildById(notification.getGuildId());
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
										.setAuthor("Notification", MessageUtils.getMessageLink(notification.getGuildId(), notification.getChannelId(), notification.getMessageId()), this.main.getJDA().getSelfUser().getEffectiveAvatarUrl())
										.setColor(Color.ORANGE)
										.setDescription(notification.getContent())
										.setFooter(member.getEffectiveName(), member.getUser().getEffectiveAvatarUrl())
										.build()
						)
				).queue();
				delete(notification.getId(), notification.getUserId());
			}, scheduleIn, TimeUnit.MILLISECONDS);
		}
	}

	private Set<Notification> getAndRemoveNext(LocalDateTime to){
		var notifications = this.notifications.values().stream().filter(
				notification -> notification.getNotificationTime().isBefore(to)
		).collect(Collectors.toSet());
		this.notifications.entrySet().removeIf(entry -> notifications.stream().anyMatch(notification -> notification.getId() == entry.getValue().getId()));
		return notifications;
	}

	public boolean delete(long id, long userId){
		this.notifications.entrySet().removeIf(entry -> entry.getValue().getId() == id && entry.getValue().getUserId() == userId);
		return deleteNotifications(id, userId);
	}

	private boolean deleteNotifications(long id, long userId){
		var dbManager = this.main.getDatabaseManager();
		try(var con = dbManager.getCon()){
			return dbManager.getCtx(con).deleteFrom(NOTIFICATIONS).where(NOTIFICATIONS.NOTIFICATION_ID.eq(id).and((NOTIFICATIONS.USER_ID.eq(userId)))).execute() == 1;
		}
		catch(SQLException e){
			LOG.error("Error while retrieving notifications", e);
		}
		return false;
	}

	public Notification create(long guildId, long channelId, long messageId, long userId, String content, LocalDateTime notificationTime){
		var now = LocalDateTime.now();
		var notif = insertNotification(guildId, channelId, messageId, userId, content, now, notificationTime);
		if(notif == null || notificationTime.isAfter(now.plusHours(1))){
			return notif;
		}
		this.notifications.put(notif.getGuildId(), notif);
		if(notificationTime.isBefore(now.plusMinutes(5))){
			schedule(Collections.singleton(notif));
		}
		return notif;
	}

	private Notification insertNotification(long guildId, long channelId, long messageId, long userId, String content, LocalDateTime creationTime, LocalDateTime notificationTime){
		var dbManager = this.main.getDatabaseManager();
		try(var con = dbManager.getCon()){
			var res = dbManager.getCtx(con).insertInto(NOTIFICATIONS)
					.columns(
							NOTIFICATIONS.GUILD_ID,
							NOTIFICATIONS.CHANNEL_ID,
							NOTIFICATIONS.MESSAGE_ID,
							NOTIFICATIONS.USER_ID,
							NOTIFICATIONS.CONTENT,
							NOTIFICATIONS.CREATION_TIME,
							NOTIFICATIONS.NOTIFICATION_TIME
					).values(
							guildId,
							channelId,
							messageId,
							userId,
							content,
							creationTime,
							notificationTime
					).returningResult(NOTIFICATIONS.NOTIFICATION_ID).fetchOne();
			if(res != null){
				return new Notification(res.get(NOTIFICATIONS.NOTIFICATION_ID), guildId, channelId, messageId, userId, content, creationTime, notificationTime);
			}
		}
		catch(SQLException e){
			LOG.error("Error while retrieving notifications", e);
		}
		return null;
	}

	public Set<Notification> get(long userId){
		return retrieveNotifications(userId);
	}

	private Set<Notification> retrieveNotifications(long userId){
		var dbManager = this.main.getDatabaseManager();
		try(var con = dbManager.getCon(); var ctx = dbManager.getCtx(con).selectFrom(NOTIFICATIONS)){
			return ctx.where(NOTIFICATIONS.USER_ID.eq(userId)).fetch().stream().map(
					record -> new Notification(record.getNotificationId(), record.getGuildId(), record.getChannelId(), record.getMessageId(), record.getUserId(), record.getContent(), record.getCreationTime(), record.getNotificationTime())
			).collect(Collectors.toSet());
		}
		catch(SQLException e){
			LOG.error("Error while retrieving notifications", e);
		}
		return Collections.emptySet();
	}

}
