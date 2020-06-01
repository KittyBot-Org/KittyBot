package de.anteiku.kittybot.commands.commands;

import de.anteiku.kittybot.KittyBot;
import de.anteiku.kittybot.commands.ACommand;
import de.anteiku.kittybot.utils.Config;
import de.anteiku.kittybot.utils.Emotes;
import de.anteiku.kittybot.utils.ReactiveMessage;
import net.dv8tion.jda.api.EmbedBuilder;
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
		answer(event, new EmbedBuilder().setColor(Color.orange).setThumbnail(event.getJDA().getSelfUser().getEffectiveAvatarUrl()).addField(Emotes.INVITE.get() + " Invite:", Emotes.BLANK.get() + " :small_blue_diamond: You want me on your server? Klick [here](" + Config.DISCORD_INVITE_LINK + ") to invite me!", false).addField(Emotes.CONSOLE.get() + " Commands:", Emotes.BLANK.get() + " :small_blue_diamond: You want to see **all my available commands**?\n" + Emotes.BLANK.get() + " " + Emotes.BLANK.get() + " Use ``.commands``", false).addField(":question: Help:", Emotes.BLANK.get() + " :small_blue_diamond: You want to **report bugs or suggest new features**?\n" + Emotes.BLANK.get() + " " + Emotes.BLANK.get() + " Message my owner on " + Emotes.TWITTER.get() + " [Twitter](https://Twitter.com/TopiDragneel) or " + Emotes.DISCORD.get() + " ``Toπ#4184``!", false)
				//.addField(":globe_with_meridians: Webinterface:", Emotes.BLANK.get() + " :small_blue_diamond: Click [here](http://anteiku.de/login) to login with discord and manage your guilds!.",false)
		).queue(message -> {
			main.commandManager.addReactiveMessage(event, message, this, "-1");
			message.addReaction(Emotes.WASTEBASKET.get()).queue();
		});

	}

	@Override
	public void reactionAdd(ReactiveMessage reactiveMessage, GuildMessageReactionAddEvent event){
		super.reactionAdd(reactiveMessage, event);
	}

}
