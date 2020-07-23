package de.anteiku.kittybot.events;

import de.anteiku.kittybot.KittyBot;
import de.anteiku.kittybot.database.Database;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdateBoostTimeEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class OnGuildMemberEvent extends ListenerAdapter{

	private static final String[] JOIN_MESSAGES = {"[username] just joined the server - glhf!", "[username] just joined. Everyone, look busy!", "[username] just joined. Can I get a heal?", "[username] joined your party.", "[username] joined. You must construct additional pylons.", "Ermagherd. [username] is here.", "Welcome, [username]. Stay awhile and listen.", "Welcome, [username]. We were expecting you ( ͡° ͜ʖ ͡°)", "Welcome, [username]. We hope you brought pizza.", "Welcome [username]. Leave your weapons by the door.", "A wild [username] appeared.", "Swoooosh. [username] just landed.", "Brace yourselves. [username] just joined the server.", "[username] just joined. Hide your bananas.", "[username] just arrived. Seems OP - please nerf.", "[username] just slid into the server.", "A [username] has spawned in the server.", "Big [username] showed up!", "Where’s [username]? In the server!", "[username] hopped into the server. Kangaroo!!", "[username] just showed up. Hold my beer.", "Challenger approaching - [username] has appeared!", "It's a bird! It's a plane! Nevermind, it's just [username].", "It's [username]! Praise the sun! \\\\[T]/", "Never gonna give [username] up. Never gonna let [username] down.", "Ha! [username] has joined! You activated my trap card!", "Cheers, love! [username]'s here!", "Hey! Listen! [username] has joined!", "We've been expecting you [username]", "It's dangerous to go alone, take [username]!", "[username] has joined the server! It's super effective!", "Cheers, love! [username] is here!", "[username] is here, as the prophecy foretold.", "[username] has arrived. Party's over.", "Ready player [username]", "[username] is here to kick butt and chew bubblegum. And [username] is all out of gum.", "Hello. Is it [username] you're looking for?", "[username] has joined. Stay a while and listen!", "Roses are red, violets are blue, [username] joined this server with you"};

	private final KittyBot main;

	public OnGuildMemberEvent(KittyBot main){
		this.main = main;
	}

	@Override
	public void onGuildMemberJoin(GuildMemberJoinEvent event){
		String id = Database.getWelcomeChannelId(event.getGuild().getId());
		if(!id.equals("-1") && Database.getWelcomeMessageEnabled(event.getGuild().getId())){
			TextChannel channel = event.getGuild().getTextChannelById(id);
			if(channel != null){
				channel.sendMessage(generateJoinMessage(Database.getWelcomeMessage(event.getGuild().getId()), event.getUser())).queue();
			}
		}
	}

	@Override
	public void onGuildMemberRemove(GuildMemberRemoveEvent event){
		//TODO
	}

	@Override
	public void onGuildMemberUpdateBoostTime(GuildMemberUpdateBoostTimeEvent event){
		//TODO
	}

	private String generateJoinMessage(String message, User user){
		String random = JOIN_MESSAGES[main.rand.nextInt(JOIN_MESSAGES.length - 1)];
		message = message.replace("[randomwelcomemessage]", random);

		message = message.replace("[username]", user.getAsMention());
		return message;
	}

}
