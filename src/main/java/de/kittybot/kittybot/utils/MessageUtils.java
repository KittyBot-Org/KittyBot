package de.kittybot.kittybot.utils;

import de.kittybot.kittybot.cache.GuildSettingsCache;
import de.kittybot.kittybot.cache.InviteCache;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Invite;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.GenericGuildEvent;
import net.dv8tion.jda.api.events.guild.member.GenericGuildMemberEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class MessageUtils{

	private static final List<String> JOIN_MESSAGES = loadMessageFile("join");
	private static final List<String> LEAVE_MESSAGES = loadMessageFile("leave");
	private static final List<String> BOOST_MESSAGES = loadMessageFile("boost");

	private static final Logger LOG = LoggerFactory.getLogger(MessageUtils.class);

	private MessageUtils(){}

	public static List<String> loadMessageFile(String fileName){
		var inputStream = MessageUtils.class.getClassLoader().getResourceAsStream("messages/" + fileName + "_messages.txt");
		if(inputStream == null){
			LOG.error("Message file not found");
			return null;
		}
		var reader = new BufferedReader(new InputStreamReader((inputStream), StandardCharsets.UTF_8));
		List<String> set = new ArrayList<>();
		try{
			String line;
			while((line = reader.readLine()) != null){
				set.add(line);
			}
			reader.close();
		}
		catch(IOException e){
			LOG.error("Error reading message file", e);
		}
		return set;
	}

	public static String maskLink(String title, String url){
		return "[" + title + "](" + url + ")";
	}

	public static String getUserMention(String userId){
		if(userId.equals("-1")){
			return "unset";
		}
		return "<@" + userId + ">";
	}

	public static String getRoleMention(String roleId){
		if(roleId.equals("-1")){
			return "unset";
		}
		return "<@&" + roleId + ">";
	}

	public static String getChannelMention(String channelId){
		if(channelId.equals("-1")){
			return "unset";
		}
		return "<#" + channelId + ">";
	}

	public static String getEmoteMention(String emoteId){
		if(emoteId.equals("-1")){
			return "unset";
		}
		return "<:i:" + emoteId + ">";
	}

	public static void sendAnnouncementMessage(Guild guild, GenericGuildEvent event){
		var settings = GuildSettingsCache.getGuildSettings(guild.getId());
		var announcementChannelId = settings.getAnnouncementChannelId();
		if(announcementChannelId.equals("-1")){
			return;
		}
		var channel = guild.getTextChannelById(announcementChannelId);
		if(channel == null || !channel.canTalk()){
			return;
		}
		var template = "I lack the permission to send %s messages to " + channel.getAsMention() + ".\n" + "You can disable them with `options %smessages off` if you don't like them.";
		var message = "";

		if(event instanceof GuildMemberJoinEvent){
			if(!settings.areJoinMessagesEnabled()){
				return;
			}
			template = String.format(template, "join");
			message = generateJoinMessage(settings.getJoinMessage(), ((GenericGuildMemberEvent) event).getUser(), InviteCache.getUsedInvite(guild));
		}
		else if(event instanceof GuildMemberRemoveEvent){
			if(!settings.areLeaveMessagesEnabled()){
				return;
			}
			template = String.format(template, "leave");
			message = generateLeaveMessage(settings.getLeaveMessage(), ((GuildMemberRemoveEvent) event).getUser());
		}
		else{
			if(!settings.areBoostMessagesEnabled()){
				return;
			}
			template = String.format(template, "boost");
			message = generateBoostMessage(settings.getBoostMessage(), ((GenericGuildMemberEvent) event).getUser());
		}

		// java :)
		final var finalTemplate = template;
		final var finalMessage = message;
		event.getJDA()
				.openPrivateChannelById(event.getGuild().getOwnerId())
				.flatMap(privateChannel -> privateChannel.sendMessage(finalTemplate))
				.flatMap(ignored -> channel.sendMessage(finalMessage))
				.queue();
	}

	private static String generateBoostMessage(String message, User user){
		if(BOOST_MESSAGES != null && BOOST_MESSAGES.size() > 1){
			var random = BOOST_MESSAGES.get(ThreadLocalRandom.current().nextInt(BOOST_MESSAGES.size() - 1));
			message = message.replace("${random_boost_message}", random);
		}
		return replacePlaceholders(message, user);
	}

	private static String generateJoinMessage(String message, User user, Invite invite){
		if(JOIN_MESSAGES != null && JOIN_MESSAGES.size() > 1){
			var random = JOIN_MESSAGES.get(ThreadLocalRandom.current().nextInt(JOIN_MESSAGES.size() - 1));
			message = message.replace("${random_join_message}", random);
		}
		if(invite != null){
			if(invite.getInviter() != null){
				message = message.replace("${inviter}", invite.getInviter().getAsMention());
			}
			message = message.replace("${invite_link}", invite.getUrl());
			message = message.replace("${invite_code}", invite.getCode());
			message = message.replace("${invite_uses}", String.valueOf(invite.getUses()));
		}
		return replacePlaceholders(message, user);
	}

	private static String generateLeaveMessage(String message, User user){
		if(LEAVE_MESSAGES != null && LEAVE_MESSAGES.size() > 1){
			var random = LEAVE_MESSAGES.get(ThreadLocalRandom.current().nextInt(LEAVE_MESSAGES.size() - 1));
			message = message.replace("${random_leave_message}", random);
		}
		return replacePlaceholders(message, user);
	}

	private static String replacePlaceholders(String message, User user){
		message = message.replace("${user}", user.getAsMention());
		message = message.replace("${user_tag}", user.getAsTag());
		message = message.replace("${name}", user.getName());
		return message;
	}

}
