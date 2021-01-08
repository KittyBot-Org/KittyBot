package de.kittybot.kittybot.utils;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import de.kittybot.kittybot.command.CommandContext;
import de.kittybot.kittybot.modules.SettingsModule;
import net.dv8tion.jda.api.Permission;

import java.util.Collection;

public class MusicUtils{

	public static String formatTracks(String message, Collection<AudioTrack> tracks){
		var trackMessage = new StringBuilder(message).append("\n");
		for(var track : tracks){
			var name = formatTrackWithInfo(track) + "\n";
			if(trackMessage.length() + name.length() >= 2048){
				break;
			}
			trackMessage.append(name);
		}
		return trackMessage.toString();
	}

	public static String formatTrackWithInfo(AudioTrack track){
		var info = track.getInfo();
		return formatTrack(track) + " - " + TimeUtils.formatDuration(info.length) + "[" + MessageUtils.getUserMention(track.getUserData(Long.class)) + "]";
	}

	public static String formatTrack(AudioTrack track){
		var info = track.getInfo();
		return MessageUtils.maskLink("`" + info.title + "`", info.uri);
	}

	public static boolean checkVoiceRequirements(CommandContext ctx){
		var member = ctx.getMember();
		if(member.hasPermission(Permission.ADMINISTRATOR) || ctx.get(SettingsModule.class).hasDJRole(member)){
			return true;
		}
		var voiceState = member.getVoiceState();
		if(voiceState == null || voiceState.getChannel() == null){
			ctx.sendError("Please connect to a voice channel to use music commands");
			return false;
		}
		var myVoiceState = ctx.getSelfMember().getVoiceState();
		if(myVoiceState != null && myVoiceState.getChannel() != null && voiceState.getChannel().getIdLong() != myVoiceState.getChannel().getIdLong()){
			ctx.sendError("Please connect to the same voice channel as me to use music commands");
			return false;
		}
		return true;
	}

}
