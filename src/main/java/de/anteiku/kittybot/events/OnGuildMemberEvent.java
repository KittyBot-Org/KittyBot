package de.anteiku.kittybot.events;

import de.anteiku.kittybot.database.Database;
import de.anteiku.kittybot.objects.cache.InviteCache;
import de.anteiku.kittybot.utils.MessageUtils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Invite;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdateBoostTimeEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class OnGuildMemberEvent extends ListenerAdapter{

	private static final List<String> JOIN_MESSAGES = MessageUtils.loadMessageFile("join");
	private static final List<String> LEAVE_MESSAGES = MessageUtils.loadMessageFile("leave");
	private static final List<String> BOOST_MESSAGES = MessageUtils.loadMessageFile("boost");

	@Override
	public void onGuildMemberRemove(GuildMemberRemoveEvent event){
		String id = Database.getAnnouncementChannelId(event.getGuild().getId());
		if(!id.equals("-1") && Database.getLeaveMessageEnabled(event.getGuild().getId())){
			TextChannel channel = event.getGuild().getTextChannelById(id);
			if(channel != null){
				if(event.getGuild().getSelfMember().hasPermission(channel, Permission.MESSAGE_WRITE)){
					channel.sendMessage(generateLeaveMessage(Database.getLeaveMessage(event.getGuild().getId()), event.getUser())).queue();
				}
				else{
					event.getGuild()
							.retrieveOwner()
							.queue(member -> member.getUser()
									.openPrivateChannel()
									.queue(success -> success.sendMessage("I lack the permission to send leave messages to " + channel.getAsMention() + ".\n" + "You can disable them with `options leavemessage off` if you don't like them")
											.queue()));
				}
			}
		}
	}

	@Override
	public void onGuildMemberJoin(GuildMemberJoinEvent event){
		String id = Database.getAnnouncementChannelId(event.getGuild().getId());
		if(!id.equals("-1") && Database.getJoinMessageEnabled(event.getGuild().getId())){
			TextChannel channel = event.getGuild().getTextChannelById(id);
			if(channel != null){
				if(event.getGuild().getSelfMember().hasPermission(channel, Permission.MESSAGE_WRITE)){
					channel.sendMessage(generateJoinMessage(Database.getJoinMessage(event.getGuild().getId()), event.getUser(), InviteCache.getUsedInvite(event.getGuild())))
							.queue();
				}
				else{
					event.getGuild()
							.retrieveOwner()
							.queue(member -> member.getUser()
									.openPrivateChannel()
									.queue(success -> success.sendMessage("I lack the permission to send join messages to " + channel.getAsMention() + ".\n" + "You can disable them with `options joinmessage off` if you don't like them")
											.queue()));
				}
			}
		}
	}

	@Override
	public void onGuildMemberUpdateBoostTime(GuildMemberUpdateBoostTimeEvent event){
		String id = Database.getAnnouncementChannelId(event.getGuild().getId());
		if(!id.equals("-1") && Database.getLeaveMessageEnabled(event.getGuild().getId())){
			TextChannel channel = event.getGuild().getTextChannelById(id);
			if(channel != null){
				if(event.getGuild().getSelfMember().hasPermission(channel, Permission.MESSAGE_WRITE)){
					channel.sendMessage(generateBoostMessage(Database.getBoostMessage(event.getGuild().getId()), event.getUser())).queue();
				}
				else{
					event.getGuild()
							.retrieveOwner()
							.queue(member -> member.getUser()
									.openPrivateChannel()
									.queue(success -> success.sendMessage("I lack the permission to send boost messages to " + channel.getAsMention() + ".\n" + "You can disable them with `options boostmessages off` if you don't like them")
											.queue()));
				}
			}
		}
	}

	private String generateBoostMessage(String message, User user){
		if(BOOST_MESSAGES != null && BOOST_MESSAGES.size() > 1){
			String random = BOOST_MESSAGES.get(ThreadLocalRandom.current().nextInt(BOOST_MESSAGES.size() - 1));
			message = message.replace("${random_boost_message}", random);
		}
		message = message.replace("${user}", user.getAsMention());
		message = message.replace("${user_tag}", user.getAsTag());
		message = message.replace("${name}", user.getName());
		return message;
	}

	private String generateJoinMessage(String message, User user, Invite invite){
		if(JOIN_MESSAGES != null && JOIN_MESSAGES.size() > 1){
			String random = JOIN_MESSAGES.get(ThreadLocalRandom.current().nextInt(JOIN_MESSAGES.size() - 1));
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
		message = message.replace("${user}", user.getAsMention());
		message = message.replace("${user_tag}", user.getAsTag());
		message = message.replace("${name}", user.getName());
		return message;
	}

	private String generateLeaveMessage(String message, User user){
		if(LEAVE_MESSAGES != null && LEAVE_MESSAGES.size() > 1){
			String random = LEAVE_MESSAGES.get(ThreadLocalRandom.current().nextInt(LEAVE_MESSAGES.size() - 1));
			message = message.replace("${random_leave_message}", random);
		}
		message = message.replace("${user}", user.getAsMention());
		message = message.replace("${user_tag}", user.getAsTag());
		message = message.replace("${name}", user.getName());
		return message;
	}

}
