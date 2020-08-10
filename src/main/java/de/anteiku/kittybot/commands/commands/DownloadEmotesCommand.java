package de.anteiku.kittybot.commands.commands;

import de.anteiku.kittybot.commands.ACommand;
import de.anteiku.kittybot.commands.CommandContext;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Message;

import java.util.List;

public class DownloadEmotesCommand extends ACommand{

	public static final String COMMAND = "downloademotes";
	public static final String USAGE = "downloademotes <Emote, Emote, ...>";
	public static final String DESCRIPTION = "Prints a ssh command to download the given emotes";
	protected static final String[] ALIAS = {"dle", "dlemotes"};

	public DownloadEmotesCommand(){
		super(COMMAND, USAGE, DESCRIPTION, ALIAS);
	}

	@Override
	public void run(CommandContext ctx){
		List<Emote> emotes = ctx.getMessage().getEmotes();
		if(!emotes.isEmpty()){
			StringBuilder links = new StringBuilder();
			for(Emote emote : emotes){
				String link = emote.getImageUrl();
				if(links.length() + (" -O " + link).length() > Message.MAX_CONTENT_LENGTH - 20){
					sendAnswer(ctx, "Command: \ncurl" + links.toString());
					links = new StringBuilder();
				}
				links.append(" -O ").append(link);
			}
			sendAnswer(ctx, "Command: \ncurl" + links.toString());
		}
		else{
			sendUsage(ctx);
		}
	}

}
