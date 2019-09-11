package de.anteiku.kittybot.commands;

import de.anteiku.kittybot.Emotes;
import de.anteiku.kittybot.KittyBot;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionAddEvent;

import java.awt.*;

public class HelpCommand extends Command{
	
	public static String COMMAND = "help";
	public static String USAGE = "help";
	public static String DESCRIPTION = "Shows some help stuff";
	public static String[] ALIAS = {"?"};
	
	public HelpCommand(KittyBot main){
		super(main, COMMAND, USAGE, DESCRIPTION, ALIAS);
	}
	
	@Override
	public void reactionAdd(Message command, GuildMessageReactionAddEvent event){
		super.reactionAdd(command, event);
	}
	
	@Override
	public void run(String[] args, GuildMessageReceivedEvent event){
		EmbedBuilder eb = new EmbedBuilder();
		eb.setColor(Color.orange);
		eb.setThumbnail(event.getJDA().getSelfUser().getEffectiveAvatarUrl());
		eb.addField(Emotes.invite.getAsMention() + " Invite:", Emotes.blank.getAsMention() + " :small_blue_diamond: You want me on your server? Klick [here](https://discordapp.com/api/oauth2/authorize?client_id=587697058602025011&permissions=8&scope=bot) to invite me!", true);
		
		eb.addField(Emotes.console.getAsMention() + " Commands:", Emotes.blank.getAsMention() + " :small_blue_diamond: You want to see **all my available commands**?\n" + Emotes.blank.getAsMention() + " " + Emotes.blank.getAsMention() + " Use ``.commands``", true);
		
		eb.addField(":question: Help:", Emotes.blank.getAsMention() + " :small_blue_diamond: You want to **report bugs or suggest new features**?\n" + Emotes.blank.getAsMention() + " " + Emotes.blank.getAsMention() + " Message my owner on " + Emotes.twitter.getAsMention() + " [Twitter](https://Twitter.com/TopiDragneel) or " + Emotes.discord.getAsMention() + " ``Toπ#4184``!", true);
		
		eb.addField(":globe_with_meridians: Webinterface:", Emotes.blank.getAsMention() + " :small_blue_diamond: Click [here](http://anteiku.de/login) to login with discord and manage your guilds!.",true);
		Message message = event.getChannel().sendMessage(eb.build()).complete();
		
		main.commandManager.addListenerCmd(message, event.getMessage(), this, - 1L);
		
		message.addReaction(Emotes.WASTEBASKET).queue();
	}
	
}
