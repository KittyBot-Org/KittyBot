package de.kittybot.kittybot.managers;

import de.kittybot.kittybot.exceptions.CommandException;
import de.kittybot.kittybot.main.KittyBot;
import de.kittybot.kittybot.objects.AnnouncementType;
import de.kittybot.kittybot.objects.StreamAnnouncement;
import de.kittybot.kittybot.streams.Stream;
import de.kittybot.kittybot.streams.StreamType;
import de.kittybot.kittybot.streams.twitch.TwitchWrapper;
import de.kittybot.kittybot.utils.Colors;
import de.kittybot.kittybot.utils.Config;
import de.kittybot.kittybot.utils.MessageUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.net.URL;
import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static de.kittybot.kittybot.jooq.Tables.STREAM_USERS;

public class StreamAnnouncementManager extends ListenerAdapter{

	private static final Logger LOG = LoggerFactory.getLogger(StreamAnnouncementManager.class);

	private final KittyBot main;
	private TwitchWrapper twitchWrapper;
	private final List<StreamAnnouncement> streamAnnouncements;
	private final Set<String> activeStreams;

	public StreamAnnouncementManager(KittyBot main){
		this.main = main;
		if(Config.TWITCH_CLIENT_ID.isBlank() || Config.TWITCH_CLIENT_SECRET.isBlank()){
			LOG.error("Twitch disabled because twitch_client_id and twitch_client_secret are missing");
		}
		else{
			this.twitchWrapper = new TwitchWrapper(Config.TWITCH_CLIENT_ID, Config.TWITCH_CLIENT_SECRET, this.main.getHttpClient());
		}
		this.streamAnnouncements = loadStreamAnnouncements();
		this.activeStreams = new HashSet<>();
	}

	@Override
	public void onReady(@NotNull ReadyEvent event){
		this.main.getScheduler().scheduleAtFixedRate(this::checkStreams, 0, 1, TimeUnit.MINUTES);
	}

	private List<StreamAnnouncement> loadStreamAnnouncements(){
		var dbManager = this.main.getDatabaseManager();
		try(var con = dbManager.getCon(); var ctx = dbManager.getCtx(con).selectFrom(STREAM_USERS)){
			return ctx.fetch().stream().map(StreamAnnouncement::new).collect(Collectors.toList());
		}
		catch(SQLException e){
			LOG.error("Error loading stream announcements", e);
		}
		return new ArrayList<>();
	}

	private void checkStreams(){
		checkTwitch();
		checkYouTube();
	}

	private void checkTwitch(){
		var userNames = this.streamAnnouncements.stream().filter(streamAnnouncement -> streamAnnouncement.getStreamType() == StreamType.TWITCH).map(StreamAnnouncement::getUserName).collect(Collectors.toList());
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
		var guild = this.main.getJDA().getGuildById(guildId);
		if(guild == null){
			return;
		}
		var settings = this.main.getGuildSettingsManager().getSettings(guildId);

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
		var dbManager = this.main.getDatabaseManager();
		try(var con = dbManager.getCon()){
			var rows = dbManager.getCtx(con).insertInto(STREAM_USERS)
					.columns(STREAM_USERS.GUILD_ID, STREAM_USERS.USER_NAME, STREAM_USERS.STREAM_TYPE)
					.values(guildId, name, type.getId())
					.execute();
			if(rows != 1){
				throw new CommandException("Stream already exists");
			}
		}
		catch(SQLException e){
			LOG.error("Error adding stream announcement", e);
		}
		this.streamAnnouncements.add(new StreamAnnouncement(name, guildId, type));
	}

	public List<StreamAnnouncement> get(long guildId){
		return this.streamAnnouncements.stream().filter(stream -> stream.getGuildId() == guildId).collect(Collectors.toList());
	}

	public void delete(String name, long guildId, StreamType type) throws CommandException{
		var dbManager = this.main.getDatabaseManager();
		try(var con = dbManager.getCon()){
			var rows = dbManager.getCtx(con).deleteFrom(STREAM_USERS).where(STREAM_USERS.USER_NAME.eq(name).and(STREAM_USERS.GUILD_ID.eq(guildId)).and(STREAM_USERS.STREAM_TYPE.eq(type.getId()))).execute();
			if(rows != 1){
				throw new CommandException("No stream found");
			}
		}
		catch(SQLException e){
			LOG.error("Error adding stream announcement", e);
		}
		this.streamAnnouncements.removeIf(stream -> stream.getUserName().equals(name) && stream.getStreamType() == type && stream.getGuildId() == guildId);
	}

}
