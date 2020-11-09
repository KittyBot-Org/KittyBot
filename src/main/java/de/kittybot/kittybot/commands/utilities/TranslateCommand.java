package de.kittybot.kittybot.commands.utilities;

import de.kittybot.kittybot.objects.Languages;
import de.kittybot.kittybot.objects.command.ACommand;
import de.kittybot.kittybot.objects.command.Category;
import de.kittybot.kittybot.objects.command.CommandContext;
import de.kittybot.kittybot.objects.requests.Requester;
import de.kittybot.kittybot.utils.Utils;
import net.dv8tion.jda.api.EmbedBuilder;

import java.util.Arrays;
import java.util.stream.Collectors;


public class TranslateCommand extends ACommand{

	public static final String COMMAND = "translate";
	public static final String USAGE = "translate <language> <text>";
	public static final String DESCRIPTION = "";
	protected static final String[] ALIASES = {"tran", "t"};
	protected static final Category CATEGORY = Category.UTILITIES;

	public TranslateCommand(){
		super(COMMAND, USAGE, DESCRIPTION, ALIASES, CATEGORY);
	}

	@Override
	public void run(CommandContext ctx){
		var args = ctx.getArgs();
		if(args.length == 0){
			sendUsage(ctx);
			return;
		}
		if(Utils.isHelp(args[0]) || args[0].equalsIgnoreCase("languages")){
			var langs = Arrays.stream(Languages.values()).map(Languages::getShortname).collect(Collectors.joining(", "));
			sendSuccess(ctx, "Following languages are supported:\n```\n" + langs + "\n```");
			return;
		}
		var lang = Arrays.stream(Languages.values()).filter(l -> l.getShortname().equalsIgnoreCase(args[0]) || l.getName().equalsIgnoreCase(args[0])).findFirst();
		if(lang.isEmpty()){
			sendError(ctx, "I couldn't find a language named '``" + args[0] + "``'");
			return;
		}
		var text = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
		if(text.isBlank()){
			sendError(ctx, "Please provide text to translate");
			return;
		}
		var translatedText = Requester.translateText(text, lang.get().getShortname());

		sendSuccess(ctx, new EmbedBuilder().setDescription("translated text:\n```\n" + translatedText + "\n```"));
	}

}
