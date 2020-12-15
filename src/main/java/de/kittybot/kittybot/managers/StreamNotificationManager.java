package de.kittybot.kittybot.managers;

import de.kittybot.kittybot.main.KittyBot;
import de.kittybot.kittybot.objects.StreamAnnouncement;
import de.kittybot.kittybot.streams.StreamType;
import de.kittybot.kittybot.streams.twitch.TwitchWrapper;
import net.dv8tion.jda.api.EmbedBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class StreamNotificationManager{

	private static final Logger LOG = LoggerFactory.getLogger(StreamNotificationManager.class);

	private final KittyBot main;
	private final TwitchWrapper twitchWrapper;
	private final List<StreamAnnouncement> streamAnnouncements;
	private final List<String> activeStreams;

	public StreamNotificationManager(KittyBot main){
		this.main = main;
		var config = this.main.getConfig();
		this.twitchWrapper = new TwitchWrapper(config.getString("twitch_client_id"), config.getString("twitch_client_secret"), this.main.getHttpClient());
		this.streamAnnouncements = new ArrayList<>();
		this.streamAnnouncements.add(new StreamAnnouncement("Topi_Senpai", 608506410803658753L, StreamType.TWITCH));
		this.activeStreams = new ArrayList<>();
		this.main.getScheduler().scheduleAtFixedRate(this::checkStreams, 0, 60, TimeUnit.SECONDS);
	}

	private void checkStreams(){
		LOG.info("Starting checking streams...");
		this.streamAnnouncements.forEach(stream -> {
			if(stream.getStreamType() == StreamType.TWITCH){
				checkTwitch(stream);
			}
			else if(stream.getStreamType() == StreamType.YOUTUBE){
				checkYouTube(stream);
			}
		});
		LOG.info("Finished checking streams...");

	}

	private void checkTwitch(StreamAnnouncement streamAnnouncement){
		var streams = this.twitchWrapper.getStreams(streamAnnouncement.getUserName());
		if(streams.isEmpty() && this.activeStreams.contains(streamAnnouncement.getUserName())){
			this.activeStreams.remove(streamAnnouncement.getUserName());
			sendAnnouncementMessage(streamAnnouncement, new EmbedBuilder().setDescription(streamAnnouncement.getUserName() + " went offline!"));
		}
		else if(!streams.isEmpty() && !this.activeStreams.contains(streamAnnouncement.getUserName())){
			this.activeStreams.add(streamAnnouncement.getUserName());
			sendAnnouncementMessage(streamAnnouncement, new EmbedBuilder().setDescription(streamAnnouncement.getUserName() + " went online!"));
		}
	}

	private void checkYouTube(StreamAnnouncement streamAnnouncement){

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

}
