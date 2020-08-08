package de.anteiku.kittybot.commands.commands;

import de.anteiku.kittybot.KittyBot;
import de.anteiku.kittybot.commands.ACommand;
import de.anteiku.kittybot.commands.CommandContext;
import de.anteiku.kittybot.objects.Cache;
import de.anteiku.kittybot.objects.Emotes;
import de.anteiku.kittybot.objects.ReactiveMessage;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;

import java.util.Arrays;
import java.util.List;

public class NekoCommand extends ACommand{

	private static final String stringNekos = "femdom, tickle, classic, ngif, erofeet, meow, erok, poke, les, v3, hololewd, lewdk, keta, feetg, nsfw_neko_gif, eroyuri, kiss, 8ball, kuni, tits, pussy_jpg, cum_jpg, pussy, lewdkemo, lizard, slap, lewd, cum, cuddle, spank, goose, Random_hentai_gif, avatar, fox_girl, nsfw_avatar, hug, gecg, boobs, pat, feet, smug, kemonomimi, solog, holo, wallpaper, bj, woof, yuri, trap, anal, baka, blowjob, holoero, feed, neko, gasm, hentai, futanari, ero, solo, waifu, pwankg, eron, erokemo";
	private static final List<String> nekos = Arrays.asList(stringNekos.split(", "));
	public static final String COMMAND = "neko";
	public static final String USAGE = "neko <" + stringNekos + ">";
	public static final String DESCRIPTION = "Sends a random/specified neko";
	protected static final String[] ALIAS = {};

	public NekoCommand(){
		super(COMMAND, USAGE, DESCRIPTION, ALIAS);
	}

	@Override
	public void run(CommandContext ctx){
		if(!ctx.getChannel().isNSFW()){
			sendError(ctx, "Sorry but this command can only be used in nsfw channels");
			return;
		}
		if(ctx.getArgs().length > 0 && nekos.contains(ctx.getArgs()[0])){
			image(ctx, getNeko(ctx.getArgs()[0])).queue(message -> {
				Cache.addReactiveMessage(ctx, message, this, "-1");
				message.addReaction(Emotes.WASTEBASKET.get()).queue();
			});
		}
		else{
			sendUsage(ctx);
		}
	}

	@Override
	public void reactionAdd(ReactiveMessage reactiveMessage, GuildMessageReactionAddEvent event){
		super.reactionAdd(reactiveMessage, event);
	}

}
