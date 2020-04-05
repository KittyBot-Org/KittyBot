package de.anteiku.kittybot.commands;

import de.anteiku.kittybot.KittyBot;
import de.anteiku.kittybot.utils.Emotes;
import de.anteiku.kittybot.utils.ReactiveMessage;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;

import java.awt.*;

public class HelpCommand extends ACommand{
	
	public static String COMMAND = "help";
	public static String USAGE = "help";
	public static String DESCRIPTION = "Shows some help stuff";
	protected static String[] ALIAS = {"?"};
	
	public HelpCommand(KittyBot main){
		super(main, COMMAND, USAGE, DESCRIPTION, ALIAS);
	}
	
	@Override
	public void run(String[] args, GuildMessageReceivedEvent event){
		EmbedBuilder eb = new EmbedBuilder();
		eb.setColor(Color.orange);
		eb.setThumbnail(event.getJDA().getSelfUser().getEffectiveAvatarUrl());
		eb.addField(Emotes.INVITE.get() + " Invite:", Emotes.BLANK.get() + " :small_blue_diamond: You want me on your server? Klick [here](https://discordapp.com/api/oauth2/authorize?client_id=587697058602025011&permissions=8&scope=bot) to invite me!", true);
		eb.addField(Emotes.CONSOLE.get() + " Commands:", Emotes.BLANK.get() + " :small_blue_diamond: You want to see **all my available commands**?\n" + Emotes.BLANK.get() + " " + Emotes.BLANK.get() + " Use ``.commands``", true);
		eb.addField(":question: Help:", Emotes.BLANK.get() + " :small_blue_diamond: You want to **report bugs or suggest new features**?\n" + Emotes.BLANK.get() + " " + Emotes.BLANK.get() + " Message my owner on " + Emotes.TWITTER.get() + " [Twitter](https://Twitter.com/TopiDragneel) or " + Emotes.DISCORD.get() + " ``Toπ#4184``!", true);
		eb.addField(":globe_with_meridians: Webinterface:", Emotes.BLANK.get() + " :small_blue_diamond: Click [here](http://anteiku.de/login) to login with discord and manage your guilds!.",true);
		Message message = sendAnswer(event.getMessage(), eb.build());
		
		main.commandManager.addReactiveMessage(event, message, this, "-1");
		message.addReaction(Emotes.WASTEBASKET.get()).queue();
	}
	
	@Override
	public void reactionAdd(ReactiveMessage reactiveMessage, GuildMessageReactionAddEvent event){
		super.reactionAdd(reactiveMessage, event);
	}
	
}
