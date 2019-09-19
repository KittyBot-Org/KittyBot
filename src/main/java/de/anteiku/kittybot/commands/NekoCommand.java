package de.anteiku.kittybot.commands;

import de.anteiku.kittybot.API;
import de.anteiku.kittybot.Emotes;
import de.anteiku.kittybot.KittyBot;
import de.anteiku.kittybot.Logger;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionAddEvent;

import java.awt.*;
import java.util.Arrays;
import java.util.List;

public class NekoCommand extends Command{

	public static final String rawNekos = "femdom, tickle, classic, ngif, erofeet, meow, erok, poke, les, v3, hololewd, nekoapi_v3.1, lewdk, keta, feetg, nsfw_neko_gif, eroyuri, kiss, 8ball, kuni, tits, pussy_jpg, cum_jpg, pussy, lewdkemo, lizard, slap, lewd, cum, cuddle, spank, smallboobs, goose, Random_hentai_gif, avatar, fox_girl, nsfw_avatar, hug, gecg, boobs, pat, feet, smug, kemonomimi, solog, holo, wallpaper, bj, woof, yuri, trap, anal, baka, blowjob, holoero, feed, neko, gasm, hentai, futanari, ero, solo, waifu, pwankg, eron, erokemo";
	public static final List<String> nekos = Arrays.asList(rawNekos.split(", "));
	public static String COMMAND = "neko";
	public static String USAGE = "neko <" + rawNekos + ">";
	public static String DESCRIPTION = "Sends a random/specified neko";
	public static String[] ALIAS = {};

	public NekoCommand(KittyBot main){
		super(main, COMMAND, USAGE, DESCRIPTION, ALIAS);
	}

	@Override
	public void reactionAdd(Message command, GuildMessageReactionAddEvent event){
		super.reactionAdd(command, event);
	}

	@Override
	public void run(String[] args, GuildMessageReceivedEvent event){
		if(! event.getChannel().isNSFW()){
			sendError(event.getChannel(), "This is not a nsfw Channel!");
			return;
		}
		if(args.length > 0){
			if(nekos.contains(args[0])){
				try{
					String url = getNeko(args[0]);
					Message message = sendImage(event.getChannel(), url);

					main.commandManager.addListenerCmd(message, event.getMessage(), this, - 1L);
					message.addReaction(Emotes.WASTEBASKET.get()).queue();
				}
				catch(Exception e){
					sendError(event.getChannel(), "No neko found!");
					Logger.error(e);
				}
			}
			else{
				sendUsage(event.getChannel());
			}
		}
		else{
			sendUsage(event.getChannel());
		}
	}

}
