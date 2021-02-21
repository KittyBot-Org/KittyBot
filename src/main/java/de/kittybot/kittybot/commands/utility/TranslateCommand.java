package de.kittybot.kittybot.commands.utility;

import de.kittybot.kittybot.modules.RequestModule;
import de.kittybot.kittybot.objects.enums.Language;
import de.kittybot.kittybot.slashcommands.application.Category;
import de.kittybot.kittybot.slashcommands.application.RunCommand;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionString;
import de.kittybot.kittybot.slashcommands.interaction.Interaction;
import de.kittybot.kittybot.slashcommands.interaction.Options;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.utils.MarkdownSanitizer;

@SuppressWarnings("unused")
public class TranslateCommand extends RunCommand{

	public TranslateCommand(){
		super("translate", "Translates text to a given language", Category.UTILITIES);
		addOptions(
			new CommandOptionString("to-language", "The target language").required(),
			new CommandOptionString("text", "The text to translate").required(),
			new CommandOptionString("from-language", "The source language")
		);
	}

	@Override
	public void run(Options options, Interaction ia){
		var toLang = Language.getFromName(options.getString("to-language"));
		var fromLang = options.has("from-language") ? Language.getFromName(options.getString("from-language")) : Language.AUTO;
		if(toLang == Language.UNKNOWN || fromLang == Language.UNKNOWN){
			ia.error("Invalid language entered");
			return;
		}
		if(toLang == fromLang){
			ia.error("From lang can not be same as to lang");
			return;
		}
		var text = options.getString("text");
		if(text.isBlank()){
			ia.error("Please provide text to translate");
			return;
		}
		ia.get(RequestModule.class).translateText(text, fromLang, toLang, translatedText -> {
			if(translatedText == null){
				ia.error("Error while trying to translate text");
				return;
			}
			ia.reply(new EmbedBuilder().setDescription("Translated text to `" + toLang.getName() + "`:\n```\n" + MarkdownSanitizer.escape(translatedText) + "\n```"));
		});
	}

}
