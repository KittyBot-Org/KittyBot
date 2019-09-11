package de.anteiku.kittybot.commands;

import de.anteiku.kittybot.KittyBot;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

import java.awt.*;

public class ScreenShareCommand extends Command{
	
	public static String COMMAND = "screenshare";
	public static String USAGE = "screenshare";
	public static String DESCRIPTION = "Sends a screenshare link";
	public static String[] ALIAS = {"sshare", "share"};
	
	public ScreenShareCommand(KittyBot main){
		super(main, COMMAND, USAGE, DESCRIPTION, ALIAS);
	}
	
	@Override
	public void run(String[] args, GuildMessageReceivedEvent event){
		VoiceChannel channel = event.getMember().getVoiceState().getChannel();
		if(channel == null){
			sendError(event.getChannel(), "You need to connect to a voice channel!");
			return;
		}
		EmbedBuilder eb = new EmbedBuilder();
		eb.setColor(Color.GREEN);
		eb.setTitle("ScreenShare Link");
		eb.setDescription("Click [here](https://discordapp.com/channels/" + event.getGuild().getId() + "/" + channel.getId() + ") to share your screen!");
		event.getChannel().sendMessage(eb.build()).queue();
	}
	
}
