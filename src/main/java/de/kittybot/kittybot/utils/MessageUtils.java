package de.kittybot.kittybot.utils;

import de.kittybot.kittybot.objects.Emoji;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.utils.MiscUtil;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Matcher;

public class MessageUtils{

	private MessageUtils(){}

	public static String getBoolEmote(boolean bool){
		return bool ? Emoji.CHECK.getAsMention() : Emoji.X.getAsMention();
	}

	public static String maskLink(String title, String url){
		return "[" + title + "](" + url + ")";
	}

	public static <T> String pluralize(String text, Collection<T> collection){
		return pluralize(text, collection.size());
	}

	public static String pluralize(String text, int count){
		return count == 1 ? text : text + "s";
	}

	public static Set<String> getMentions(String message){
		var mentions = new HashSet<String>();
		var userMatcher = Message.MentionType.USER.getPattern().matcher(message);
		while (userMatcher.find()){
			try{
				MiscUtil.parseSnowflake(userMatcher.group(1));
				mentions.add(userMatcher.group());
			}
			catch (NumberFormatException ignored) {}
		}

		var roleMatcher = Message.MentionType.ROLE.getPattern().matcher(message);
		while (roleMatcher.find()){
			try{
				MiscUtil.parseSnowflake(roleMatcher.group(1));
				mentions.add(roleMatcher.group());
			}
			catch (NumberFormatException ignored) {}
		}

		if(Message.MentionType.EVERYONE.getPattern().matcher(message).find()){
			mentions.add("@everyone");
		}

		if(Message.MentionType.HERE.getPattern().matcher(message).find()){
			mentions.add("@here");
		}
		return mentions;
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

	public static String getMessageLink(long guildId, long channelId, long messageId){
		return String.format("https://discord.com/channels/%d/%d/%d", guildId, channelId, messageId);
	}

}
