package de.kittybot.kittybot.modules;

import de.kittybot.kittybot.objects.module.Module;
import de.kittybot.kittybot.utils.Config;
import lavalink.client.io.jda.JdaLavalink;
import lavalink.client.io.jda.JdaLink;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.hooks.VoiceDispatchInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.net.URI;
import java.net.URISyntaxException;

public class LavalinkModule extends Module{

	private static final Logger LOG = LoggerFactory.getLogger(LavalinkModule.class);

	private JdaLavalink lavalink;

	@Override
	public void onEnable(){
		this.lavalink = new JdaLavalink(String.valueOf(Config.BOT_ID), 1, guildId -> modules.getJDA(guildId));
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

	protected JdaLink getExistingLink(long guildId){
		return this.lavalink.getExistingLink(guildId);
	}

	public JdaLavalink getLavalink(){
		return this.lavalink;
	}

	@Override
	public void onGenericEvent(@Nonnull GenericEvent event){
		this.lavalink.onEvent(event);
	}

	public VoiceDispatchInterceptor getVoiceInterceptor(){
		return this.lavalink.getVoiceInterceptor();
	}

}
