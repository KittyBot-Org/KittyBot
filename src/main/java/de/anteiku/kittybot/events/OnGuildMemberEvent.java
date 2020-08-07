package de.anteiku.kittybot.events;

import de.anteiku.kittybot.KittyBot;
import de.anteiku.kittybot.database.Database;
import de.anteiku.kittybot.objects.Cache;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Invite;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdateBoostTimeEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class OnGuildMemberEvent extends ListenerAdapter{

	private static final String[] JOIN_MESSAGES = {"${user} just joined the server - glhf!", "${user} just joined. Everyone, look busy!", "${user} just joined. Can I get a heal?", "${user} joined your party.", "${user} joined. You must construct additional pylons.", "Ermagherd. ${user} is here.", "Welcome, ${user}. Stay awhile and listen.", "Welcome, ${user}. We were expecting you ( ͡° ͜ʖ ͡°)", "Welcome, ${user}. We hope you brought pizza.", "Welcome ${user}. Leave your weapons by the door.", "A wild ${user} appeared.", "Swoooosh. ${user} just landed.", "Brace yourselves. ${user} just joined the server.", "${user} just joined. Hide your bananas.", "${user} just arrived. Seems OP - please nerf.", "${user} just slid into the server.", "A ${user} has spawned in the server.", "Big ${user} showed up!", "Where’s ${user}? In the server!", "${user} hopped into the server. Kangaroo!!", "${user} just showed up. Hold my beer.", "Challenger approaching - ${user} has appeared!", "It's a bird! It's a plane! Nevermind, it's just ${user}.", "It's ${user}! Praise the sun! \\\\[T]/", "Never gonna give ${user} up. Never gonna let ${user} down.", "Ha! ${user} has joined! You activated my trap card!", "Cheers, love! ${user}'s here!", "Hey! Listen! ${user} has joined!", "We've been expecting you ${user}", "It's dangerous to go alone, take ${user}!", "${user} has joined the server! It's super effective!", "Cheers, love! ${user} is here!", "${user} is here, as the prophecy foretold.", "${user} has arrived. Party's over.", "Ready player ${user}", "${user} is here to kick butt and chew bubblegum. And ${user} is all out of gum.", "Hello. Is it ${user} you're looking for?", "${user} has joined. Stay a while and listen!", "Roses are red, violets are blue, ${user} joined this server with you"};

	@Override
	public void onGuildMemberRemove(GuildMemberRemoveEvent event){
		//TODO
	}

	@Override
	public void onGuildMemberJoin(GuildMemberJoinEvent event){
		String id = Database.getWelcomeChannelId(event.getGuild().getId());
		if(!id.equals("-1") && Database.getWelcomeMessageEnabled(event.getGuild().getId())){
			TextChannel channel = event.getGuild().getTextChannelById(id);
			if(channel != null){
				if(event.getGuild().getSelfMember().hasPermission(channel, Permission.MESSAGE_WRITE)){
					channel.sendMessage(generateJoinMessage(Database.getWelcomeMessage(event.getGuild().getId()), event.getUser(), Cache.getUsedInvite(event.getGuild()))).queue();
				}
				else{
					event.getGuild().retrieveOwner().queue(
							member -> member.getUser().openPrivateChannel().queue(
									success -> success.sendMessage("I lack the permission to send welcome messages to " + channel.getAsMention() + ".\n" +
											"You can disable them with `options welcomemessage off` if you don't like them").queue()
							)
					);
				}

			}
		}
	}

	@Override
	public void onGuildMemberUpdateBoostTime(GuildMemberUpdateBoostTimeEvent event){
		//TODO
	}

	private String generateJoinMessage(String message, User user, Invite invite){
		String random = JOIN_MESSAGES[KittyBot.rand.nextInt(JOIN_MESSAGES.length - 1)];
		message = message.replace("${random_welcome_message}", random);
		if(invite != null){
			if(invite.getInviter() != null){
				message = message.replace("${inviter}", invite.getInviter().getAsMention());
			}
			message = message.replace("${invite_link}", invite.getUrl());
			message = message.replace("${invite_code}", invite.getCode());
			message = message.replace("${invite_uses}", String.valueOf(invite.getUses()));
		}
		message = message.replace("${user}", user.getAsMention());
		return message;
	}

}
