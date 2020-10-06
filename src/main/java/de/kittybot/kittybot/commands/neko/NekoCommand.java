package de.kittybot.kittybot.commands.neko;

import de.kittybot.kittybot.cache.ReactiveMessageCache;
import de.kittybot.kittybot.objects.Emojis;
import de.kittybot.kittybot.objects.command.ACommand;
import de.kittybot.kittybot.objects.command.Category;
import de.kittybot.kittybot.objects.command.CommandContext;

import java.util.Set;

public class NekoCommand extends ACommand{

	public static final String COMMAND = "neko";
	public static final String DESCRIPTION = "Sends a random/specified neko";
	protected static final String[] ALIASES = {};
	protected static final Category CATEGORY = Category.NEKO;
	private static final String STRING_NEKOS = "femdom, tickle, classic, ngif, erofeet, meow, erok, poke, les, v3, hololewd, lewdk, keta, feetg, nsfw_neko_gif, eroyuri, kiss, 8ball, kuni, tits, pussy_jpg, cum_jpg, pussy, lewdkemo, lizard, slap, lewd, cum, cuddle, spank, goose, Random_hentai_gif, avatar, fox_girl, nsfw_avatar, hug, gecg, boobs, pat, feet, smug, kemonomimi, solog, holo, wallpaper, bj, woof, yuri, trap, anal, baka, blowjob, holoero, feed, neko, gasm, hentai, futanari, ero, solo, waifu, pwankg, eron, erokemo";
	public static final String USAGE = "neko <" + STRING_NEKOS + ">";
	private static final Set<String> NEKOS = Set.of(STRING_NEKOS.split(", "));

	public NekoCommand(){
		super(COMMAND, USAGE, DESCRIPTION, ALIASES, CATEGORY);
	}

	@Override
	public void run(CommandContext ctx){
		if(!ctx.getChannel().isNSFW()){
			sendError(ctx, "Sorry but this command can only be used in nsfw channels");
			return;
		}
		if(ctx.getArgs().length > 0 && NEKOS.contains(ctx.getArgs()[0])){
			image(ctx, getNeko(ctx.getArgs()[0])).queue(message -> {
				ReactiveMessageCache.addReactiveMessage(ctx, message, this, "-1");
				message.addReaction(Emojis.WASTEBASKET).queue();
			});
		}
		else{
			sendUsage(ctx);
		}
	}

}
