package de.kittybot.kittybot.commands.neko;

import de.kittybot.kittybot.modules.RequestModule;
import de.kittybot.kittybot.modules.SettingsModule;
import de.kittybot.kittybot.objects.enums.Neko;
import de.kittybot.kittybot.slashcommands.application.Category;
import de.kittybot.kittybot.slashcommands.application.Command;
import de.kittybot.kittybot.slashcommands.application.RunnableCommand;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionInteger;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionString;
import de.kittybot.kittybot.slashcommands.context.CommandContext;
import de.kittybot.kittybot.slashcommands.context.Options;
import de.kittybot.kittybot.utils.Colors;
import net.dv8tion.jda.api.EmbedBuilder;

@SuppressWarnings("unused")
public class NekoCommand extends Command implements RunnableCommand{

	public NekoCommand(){
		super("neko", "Sends a random image/gif from a specific category", Category.NEKO);
		addOptions(
			new CommandOptionString("type", "The image type")
				.required()
				.addChoices(
					new NekoCommandOptionChoice(Neko.ANAL),
					new NekoCommandOptionChoice(Neko.BLOWJOB),
					new NekoCommandOptionChoice(Neko.CUM),
					new NekoCommandOptionChoice(Neko.FUCK),
					new NekoCommandOptionChoice(Neko.NEKO_NSFW),
					new NekoCommandOptionChoice(Neko.NEKO_NSFW_GIF),
					new NekoCommandOptionChoice(Neko.PUSSY_LICK),
					new NekoCommandOptionChoice(Neko.SOLO),
					new NekoCommandOptionChoice(Neko.THREESOME_FFF),
					new NekoCommandOptionChoice(Neko.THREESOME_FFM),
					new NekoCommandOptionChoice(Neko.THREESOME_MMF),
					new NekoCommandOptionChoice(Neko.YAOI),
					new NekoCommandOptionChoice(Neko.YURI),
					new NekoCommandOptionChoice(Neko.KITSUNE),
					new NekoCommandOptionChoice(Neko.SENKO),
					new NekoCommandOptionChoice(Neko.TAIL),
					new NekoCommandOptionChoice(Neko.FLUFF),
					new NekoCommandOptionChoice(Neko.EEVEE),
					new NekoCommandOptionChoice(Neko.EEVEE_GIF),
					new NekoCommandOptionChoice(Neko.NEKO_SFW),
					new NekoCommandOptionChoice(Neko.NEKO_SFW_GIF)
				)
		);
	}

	@Override
	public void run(Options options, CommandContext ctx){
		var nekoType = options.getString("type");
		var neko = Neko.valueOf(nekoType);
		if(neko.isNsfw()){
			if(!ctx.get(SettingsModule.class).isNsfwEnabled(ctx.getGuildId())){
				ctx.error("NSFW commands are disabled in this guild");
				return;
			}
			if(!ctx.getChannel().isNSFW()){
				ctx.error("This command is nsfw channel only");
				return;
			}
		}
		ctx.reply(new EmbedBuilder()
			.setColor(Colors.KITTYBOT_BLUE)
			.setImage(ctx.get(RequestModule.class).getNeko(neko))
		);
	}

}
