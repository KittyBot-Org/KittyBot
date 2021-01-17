package de.kittybot.kittybot.commands.utility;

import de.kittybot.kittybot.command.Category;
import de.kittybot.kittybot.command.application.Command;
import de.kittybot.kittybot.command.application.RunnableCommand;
import de.kittybot.kittybot.command.context.CommandContext;
import de.kittybot.kittybot.command.interaction.Options;
import de.kittybot.kittybot.command.options.CommandOptionString;
import de.kittybot.kittybot.modules.CommandModule;
import de.kittybot.kittybot.modules.RequestModule;
import de.kittybot.kittybot.objects.Language;
import de.kittybot.kittybot.utils.Colors;
import de.kittybot.kittybot.utils.Config;
import de.kittybot.kittybot.utils.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDAInfo;
import net.dv8tion.jda.api.utils.MarkdownSanitizer;

import java.lang.management.ManagementFactory;
import java.time.Instant;

@SuppressWarnings("unused")
public class TranslateCommand extends Command implements RunnableCommand{

	public TranslateCommand(){
		super("translate", "Translates text to a given language", Category.UTILITIES);
		addOptions(
				new CommandOptionString("to-language", "The target language").required(),
				new CommandOptionString("text", "The text to translate").required(),
				new CommandOptionString("from-language", "The source language")
		);
	}

	@Override
	public void run(Options options, CommandContext ctx){
		var toLang = Language.getFromName(options.getString("to-language"));
		var fromLang = options.has("from-language") ? Language.getFromName(options.getString("from-language")) : Language.AUTO;
		if(toLang == Language.UNKNOWN || fromLang == Language.UNKNOWN){
			ctx.error("Invalid language entered");
			return;
		}
		if(toLang == fromLang){
			ctx.error("From lang can not be same as to lang");
			return;
		}
		var text = options.getString("text");
		if(text.isBlank()){
			ctx.error("Please provide text to translate");
			return;
		}
		ctx.get(RequestModule.class).translateText(text, fromLang, toLang, translatedText -> {
			if(translatedText == null){
				ctx.error("Error while trying to translate text");
				return;
			}
			ctx.reply(new EmbedBuilder().setDescription("Translated text to `" + toLang.getName() + "`:\n```\n" + MarkdownSanitizer.escape(translatedText) + "\n```"));
		});
	}

}
