package de.kittybot.kittybot.main.commands.utility;

import de.kittybot.kittybot.command.old.Args;
import de.kittybot.kittybot.command.Category;
import de.kittybot.kittybot.command.old.Command;
import de.kittybot.kittybot.command.old.CommandContext;
import net.dv8tion.jda.api.entities.Message;

@SuppressWarnings("unused")
public class DownloadEmotesCommand extends Command{

	public DownloadEmotesCommand(){
		super("downloademotes", "Prints a ssh command to download the given emotes", Category.UTILITIES);
		setUsage("<Emote, Emote, ...>");
		addAliases("dle", "dlemotes");
	}

	@Override
	public void run(Args args, CommandContext ctx){
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
