package de.anteiku.kittybot.commands.commands;

import de.anteiku.kittybot.KittyBot;
import de.anteiku.kittybot.commands.ACommand;
import de.anteiku.kittybot.commands.CommandContext;
import de.anteiku.kittybot.objects.Cache;
import de.anteiku.kittybot.objects.Config;
import de.anteiku.kittybot.objects.Emotes;
import de.anteiku.kittybot.objects.ReactiveMessage;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;

import java.awt.*;

public class HelpCommand extends ACommand{

	public static final String COMMAND = "help";
	public static final String USAGE = "help";
	public static final String DESCRIPTION = "Shows some help stuff";
	protected static final String[] ALIAS = {"?"};

	public HelpCommand(){
		super(COMMAND, USAGE, DESCRIPTION, ALIAS);
	}

	@Override
	public void run(CommandContext ctx){
		answer(ctx, new EmbedBuilder().setColor(Color.orange).setThumbnail(ctx.getJDA().getSelfUser().getEffectiveAvatarUrl()).addField(Emotes.INVITE.get() + " Invite:", Emotes.BLANK.get() + " :small_blue_diamond: You want me on your server? Klick [here](" + Config.INVITE_LINK + ") to invite me!", false).addField(Emotes.CONSOLE.get() + " Commands:", Emotes.BLANK.get() + " :small_blue_diamond: You want to see **all my available commands**?\n" + Emotes.BLANK.get() + " " + Emotes.BLANK.get() + " Use ``.commands``", false).addField(":question: Help:", Emotes.BLANK.get() + " :small_blue_diamond: You want to **report bugs or suggest new features**?\n" + Emotes.BLANK.get() + " " + Emotes.BLANK.get() + " Message my owner on " + Emotes.TWITTER.get() + " [Twitter](https://Twitter.com/TopiDragneel) or " + Emotes.DISCORD.get() + " ``Toπ#4184``!", false)
				//.addField(":globe_with_meridians: Webinterface:", Emotes.BLANK.get() + " :small_blue_diamond: Click [here](http://anteiku.de/login) to login with discord and manage your guilds!.",false)
		).queue(message -> {
			Cache.addReactiveMessage(ctx, message, this, "-1");
			message.addReaction(Emotes.WASTEBASKET.get()).queue();
		});

	}

	@Override
	public void reactionAdd(ReactiveMessage reactiveMessage, GuildMessageReactionAddEvent event){
		super.reactionAdd(reactiveMessage, event);
	}

}
