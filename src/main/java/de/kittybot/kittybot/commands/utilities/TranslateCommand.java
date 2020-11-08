package de.kittybot.kittybot.commands.utilities;

import de.kittybot.kittybot.objects.command.ACommand;
import de.kittybot.kittybot.objects.command.Category;
import de.kittybot.kittybot.objects.command.CommandContext;
import de.kittybot.kittybot.objects.requests.Requester;

import java.util.Arrays;


public class TranslateCommand extends ACommand{

	public static final String COMMAND = "translate";
	public static final String USAGE = "translate <language> <text>";
	public static final String DESCRIPTION = "";
	protected static final String[] ALIASES = {"tran"};
	protected static final Category CATEGORY = Category.UTILITIES;

	public TranslateCommand(){
		super(COMMAND, USAGE, DESCRIPTION, ALIASES, CATEGORY);
	}

	@Override
	public void run(CommandContext ctx){
		var args = ctx.getArgs();
		var text = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
		var translatedText = Requester.translateText(text, args[0]);
		sendSuccess(ctx, translatedText);
	}

}
