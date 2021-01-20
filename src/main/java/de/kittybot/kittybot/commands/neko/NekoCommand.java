package de.kittybot.kittybot.commands.neko;

import de.kittybot.kittybot.modules.RequestModule;
import de.kittybot.kittybot.modules.SettingsModule;
import de.kittybot.kittybot.objects.enums.Neko;
import de.kittybot.kittybot.slashcommands.application.Category;
import de.kittybot.kittybot.slashcommands.application.Command;
import de.kittybot.kittybot.slashcommands.application.RunnableCommand;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionInteger;
import de.kittybot.kittybot.slashcommands.context.CommandContext;
import de.kittybot.kittybot.slashcommands.context.Options;
import de.kittybot.kittybot.utils.Colors;
import net.dv8tion.jda.api.EmbedBuilder;

@SuppressWarnings("unused")
public class NekoCommand extends Command implements RunnableCommand{

	public NekoCommand(){
		super("neko", "Sends some nekos", Category.NEKO);
		addOptions(
			new CommandOptionInteger("type", "The neko type")
				.required()
				.addChoices(
					new NekoCommandOptionChoice(Neko.ANAL),
					new NekoCommandOptionChoice(Neko.BLOWJOB),
					new NekoCommandOptionChoice(Neko.CUM),
					new NekoCommandOptionChoice(Neko.FUCK),
					new NekoCommandOptionChoice(Neko.PUSSY_LICK),
					new NekoCommandOptionChoice(Neko.SOLO),
					new NekoCommandOptionChoice(Neko.THREESOME_FFF),
					new NekoCommandOptionChoice(Neko.THREESOME_FFM),
					//new NekoCommandOptionChoice(Neko.THREESOME_MMF),
					new NekoCommandOptionChoice(Neko.YAOI),
					new NekoCommandOptionChoice(Neko.YURI)
				)
		);
	}

	@Override
	public void run(Options options, CommandContext ctx){
		if(!ctx.get(SettingsModule.class).isNsfwEnabled(ctx.getGuildId())){
			ctx.error("NSFW commands are disabled in this guild");
			return;
		}
		if(!ctx.getChannel().isNSFW()){
			ctx.error("This command is nsfw channel only");
			return;
		}
		var nekoType = options.getInt("type");
		var neko = Neko.byId(nekoType);
		ctx.reply(new EmbedBuilder()
			.setColor(Colors.KITTYBOT_BLUE)
			.setImage(ctx.get(RequestModule.class).getNeko(neko.isNsfw(), neko.getName(), "gif"))
		);
	}

}
