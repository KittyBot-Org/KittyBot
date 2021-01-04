package de.kittybot.kittybot.managers;

import de.kittybot.kittybot.main.KittyBot;
import de.kittybot.kittybot.utils.Config;
import lavalink.client.io.jda.JdaLavalink;
import lavalink.client.io.jda.JdaLink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;

public class LavalinkManager{

	private static final Logger LOG = LoggerFactory.getLogger(KittyBot.class);

	private final JdaLavalink lavalink;

	public LavalinkManager(KittyBot main){
		this.lavalink = new JdaLavalink(1, guildId -> main.getJDA());
	}

	public void connect(String userId){
		this.lavalink.setUserId(userId);
		try{
			for(var node : Config.LAVALINK_NODES){
				lavalink.addNode(new URI("ws://" + node.getHost() + ":" + node.getPort()), node.getPassword());
			}
		}
		catch(URISyntaxException e){
			LOG.error("Error while connecting to lavalink node", e);
		}
	}

	public JdaLink getLink(long guildId){
		return this.lavalink.getLink(guildId);
	}

	public JdaLavalink getLavalink(){
		return this.lavalink;
	}

}
