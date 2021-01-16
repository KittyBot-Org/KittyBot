package de.kittybot.kittybot.main.commands.utility;

import de.kittybot.kittybot.command.old.Args;
import de.kittybot.kittybot.command.Category;
import de.kittybot.kittybot.command.old.Command;
import de.kittybot.kittybot.command.old.CommandContext;
import de.kittybot.kittybot.modules.CommandModule;
import de.kittybot.kittybot.modules.RequestModule;
import de.kittybot.kittybot.objects.Language;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.utils.MarkdownSanitizer;

import java.util.Arrays;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class TranslateCommand extends Command{

	public TranslateCommand(){
		super("translate", "Translates text to a given language", Category.UTILITIES);
		addAliases("tran", "tr");
		setUsage("<language> <text>");
	}

	@Override
	public void run(Args args, CommandContext ctx){
		if(args.isEmpty()){
			ctx.sendUsage(this);
			return;
		}
		if(args.isHelp(0) || args.is(0, "languages")){
			var langs = Arrays.stream(Language.values()).map(Language::getShortname).collect(Collectors.joining(", "));
			ctx.sendSuccess("Following languages are supported:\n```\n" + langs + "\n```");
			return;
		}
		var lang = Arrays.stream(Language.values()).filter(l -> l.getShortname().equalsIgnoreCase(args.get(0)) || l.getName().equalsIgnoreCase(args.get(0))).findFirst();
		if(lang.isEmpty()){
			ctx.sendError("I couldn't find a language named '``" + args.get(0) + "``'");
			return;
		}
		var text = ctx.getRawMessage().split(CommandModule.ARGUMENT_REGEX, 2)[1];
		if(text.isBlank()){
			ctx.sendError("Please provide text to translate");
			return;
		}
		ctx.get(RequestModule.class).translateText(text, lang.get().getShortname(), translatedText -> {
			if(translatedText == null){
				ctx.sendError("Error while trying to translate text");
				return;
			}
			ctx.sendSuccess(new EmbedBuilder().setDescription("Translated text to `" + lang.get().getName() + "`:\n```\n" + MarkdownSanitizer.escape(translatedText) + "\n```"));
		});
	}

}
