package de.kittybot.kittybot.commands.utility;

import de.kittybot.kittybot.command.Category;
import de.kittybot.kittybot.command.Command;
import de.kittybot.kittybot.command.ctx.CommandContext;
import de.kittybot.kittybot.main.KittyBot;
import net.dv8tion.jda.api.entities.Message;

import java.util.List;

public class DownloadEmotesCommand extends Command{

	public DownloadEmotesCommand(KittyBot main){
		super("downloademotes", "Prints a ssh command to download the given emotes", Category.UTILITIES);
		setUsage("<Emote, Emote, ...>");
		addAliases("dle", "dlemotes");
	}

	@Override
	public void run(List<String> args, CommandContext ctx){
		var emotes = ctx.getMessage().getEmotes();
		if(emotes.isEmpty()){
			ctx.sendUsage(this);
			return;
		}
		var links = new StringBuilder();
		for(var emote : emotes){
			var link = emote.getImageUrl();
			if(links.length() + (" -O " + link).length() > Message.MAX_CONTENT_LENGTH - 20){
				ctx.sendSuccess("Command: \ncurl" + links);
				links = new StringBuilder();
			}
			links.append(" -O ").append(link);
		}
		ctx.sendSuccess("Command: \ncurl" + links);
	}

}
