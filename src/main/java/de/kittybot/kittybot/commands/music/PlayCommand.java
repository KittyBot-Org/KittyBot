package de.kittybot.kittybot.commands.music;

import de.kittybot.kittybot.objects.command.ACommand;
import de.kittybot.kittybot.objects.command.Category;
import de.kittybot.kittybot.objects.command.CommandContext;
import de.kittybot.kittybot.objects.music.AudioLoader;
import de.kittybot.kittybot.utils.MusicUtils;

public class PlayCommand extends ACommand{

	public static final String COMMAND = "play";
	public static final String USAGE = "play <playlist/song/video>";
	public static final String DESCRIPTION = "Plays what you want Kitty to play";
	protected static final String[] ALIASES = {"p", "spiele"};
	protected static final Category CATEGORY = Category.MUSIC;

	public PlayCommand(){
		super(COMMAND, USAGE, DESCRIPTION, ALIASES, CATEGORY);
	}

	@Override
	public void run(CommandContext ctx){
		final var args = ctx.getArgs();
		if(args.length == 0){
			sendError(ctx, "Please provide a link or search term");
			return;
		}
		final var connectionFailure = MusicUtils.checkVoiceChannel(ctx);
		if(connectionFailure != null){
			sendError(ctx, "I can't play music as " + connectionFailure.getReason());
			return;
		}
		AudioLoader.loadQuery(ctx);
	}

}
