package de.kittybot.kittybot.modules;

import de.kittybot.kittybot.objects.enums.AnnouncementType;
import de.kittybot.kittybot.objects.module.Module;
import de.kittybot.kittybot.objects.settings.StreamAnnouncement;
import de.kittybot.kittybot.objects.streams.Stream;
import de.kittybot.kittybot.objects.streams.StreamType;
import de.kittybot.kittybot.objects.streams.twitch.Subscription;
import de.kittybot.kittybot.objects.streams.twitch.TwitchUser;
import de.kittybot.kittybot.objects.streams.twitch.TwitchWrapper;
import de.kittybot.kittybot.utils.Colors;
import de.kittybot.kittybot.utils.Config;
import net.dv8tion.jda.api.EmbedBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import static de.kittybot.kittybot.jooq.Tables.STREAM_USERS;

public class StreamModule extends Module{

	private static final Logger LOG = LoggerFactory.getLogger(StreamModule.class);

	private TwitchWrapper twitchWrapper;

	@Override
	public Set<Class<? extends Module>> getDependencies(){
		return Set.of(DatabaseModule.class);
	}

	@Override
	public void onEnable(){
		if(Config.TWITCH_CLIENT_ID.isBlank() || Config.TWITCH_CLIENT_SECRET.isBlank() || Config.TWITCH_WEBHOOK_CALLBACK.isBlank() || Config.TWITCH_WEBHOOK_SECRET.isBlank()){
			LOG.error("Twitch disabled because twitch_client_id and twitch_client_secret are missing");
		}
		else{
			this.twitchWrapper = new TwitchWrapper(Config.TWITCH_CLIENT_ID, Config.TWITCH_CLIENT_SECRET, Config.TWITCH_WEBHOOK_CALLBACK, Config.TWITCH_WEBHOOK_SECRET, this.modules.getHttpClient());
		}
	}

	public void sendAnnouncementMessage(Collection<StreamAnnouncement> streamAnnouncements, Stream stream, AnnouncementType announcementType){
		var embed = new EmbedBuilder();
		var streamThumbnailUrl = stream.getThumbnailUrl(320, 180);
		InputStream thumbnail;
		try{
			thumbnail = new URL(streamThumbnailUrl).openStream();
		}
		catch(IOException e){
			LOG.error("Failed to get thumbnail url", e);
			return;
		}
		switch(announcementType){
			case END:
				embed.setAuthor(stream.getUserName(), stream.getStreamUrl());
				break;
			case START:
				embed.setTitle(stream.getStreamTitle(), stream.getStreamUrl())
					.setImage("attachment://thumbnail.png")
					.setThumbnail(stream.getGame().getThumbnailUrl(144, 192))
					.addField("Game", stream.getGame().getName(), true);
				break;
		}
		for(var streamAnnouncement : streamAnnouncements){
			System.out.println(streamAnnouncement);
			var guildId = streamAnnouncement.getGuildId();
			var guild = this.modules.getGuildById(guildId);
			if(guild == null){
				return;
			}
			var settings = this.modules.get(SettingsModule.class).getSettings(guildId);

			var channel = guild.getTextChannelById(settings.getStreamAnnouncementChannelId());
			if(channel == null || !channel.canTalk()){
				return;
			}
			channel.sendMessage(settings.getStreamAnnouncementMessage().replace("${user}", stream.getUserName()))
				.embed(embed
					.setTimestamp(Instant.now())
					.setColor(Colors.TWITCH_PURPLE)
					.build()
				)
				.addFile(thumbnail, "thumbnail.png")
				.queue();
		}
	}

	public TwitchUser add(String name, long guildId, StreamType type){
		var user = this.twitchWrapper.getUserByUsername(name, false);
		if(user == null){
			return null;
		}

		var rows = this.modules.get(DatabaseModule.class).getCtx()
			.insertInto(STREAM_USERS)
			.columns(STREAM_USERS.GUILD_ID, STREAM_USERS.USER_ID, STREAM_USERS.USER_NAME, STREAM_USERS.STREAM_TYPE)
			.values(guildId, user.getId(), user.getDisplayName(), type.getId())
			.onConflict(STREAM_USERS.GUILD_ID, STREAM_USERS.USER_ID, STREAM_USERS.STREAM_TYPE)
			.doNothing()
			.execute();
		if(rows == 0){
			return null;
		}

		if(this.twitchWrapper.getSubscriptions().values().stream().noneMatch(subscription -> subscription.getConditions().stream().noneMatch(condition -> condition.getValue() instanceof  Long && ((Long) condition.getValue()) == user.getId()))){
			this.twitchWrapper.subscribe(Subscription.Type.STREAM_ONLINE, new Subscription.Condition("broadcaster_user_id", Long.toString(user.getId())));
			this.twitchWrapper.subscribe(Subscription.Type.STREAM_OFFLINE, new Subscription.Condition("broadcaster_user_id", Long.toString(user.getId())));
		}

		return user;
	}

	public boolean remove(String name, long guildId, StreamType type){
		var user = this.twitchWrapper.getUserByUsername(name, false);
		if(user == null){
			return false;
		}

		var records = this.modules.get(DatabaseModule.class).getCtx()
			.selectFrom(STREAM_USERS)
			.where(STREAM_USERS.USER_ID.eq(user.getId())
				.and(STREAM_USERS.STREAM_TYPE.eq(type.getId()))
			).fetch();

		var subscription = this.twitchWrapper.getSubscriptions().values().stream().filter(sub ->
			sub.getConditions().stream().anyMatch(condition ->
				condition.getValue().equals("broadcaster_user_id") && condition.getValue() instanceof Long && ((Long) condition.getValue()) == user.getId()
			)
		).findFirst();

		if(subscription.isPresent()){
			this.twitchWrapper.unsubscribe(subscription.get().getId());
			this.twitchWrapper.unsubscribe(subscription.get().getId());
		}

		var rows = this.modules.get(DatabaseModule.class).getCtx()
			.deleteFrom(STREAM_USERS)
			.where(STREAM_USERS.GUILD_ID.eq(guildId)
				.and(STREAM_USERS.USER_ID.eq(user.getId())
					.and(STREAM_USERS.STREAM_TYPE.eq(type.getId())))
			).execute();
		return rows == 1;
	}

	public TwitchWrapper getTwitchWrapper(){
		return this.twitchWrapper;
	}

	public List<StreamAnnouncement> getStreamAnnouncements(long userId, StreamType type){
		try(var ctx = this.modules.get(DatabaseModule.class).getCtx().selectFrom(STREAM_USERS)){
			return ctx.where(STREAM_USERS.USER_ID.eq(userId).and(STREAM_USERS.STREAM_TYPE.eq(type.getId()))).fetch(StreamAnnouncement::new);
		}
	}

	public List<StreamAnnouncement> getStreamAnnouncements(long guildId){
		try(var ctx = this.modules.get(DatabaseModule.class).getCtx().selectFrom(STREAM_USERS)){
			return ctx.where(STREAM_USERS.GUILD_ID.eq(guildId)).fetch(StreamAnnouncement::new);
		}
	}

}
