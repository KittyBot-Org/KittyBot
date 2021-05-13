package de.kittybot.kittybot.modules;

import de.kittybot.kittybot.jooq.tables.records.StreamUsersRecord;
import de.kittybot.kittybot.objects.enums.AnnouncementType;
import de.kittybot.kittybot.objects.module.Module;
import de.kittybot.kittybot.objects.streams.Stream;
import de.kittybot.kittybot.objects.streams.StreamType;
import de.kittybot.kittybot.objects.streams.twitch.TwitchUser;
import de.kittybot.kittybot.objects.streams.twitch.TwitchWrapper;
import de.kittybot.kittybot.utils.Colors;
import de.kittybot.kittybot.utils.Config;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.ReadyEvent;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static de.kittybot.kittybot.jooq.Tables.STREAM_USERS;

public class StreamModule extends Module{

	private static final Logger LOG = LoggerFactory.getLogger(StreamModule.class);

	private List<StreamUsersRecord> streams;
	private TwitchWrapper twitchWrapper;

	@Override
	public Set<Class<? extends Module>> getDependencies(){
		return Set.of(DatabaseModule.class);
	}

	@Override
	public void onEnable(){
		if(Config.TWITCH_CLIENT_ID.isBlank() || Config.TWITCH_CLIENT_SECRET.isBlank()){
			LOG.error("Twitch disabled because twitch_client_id and twitch_client_secret are missing");
		}
		else{
			this.twitchWrapper = new TwitchWrapper(Config.TWITCH_CLIENT_ID, Config.TWITCH_CLIENT_SECRET, this.modules.getHttpClient());
		}
		try(var ctx = this.modules.get(DatabaseModule.class).getCtx().selectFrom(STREAM_USERS)){
			var result = ctx.fetch();
			result.detach();
			this.streams = result;
		}
	}

	@Override
	public void onReady(@NotNull ReadyEvent event){
		if(this.twitchWrapper != null){
			this.modules.scheduleAtFixedRate(this::checkStreams, 0, 30, TimeUnit.SECONDS);
		}
	}

	private void checkStreams(){
		checkTwitch();
		checkYouTube();
	}

	private void checkTwitch(){
		var userIds = this.streams.stream().filter(streamAnnouncement -> streamAnnouncement.getStreamType() == StreamType.TWITCH.getId())
			.map(StreamUsersRecord::getUserId).collect(Collectors.toList());
		if(userIds.isEmpty()){
			return;
		}
		var streams = this.twitchWrapper.getStreams(userIds, false);

		for(var streamAnnouncement : this.streams){
			var stream = streams.stream().filter(st -> st.getUserId() == streamAnnouncement.getUserId()).findFirst();
			if(stream.isPresent() && !streamAnnouncement.getIsLive()){
				setLiveStatus(streamAnnouncement, true);
				// send online
				sendAnnouncementMessage(streamAnnouncement, stream.get(), AnnouncementType.START);
			}
			if(stream.isEmpty() && streamAnnouncement.getIsLive()){
				setLiveStatus(streamAnnouncement, false);
				// send offline
				//sendAnnouncementMessage(streamAnnouncement, null, AnnouncementType.END);
			}
		}
	}

	private void checkYouTube(){

	}

	private void setLiveStatus(StreamUsersRecord record, boolean status){
		record.setIsLive(status);
		record.attach(this.modules.get(DatabaseModule.class).getConfiguration());
		record.store();
		record.detach();
	}

	private void sendAnnouncementMessage(StreamUsersRecord streamAnnouncement, Stream stream, AnnouncementType announcementType){
		var guildId = streamAnnouncement.getGuildId();
		var guild = this.modules.getGuildById(guildId);
		if(guild == null){
			return;
		}
		var settings = this.modules.get(GuildSettingsModule.class).get(guildId);

		var channel = guild.getTextChannelById(settings.getStreamAnnouncementChannelId());
		if(channel == null || !channel.canTalk()){
			return;
		}
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
		channel.sendMessage(settings.getStreamAnnouncementMessage().replace("${user}", stream.getUserName()))
			.embed(embed
				.setTimestamp(Instant.now())
				.setColor(Colors.TWITCH_PURPLE)
				.build()
			)
			.addFile(thumbnail, "thumbnail.png")
			.queue();
	}

	public TwitchUser add(String name, long guildId, StreamType type){
		var user = this.twitchWrapper.getUserByUsername(name, false);
		if(user == null){
			return null;
		}
		var record = new StreamUsersRecord()
			.setUserId(user.getId())
			.setUserName(user.getDisplayName())
			.setGuildId(guildId)
			.setStreamType(type.getId());

		record.attach(this.modules.get(DatabaseModule.class).getConfiguration());
		record.store();
		record.detach();
		this.streams.add(record);
		return user;
	}

	public List<StreamUsersRecord> get(long guildId){
		return this.streams.stream().filter(stream -> stream.getGuildId() == guildId).collect(Collectors.toList());
	}

	public boolean remove(String name, long guildId, StreamType type){
		var user = this.twitchWrapper.getUserByUsername(name, false);
		if(user == null){
			return false;
		}
		var optionalRecord = this.streams.stream().filter(stream -> stream.getUserId() == user.getId() && stream.getGuildId() == guildId && stream.getStreamType() == type.getId()).findFirst();
		if(optionalRecord.isEmpty()){
			return false;
		}
		var record = optionalRecord.get();
		record.attach(this.modules.get(DatabaseModule.class).getConfiguration());
		record.delete();
		record.detach();
		this.streams.remove(record);
		return true;
	}

}
