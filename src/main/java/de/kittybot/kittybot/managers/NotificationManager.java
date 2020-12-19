package de.kittybot.kittybot.managers;

import de.kittybot.kittybot.jooq.tables.records.NotificationsRecord;
import de.kittybot.kittybot.main.KittyBot;
import de.kittybot.kittybot.objects.Notification;
import de.kittybot.kittybot.utils.MessageUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static de.kittybot.kittybot.jooq.Tables.NOTIFICATIONS;

public class NotificationManager{

	private static final Logger LOG = LoggerFactory.getLogger(NotificationManager.class);

	private final KittyBot main;
	private final Map<Long, Notification> notifications;

	public NotificationManager(KittyBot main){
		this.main = main;
		this.notifications = new HashMap<>();
		this.main.getScheduler().scheduleAtFixedRate(this::update, 0, 30, TimeUnit.MINUTES);
		this.main.getScheduler().scheduleAtFixedRate(this::scheduleNext, 0, 5, TimeUnit.MINUTES);
	}

	private void update(){
		var from = LocalDateTime.now();
		var to = from.plus(60, ChronoUnit.MINUTES);
		this.notifications.putAll(retrieveNotifications(from, to));
	}

	private void scheduleNext(){
		var from = LocalDateTime.now();
		var to = from.plus(5, ChronoUnit.MINUTES);
		var notifs = getAndRemoveNext(from, to);
		var now = Instant.now();
		for(var notification : notifs){
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
										.setDescription(notification.getContent())
										.setFooter(member.getEffectiveName(), member.getUser().getEffectiveAvatarUrl())
										.build()
						)
				).queue();
			}, now.until(notification.getNotificationTime(), ChronoUnit.MILLIS), TimeUnit.MILLISECONDS);
		}
	}

	private Map<Long, Notification> retrieveNotifications(LocalDateTime from, LocalDateTime to){
		var dbManager = this.main.getDatabaseManager();
		try(var con = dbManager.getCon(); var ctx = dbManager.getCtx(con).selectFrom(NOTIFICATIONS)){
			return ctx.where(NOTIFICATIONS.NOTIFICATION_TIME.between(from, to)).fetch().stream().collect(
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

	private Set<Notification> getAndRemoveNext(LocalDateTime from, LocalDateTime to){
		var notifications = this.notifications.values().stream().filter(
				notification -> notification.getNotificationTime().isAfter(from) && notification.getNotificationTime().isBefore(to)
		).collect(Collectors.toSet());
		this.notifications.entrySet().removeIf(entry -> notifications.stream().anyMatch(notification -> notification.getId() == entry.getValue().getId()));
		return notifications;
	}

	public void add(Notification notification){
		this.notifications.put(notification.getGuildId(), notification);
	}

	public void remove(Notification notification){
		this.notifications.remove(notification.getId());
	}

}
