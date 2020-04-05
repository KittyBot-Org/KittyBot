package de.anteiku.kittybot.commands;

import de.anteiku.kittybot.KittyBot;
import de.anteiku.kittybot.utils.Emotes;
import de.anteiku.kittybot.utils.ReactiveMessage;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;

import java.util.Arrays;
import java.util.List;

public class NekoCommand extends ACommand{
	
	private static final String stringNekos = "femdom, tickle, classic, ngif, erofeet, meow, erok, poke, les, v3, hololewd, nekoapi_v3.1, lewdk, keta, feetg, nsfw_neko_gif, eroyuri, kiss, 8ball, kuni, tits, pussy_jpg, cum_jpg, pussy, lewdkemo, lizard, slap, lewd, cum, cuddle, spank, smallboobs, goose, Random_hentai_gif, avatar, fox_girl, nsfw_avatar, hug, gecg, boobs, pat, feet, smug, kemonomimi, solog, holo, wallpaper, bj, woof, yuri, trap, anal, baka, blowjob, holoero, feed, neko, gasm, hentai, futanari, ero, solo, waifu, pwankg, eron, erokemo";
	private static final List<String> nekos = Arrays.asList(stringNekos.split(", "));
	public static String COMMAND = "neko";
	public static String USAGE = "neko <" + stringNekos + ">";
	public static String DESCRIPTION = "Sends a random/specified neko";
	protected static String[] ALIAS = {};

	public NekoCommand(KittyBot main){
		super(main, COMMAND, USAGE, DESCRIPTION, ALIAS);
	}

	@Override
	public void reactionAdd(ReactiveMessage reactiveMessage, GuildMessageReactionAddEvent event){
		super.reactionAdd(reactiveMessage, event);
	}

	@Override
	public void run(String[] args, GuildMessageReceivedEvent event){
		if(! event.getChannel().isNSFW()){
			sendError(event.getMessage(), "Sorry but this command can only be used in nsfw channels");
			return;
		}
		if(args.length > 0 && nekos.contains(args[0])){
			Message message = sendImage(event.getMessage(), getNeko(args[0]));
			main.commandManager.addReactiveMessage(event, message, this, "-1");
			message.addReaction(Emotes.WASTEBASKET.get()).queue();
		}
		else{
			sendUsage(event.getMessage());
		}
	}

}
