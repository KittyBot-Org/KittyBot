package de.kittybot.kittybot.managers;

import de.kittybot.kittybot.exceptions.CommandException;
import de.kittybot.kittybot.main.KittyBot;
import de.kittybot.kittybot.objects.StreamAnnouncement;
import de.kittybot.kittybot.streams.StreamType;
import de.kittybot.kittybot.streams.twitch.TwitchWrapper;
import net.dv8tion.jda.api.EmbedBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static de.kittybot.kittybot.jooq.Tables.STREAM_USERS;

public class StreamAnnouncementManager{

	private static final Logger LOG = LoggerFactory.getLogger(StreamAnnouncementManager.class);

	private final KittyBot main;
	private final TwitchWrapper twitchWrapper;
	private final List<StreamAnnouncement> streamAnnouncements;
	private final Set<String> activeStreams;

	public StreamAnnouncementManager(KittyBot main){
		this.main = main;
		var config = this.main.getConfig();
		this.twitchWrapper = new TwitchWrapper(config.getString("twitch_client_id"), config.getString("twitch_client_secret"), this.main.getHttpClient());
		this.streamAnnouncements = loadStreamAnnouncements();
		this.activeStreams = new HashSet<>();
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
				sendAnnouncementMessage(streamAnnouncement, new EmbedBuilder().setDescription(userName + " is now Online"));
			}
			if(stream.isEmpty() && this.activeStreams.contains(streamAnnouncement.getUserName())){
				this.activeStreams.remove(userName);
				// send offline
				sendAnnouncementMessage(streamAnnouncement, new EmbedBuilder().setDescription(userName + " is now Offline"));
			}
		}
	}

	private void checkYouTube(){

	}

	private void sendAnnouncementMessage(StreamAnnouncement streamAnnouncement, EmbedBuilder embedBuilder){
		var guild = this.main.getJDA().getGuildById(streamAnnouncement.getGuildId());
		if(guild == null){
			return;
		}
		var channel = guild.getTextChannelById(732551971340550164L);
		if(channel == null){
			return;
		}
		channel.sendMessage(embedBuilder.build()).queue();
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
		return this.streamAnnouncements.parallelStream().filter(stream -> stream.getGuildId() == guildId).collect(Collectors.toList());
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
