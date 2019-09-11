package de.anteiku.kittybot.messages;

import de.anteiku.kittybot.KittyBot;
import net.dv8tion.jda.core.entities.User;

public class Messages{
	
	protected static final String[] JOINMESSAGES = {"[username] just joined the server - glhf!", "[username] just joined. Everyone, look busy!", "[username] just joined. Can I get a heal?", "[username] joined your party.", "[username] joined. You must construct additional pylons.", "Ermagherd. [username] is here.", "Welcome, [username]. Stay awhile and listen.", "Welcome, [username]. We were expecting you ( ͡° ͜ʖ ͡°)", "Welcome, [username]. We hope you brought pizza.", "Welcome [username]. Leave your weapons by the door.", "A wild [username] appeared.", "Swoooosh. [username] just landed.", "Brace yourselves. [username] just joined the server.", "[username] just joined. Hide your bananas.", "[username] just arrived. Seems OP - please nerf.", "[username] just slid into the server.", "A [username] has spawned in the server.", "Big [username] showed up!", "Where’s [username]? In the server!", "[username] hopped into the server. Kangaroo!!", "[username] just showed up. Hold my beer.", "Challenger approaching - [username] has appeared!", "It's a bird! It's a plane! Nevermind, it's just [username].", "It's [username]! Praise the sun! \\\\[T]/", "Never gonna give [username] up. Never gonna let [username] down.", "Ha! [username] has joined! You activated my trap card!", "Cheers, love! [username]'s here!", "Hey! Listen! [username] has joined!", "We've been expecting you [username]", "It's dangerous to go alone, take [username]!", "[username] has joined the server! It's super effective!", "Cheers, love! [username] is here!", "[username] is here, as the prophecy foretold.", "[username] has arrived. Party's over.", "Ready player [username]", "[username] is here to kick butt and chew bubblegum. And [username] is all out of gum.", "Hello. Is it [username] you're looking for?", "[username] has joined. Stay a while and listen!", "Roses are red, violets are blue, [username] joined this server with you"};
	
	public static String generateJoinMessage(String message, User user){
		String random = Messages.JOINMESSAGES[KittyBot.rand.nextInt(Messages.JOINMESSAGES.length - 1)];
		message = message.replace("[randomwelcomemessage]", random);
		
		message = message.replace("[username]", user.getAsMention());
		return message;
	}
	
}
