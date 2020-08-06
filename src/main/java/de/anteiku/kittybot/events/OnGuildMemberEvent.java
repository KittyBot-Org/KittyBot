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

	private static final String[] JOIN_MESSAGES = {"[username] just joined the server - glhf!", "[username] just joined. Everyone, look busy!", "[username] just joined. Can I get a heal?", "[username] joined your party.", "[username] joined. You must construct additional pylons.", "Ermagherd. [username] is here.", "Welcome, [username]. Stay awhile and listen.", "Welcome, [username]. We were expecting you ( ͡° ͜ʖ ͡°)", "Welcome, [username]. We hope you brought pizza.", "Welcome [username]. Leave your weapons by the door.", "A wild [username] appeared.", "Swoooosh. [username] just landed.", "Brace yourselves. [username] just joined the server.", "[username] just joined. Hide your bananas.", "[username] just arrived. Seems OP - please nerf.", "[username] just slid into the server.", "A [username] has spawned in the server.", "Big [username] showed up!", "Where’s [username]? In the server!", "[username] hopped into the server. Kangaroo!!", "[username] just showed up. Hold my beer.", "Challenger approaching - [username] has appeared!", "It's a bird! It's a plane! Nevermind, it's just [username].", "It's [username]! Praise the sun! \\\\[T]/", "Never gonna give [username] up. Never gonna let [username] down.", "Ha! [username] has joined! You activated my trap card!", "Cheers, love! [username]'s here!", "Hey! Listen! [username] has joined!", "We've been expecting you [username]", "It's dangerous to go alone, take [username]!", "[username] has joined the server! It's super effective!", "Cheers, love! [username] is here!", "[username] is here, as the prophecy foretold.", "[username] has arrived. Party's over.", "Ready player [username]", "[username] is here to kick butt and chew bubblegum. And [username] is all out of gum.", "Hello. Is it [username] you're looking for?", "[username] has joined. Stay a while and listen!", "Roses are red, violets are blue, [username] joined this server with you"};

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
