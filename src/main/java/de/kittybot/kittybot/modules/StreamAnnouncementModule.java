package de.kittybot.kittybot.modules;

import de.kittybot.kittybot.exceptions.CommandException;
import de.kittybot.kittybot.module.Module;
import de.kittybot.kittybot.objects.AnnouncementType;
import de.kittybot.kittybot.objects.StreamAnnouncement;
import de.kittybot.kittybot.streams.Stream;
import de.kittybot.kittybot.streams.StreamType;
import de.kittybot.kittybot.streams.twitch.TwitchWrapper;
import de.kittybot.kittybot.streams.youtube.YouTubeWrapper;
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
	private Set<String> activeStreams;
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
		this.modules.getScheduler().scheduleAtFixedRate(this::checkStreams, 0, 1, TimeUnit.MINUTES);
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
		var userNames = this.streamAnnouncements.stream().filter(streamAnnouncement -> streamAnnouncement.getStreamType() == StreamType.TWITCH).map(
				StreamAnnouncement::getUserName).collect(Collectors.toList());
		var streams = this.twitchWrapper.getStreams(userNames);
		for(var streamAnnouncement : this.streamAnnouncements){
			var stream = streams.stream().filter(st -> st.getUserName().equals(streamAnnouncement.getUserName())).findFirst();
			var userName = streamAnnouncement.getUserName();
			if(stream.isPresent() && !this.activeStreams.contains(userName)){
				this.activeStreams.add(userName);
				// send online
				sendAnnouncementMessage(streamAnnouncement, stream.get(), AnnouncementType.START);
			}
			if(stream.isEmpty() && this.activeStreams.contains(streamAnnouncement.getUserName())){
				this.activeStreams.remove(userName);
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
				embed.setAuthor(streamAnnouncement.getUserName(), streamAnnouncement.getStreamUrl());
				break;
			case START:
				embed
						.setTitle(stream.getStreamTitle(), streamAnnouncement.getStreamUrl())
						.setImage(stream.getThumbnailUrl(854, 480))
						.setThumbnail(stream.getGame().getThumbnailUrl(144, 192))
						.addField("Game", stream.getGame().getName(), true);
				break;
		}
		channel.sendMessage(settings.getStreamAnnouncementMessage()).embed(embed
				.setTimestamp(Instant.now())
				.setColor(Colors.TWITCH_PURPLE)
				.build()
		).queue();
	}

	public void add(String name, long guildId, StreamType type) throws CommandException{
		var rows = this.modules.get(DatabaseModule.class).getCtx().insertInto(STREAM_USERS)
				.columns(STREAM_USERS.GUILD_ID, STREAM_USERS.USER_NAME, STREAM_USERS.STREAM_TYPE)
				.values(guildId, name, type.getId())
				.execute();
		if(rows != 1){
			throw new CommandException("Stream already exists");
		}

		this.streamAnnouncements.add(new StreamAnnouncement(name, guildId, type));
	}

	public List<StreamAnnouncement> get(long guildId){
		return this.streamAnnouncements.stream().filter(stream -> stream.getGuildId() == guildId).collect(Collectors.toList());
	}

	public void delete(String name, long guildId, StreamType type) throws CommandException{
		var dbModule = this.modules.get(DatabaseModule.class);
		var rows = dbModule.getCtx().deleteFrom(STREAM_USERS).where(
				STREAM_USERS.USER_NAME.eq(name).and(STREAM_USERS.GUILD_ID.eq(guildId)).and(STREAM_USERS.STREAM_TYPE.eq(type.getId()))).execute();
		if(rows != 1){
			throw new CommandException("No stream found");
		}

		this.streamAnnouncements.removeIf(
				stream -> stream.getUserName().equals(name) && stream.getStreamType() == type && stream.getGuildId() == guildId);
	}

}