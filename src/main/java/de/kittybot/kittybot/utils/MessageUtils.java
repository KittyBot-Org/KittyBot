package de.kittybot.kittybot.utils;

import de.kittybot.kittybot.objects.Emoji;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Invite;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.GenericGuildEvent;
import net.dv8tion.jda.api.events.guild.member.GenericGuildMemberEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.time.Duration;
import java.util.concurrent.ThreadLocalRandom;

public class MessageUtils{

	private MessageUtils(){}

	public static String getBoolEmote(boolean bool) {
		return bool ? Emoji.CHECK.getAsMention() : Emoji.X.getAsMention();
	}

	public static String maskLink(String title, String url){
		return "[" + title + "](" + url + ")";
	}

	public static String formatDurationDHMS(long length){
		return formatDurationDHMS(Duration.ofMillis(length));
	}

	public static String formatDurationDHMS(Duration duration){
		return String.format(
			"%sd %s:%s:%s", duration.toDays(), fTime(duration.toHoursPart()), fTime(duration.toMinutesPart()), fTime(duration.toSecondsPart()));
	}

	public static String fTime(int time){
		return time > 9 ? String.valueOf(time) : "0" + time;
	}

	public static String getUserMention(long userId){
		if(userId == -1L){
			return "unset";
		}
		return "<@!" + userId + ">";
	}

	public static String getRoleMention(long roleId){
		if(roleId == -1L){
			return "unset";
		}
		return "<@&" + roleId + ">";
	}

	public static String getChannelMention(long channelId){
		if(channelId == -1L){
			return "unset";
		}
		return "<#" + channelId + ">";
	}

	public static String getEmoteMention(long emoteId){
		if(emoteId == -1L){
			return "unset";
		}
		return "<:i:" + emoteId + ">";
	}

}
