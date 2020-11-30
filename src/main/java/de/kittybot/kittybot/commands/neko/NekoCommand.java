package de.kittybot.kittybot.commands.neko;

import de.kittybot.kittybot.objects.command.ACommand;
import de.kittybot.kittybot.objects.command.Category;
import de.kittybot.kittybot.objects.command.CommandContext;
import de.kittybot.kittybot.objects.requests.Requester;

import java.util.Arrays;

public class NekoCommand extends ACommand{

	public static final String COMMAND = "neko";
	public static final String DESCRIPTION = "Sends a random/specified neko";
	protected static final String[] ALIASES = {};
	protected static final Category CATEGORY = Category.NEKO;
	private static final String[] TYPES = {"neko", "anal", "blowjob", "cum", "fuck", "pussylick", "solo", "threesome_fff", "threesome_ffm", "threesome_mmf", "yaoi", "yuri"};
	private static final String TYPE_STRING = String.join("/", TYPES);
	public static final String USAGE = "neko <" + TYPE_STRING + "> <img/gif>";

	public NekoCommand(){
		super(COMMAND, USAGE, DESCRIPTION, ALIASES, CATEGORY);
	}

	@Override
	public void run(CommandContext ctx){
		if(!ctx.getChannel().isNSFW()){
			sendError(ctx, "This command is NSFW channel only");
			return;
		}
		var args = ctx.getArgs();
		var type = "neko";
		if(args.length > 0 && Arrays.stream(TYPES).anyMatch(args[0]::equalsIgnoreCase)){
			type = args[0].toLowerCase();
		}
		queue(ctx, image(ctx, Requester.getNeko(true, type, "gif")));
	}

}
