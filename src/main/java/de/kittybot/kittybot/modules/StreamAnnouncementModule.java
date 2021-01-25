package de.kittybot.kittybot.modules;

import de.kittybot.kittybot.objects.enums.AnnouncementType;
import de.kittybot.kittybot.objects.module.Module;
import de.kittybot.kittybot.objects.settings.StreamAnnouncement;
import de.kittybot.kittybot.objects.streams.Stream;
import de.kittybot.kittybot.objects.streams.StreamType;
import de.kittybot.kittybot.objects.streams.twitch.TwitchUser;
import de.kittybot.kittybot.objects.streams.twitch.TwitchWrapper;
import de.kittybot.kittybot.objects.streams.youtube.YouTubeWrapper;
import de.kittybot.kittybot.utils.Colors;
import de.kittybot.kittybot.utils.Config;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.ReadyEvent;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static de.kittybot.kittybot.jooq.Tables.STREAM_USERS;

public class StreamAnnouncementModule extends Module{

	private static final Logger LOG = LoggerFactory.getLogger(StreamAnnouncementModule.class);

	private List<StreamAnnouncement> streamAnnouncements;
	private Set<Long> activeStreams;
	private TwitchWrapper twitchWrapper;
	private YouTubeWrapper youTubeWrapper;


	@Override
	public void onEnable(){
		if(Config.TWITCH_CLIENT_ID.isBlank() || Config.TWITCH_CLIENT_SECRET.isBlank()){
			LOG.error("Twitch disabled because twitch_client_id and twitch_client_secret are missing");
		}
		else{
			this.twitchWrapper = new TwitchWrapper(Config.TWITCH_CLIENT_ID, Config.TWITCH_CLIENT_SECRET, this.modules.getHttpClient());
		}
		this.streamAnnouncements = new ArrayList<>();
		this.activeStreams = new HashSet<>();
	}

	@Override
	public void onReady(@NotNull ReadyEvent event){
		this.streamAnnouncements.addAll(loadStreamAnnouncements());
		this.modules.scheduleAtFixedRate(this::checkStreams, 0, 30, TimeUnit.SECONDS);
	}

	private List<StreamAnnouncement> loadStreamAnnouncements(){
		try(var ctx = this.modules.get(DatabaseModule.class).getCtx().selectFrom(STREAM_USERS)){
			return ctx.fetch().stream().map(StreamAnnouncement::new).collect(Collectors.toList());
		}
	}

	private void checkStreams(){
		checkTwitch();
		checkYouTube();
	}

	private void checkTwitch(){
		var userIds = this.streamAnnouncements.stream().filter(streamAnnouncement -> streamAnnouncement.getStreamType() == StreamType.TWITCH).map(
			StreamAnnouncement::getUserId).collect(Collectors.toList());
		var streams = this.twitchWrapper.getStreams(userIds);
		for(var streamAnnouncement : this.streamAnnouncements){
			var stream = streams.stream().filter(st -> st.getUserId() == streamAnnouncement.getUserId()).findFirst();
			var userId = streamAnnouncement.getUserId();
			if(stream.isPresent() && !this.activeStreams.contains(userId)){
				this.activeStreams.add(userId);
				// send online
				sendAnnouncementMessage(streamAnnouncement, stream.get(), AnnouncementType.START);
			}
			if(stream.isEmpty() && this.activeStreams.contains(streamAnnouncement.getUserId())){
				this.activeStreams.remove(userId);
				// send offline
				//sendAnnouncementMessage(streamAnnouncement, null, AnnouncementType.END);
			}
		}
	}

	private void checkYouTube(){

	}

	private void sendAnnouncementMessage(StreamAnnouncement streamAnnouncement, Stream stream, AnnouncementType announcementType){
		var guildId = streamAnnouncement.getGuildId();
		var guild = this.modules.getGuildById(guildId);
		if(guild == null){
			return;
		}
		var settings = this.modules.get(SettingsModule.class).getSettings(guildId);

		var channel = guild.getTextChannelById(settings.getStreamAnnouncementChannelId());
		if(channel == null){
			return;
		}
		var embed = new EmbedBuilder();
		switch(announcementType){
			case END:
				embed.setAuthor(stream.getUserName(), stream.getStreamUrl());
				break;
			case START:
				embed
					.setTitle(stream.getStreamTitle(), stream.getStreamUrl())
					.setImage(stream.getThumbnailUrl(320, 180))
					.setThumbnail(stream.getGame().getThumbnailUrl(144, 192))
					.addField("Game", stream.getGame().getName(), true);
				break;
		}
		channel.sendMessage(settings.getStreamAnnouncementMessage().replace("${user}", stream.getUserName())).embed(embed
			.setTimestamp(Instant.now())
			.setColor(Colors.TWITCH_PURPLE)
			.build()
		).queue();
	}

	public TwitchUser add(String name, long guildId, StreamType type){
		var user = this.twitchWrapper.getUserByUsername(name);
		if(user == null){
			return null;
		}
		var rows = this.modules.get(DatabaseModule.class).getCtx().insertInto(STREAM_USERS)
			.columns(STREAM_USERS.GUILD_ID, STREAM_USERS.USER_ID, STREAM_USERS.USER_NAME, STREAM_USERS.STREAM_TYPE)
			.values(guildId, user.getId(), name, type.getId())
			.execute();
		if(rows != 1){
			return user;
		}
		this.streamAnnouncements.add(new StreamAnnouncement(user.getId(), name, guildId, type));
		return user;
	}

	public List<StreamAnnouncement> get(long guildId){
		return this.streamAnnouncements.stream().filter(stream -> stream.getGuildId() == guildId).collect(Collectors.toList());
	}

	public boolean remove(String name, long guildId, StreamType type){
		var user = this.twitchWrapper.getUserByUsername(name);
		if(user == null){
			return false;
		}
		var dbModule = this.modules.get(DatabaseModule.class);
		var rows = dbModule.getCtx().deleteFrom(STREAM_USERS).where(
			STREAM_USERS.USER_ID.eq(user.getId()).and(STREAM_USERS.GUILD_ID.eq(guildId)).and(STREAM_USERS.STREAM_TYPE.eq(type.getId()))).execute();
		if(rows != 1){
			return false;
		}

		this.streamAnnouncements.removeIf(stream -> stream.getUserId() == user.getId() && stream.getStreamType() == type && stream.getGuildId() == guildId);
		return true;
	}

}
