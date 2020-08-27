package de.anteiku.kittybot.commands.info;

import de.anteiku.kittybot.command.ACommand;
import de.anteiku.kittybot.command.Category;
import de.anteiku.kittybot.command.CommandContext;
import de.anteiku.kittybot.objects.Cache;
import de.anteiku.kittybot.objects.Config;
import de.anteiku.kittybot.objects.Emojis;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;

public class HelpCommand extends ACommand{

	public static final String COMMAND = "help";
	public static final String USAGE = "help";
	public static final String DESCRIPTION = "Shows some help stuff";
	protected static final String[] ALIASES = { "?" };
	protected static final Category CATEGORY = Category.INFORMATIVE;

	public HelpCommand(){
		super(COMMAND, USAGE, DESCRIPTION, ALIASES, CATEGORY);
	}

	@Override public void run(CommandContext ctx){
		answer(ctx, new EmbedBuilder().setColor(Color.orange)
				.setThumbnail(ctx.getJDA().getSelfUser().getEffectiveAvatarUrl())
				.addField(Emojis.INVITE + " Invite:", Emojis.BLANK + " :small_blue_diamond: You want me on your server? Click [here](" + Config.INVITE_URL + ") to invite me!", false)
				.addField(Emojis.CONSOLE + " Commands:", Emojis.BLANK + " :small_blue_diamond: You want to see **all my available commands**?\n" + Emojis.BLANK + " " + Emojis.BLANK + " Use ``.commands``", false)
				.addField(":question: Help:", Emojis.BLANK + " :small_blue_diamond: You want to **report bugs or suggest new features**?\n" + Emojis.BLANK + " " + Emojis.BLANK + " Message my owner on " + Emojis.TWITTER + " [Twitter](https://twitter.com/TopiSenpai) or " + Emojis.DISCORD + " [/home/toπ#3141](https://discord.com/users/170939974227591168)!", false))
				.queue(message -> {
					Cache.addReactiveMessage(ctx, message, this, ctx.getUser().getId());
					message.addReaction(Emojis.WASTEBASKET).queue();
				});
	}

}
