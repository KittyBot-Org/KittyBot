package de.anteiku.kittybot.commands.info;

import de.anteiku.kittybot.objects.Cache;
import de.anteiku.kittybot.objects.Config;
import de.anteiku.kittybot.objects.Emotes;
import de.anteiku.kittybot.objects.ReactiveMessage;
import de.anteiku.kittybot.objects.command.ACommand;
import de.anteiku.kittybot.objects.command.CommandContext;
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
		answer(ctx, new EmbedBuilder()
				.setColor(Color.orange)
				.setThumbnail(ctx.getJDA().getSelfUser().getEffectiveAvatarUrl())
				.addField(Emotes.INVITE.get() + " Invite:", Emotes.BLANK.get() + " :small_blue_diamond: You want me on your server? Click [here](" + Config.INVITE_URL + ") to invite me!", false)
				.addField(Emotes.CONSOLE.get() + " Commands:", Emotes.BLANK.get() + " :small_blue_diamond: You want to see **all my available commands**?\n" + Emotes.BLANK.get() + " " + Emotes.BLANK.get() + " Use ``.commands``", false)
				.addField(":question: Help:", Emotes.BLANK.get() + " :small_blue_diamond: You want to **report bugs or suggest new features**?\n" + Emotes.BLANK.get() + " " + Emotes.BLANK.get() + " Message my owner on " + Emotes.TWITTER.get() + " [Twitter](https://twitter.com/TopiSenpai) or " + Emotes.DISCORD.get() + " ``/home/toπ#3141``!", false)
		).queue(message -> {
			Cache.addReactiveMessage(ctx, message, this, ctx.getUser().getId());
			message.addReaction(Emotes.WASTEBASKET.get()).queue();
		});

	}

	@Override
	public void reactionAdd(ReactiveMessage reactiveMessage, GuildMessageReactionAddEvent event){
		super.reactionAdd(reactiveMessage, event);
	}

}
