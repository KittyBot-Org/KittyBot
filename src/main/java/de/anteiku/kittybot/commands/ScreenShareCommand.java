package de.anteiku.kittybot.commands;

import de.anteiku.kittybot.KittyBot;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

public class ScreenShareCommand extends ACommand{
	
	public static String COMMAND = "screenshare";
	public static String USAGE = "screenshare";
	public static String DESCRIPTION = "Sends a screenshare link";
	protected static String[] ALIAS = {"sshare", "share"};
	
	public ScreenShareCommand(KittyBot main){
		super(main, COMMAND, USAGE, DESCRIPTION, ALIAS);
	}
	
	@Override
	public void run(String[] args, GuildMessageReceivedEvent event){
		VoiceChannel channel = event.getMember().getVoiceState().getChannel();
		if(channel == null){
			sendError(event.getMessage(), "You need to be connected to a voice channel!");
			return;
		}
		sendAnswer(event.getMessage(), "Click [here](https://discordapp.com/channels/" + event.getGuild().getId() + "/" + channel.getId() + ") to share your screen for " + event.getChannel().getAsMention() + "!", "ScreenShare Link");
	}
	
}
