package de.kittybot.kittybot.managers;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.source.bandcamp.BandcampAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.http.HttpAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.twitch.TwitchStreamAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.vimeo.VimeoAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import de.kittybot.kittybot.main.KittyBot;
import lavalink.client.io.jda.JdaLavalink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;

public class LavalinkManager{

	private static final Logger LOG = LoggerFactory.getLogger(KittyBot.class);

	private final KittyBot main;
	private final AudioPlayerManager audioPlayerManager = new DefaultAudioPlayerManager();
	private final JdaLavalink lavalink;

	public LavalinkManager(KittyBot main){
		this.main = main;
		this.lavalink = new JdaLavalink(1, guildId -> main.getJDA());

		audioPlayerManager.registerSourceManager(new YoutubeAudioSourceManager());
		audioPlayerManager.registerSourceManager(new BandcampAudioSourceManager());
		audioPlayerManager.registerSourceManager(new VimeoAudioSourceManager());
		audioPlayerManager.registerSourceManager(new TwitchStreamAudioSourceManager());
		audioPlayerManager.registerSourceManager(new HttpAudioSourceManager());
		AudioSourceManagers.registerRemoteSources(audioPlayerManager);
	}

	public void connect(String userId){
		this.lavalink.setUserId(userId);
		var config = main.getConfig();
		try{
			for(var node : config.getArray("lavalink_nodes")){
				lavalink.addNode(new URI("ws://" + node.getString("host") + ":" + node.getString("port")), node.getString("password"));
			}
		}
		catch(URISyntaxException e){
			LOG.error("Error while connecting to lavalink node", e);
		}
	}


	public JdaLavalink getLavalink(){
		return this.lavalink;
	}

	public AudioPlayerManager getAudioPlayerManager(){
		return this.audioPlayerManager;
	}

}
