package de.kittybot.kittybot.modules;

import de.kittybot.kittybot.main.KittyBot;
import de.kittybot.kittybot.module.Module;
import de.kittybot.kittybot.module.Modules;
import de.kittybot.kittybot.utils.Config;
import lavalink.client.io.jda.JdaLavalink;
import lavalink.client.io.jda.JdaLink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;

public class LavalinkModule extends Module{

	private static final Logger LOG = LoggerFactory.getLogger(KittyBot.class);

	private final JdaLavalink lavalink;

	public LavalinkModule(Modules modules){
		this.lavalink = new JdaLavalink(String.valueOf(Config.BOT_ID), 1, guildId -> modules.getJDA());
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
