package de.kittybot.kittybot.commands.utilities;

import de.kittybot.kittybot.objects.command.ACommand;
import de.kittybot.kittybot.objects.command.Category;
import de.kittybot.kittybot.objects.command.CommandContext;
import net.dv8tion.jda.api.entities.Message;

public class DownloadEmotesCommand extends ACommand{

	public static final String COMMAND = "downloademotes";
	public static final String USAGE = "downloademotes <Emote, Emote, ...>";
	public static final String DESCRIPTION = "Prints a ssh command to download the given emotes";
	protected static final String[] ALIASES = {"dle", "dlemotes"};
	protected static final Category CATEGORY = Category.UTILITIES;

	public DownloadEmotesCommand(){
		super(COMMAND, USAGE, DESCRIPTION, ALIASES, CATEGORY);
	}

	@Override
	public void run(CommandContext ctx){
		var emotes = ctx.getMessage().getEmotes();
		if(emotes.isEmpty()){
			sendUsage(ctx);
			return;
		}
		var links = new StringBuilder();
		for(var emote : emotes){
			var link = emote.getImageUrl();
			if(links.length() + (" -O " + link).length() > Message.MAX_CONTENT_LENGTH - 20){
				sendSuccess(ctx, "Command: \ncurl" + links);
				links = new StringBuilder();
			}
			links.append(" -O ").append(link);
		}
		sendSuccess(ctx, "Command: \ncurl" + links);
	}

}
