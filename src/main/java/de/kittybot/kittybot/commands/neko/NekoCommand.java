package de.kittybot.kittybot.commands.neko;

import de.kittybot.kittybot.modules.RequestModule;
import de.kittybot.kittybot.modules.SettingsModule;
import de.kittybot.kittybot.objects.enums.Neko;
import de.kittybot.kittybot.slashcommands.CommandContext;
import de.kittybot.kittybot.slashcommands.GuildCommandContext;
import de.kittybot.kittybot.slashcommands.Options;
import de.kittybot.kittybot.slashcommands.application.Category;
import de.kittybot.kittybot.slashcommands.application.OptionChoice;
import de.kittybot.kittybot.slashcommands.application.RunCommand;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionString;

@SuppressWarnings("unused")
public class NekoCommand extends RunCommand{

	public NekoCommand(){
		super("neko", "Sends a random image/gif from a specific category", Category.NEKO);
		addOptions(
			new CommandOptionString("type", "The image type")
				.required()
				.addChoices(
					new OptionChoice(Neko.ANAL),
					new OptionChoice(Neko.BLOWJOB),
					new OptionChoice(Neko.CUM),
					new OptionChoice(Neko.FUCK),
					new OptionChoice(Neko.NEKO_NSFW),
					new OptionChoice(Neko.NEKO_NSFW_GIF),
					new OptionChoice(Neko.PUSSY_LICK),
					new OptionChoice(Neko.SOLO),
					new OptionChoice(Neko.THREESOME_FFF),
					new OptionChoice(Neko.THREESOME_FFM),
					new OptionChoice(Neko.THREESOME_MMF),
					new OptionChoice(Neko.YAOI),
					new OptionChoice(Neko.YURI),
					new OptionChoice(Neko.KITSUNE),
					new OptionChoice(Neko.SENKO),
					new OptionChoice(Neko.TAIL),
					new OptionChoice(Neko.FLUFF),
					new OptionChoice(Neko.EEVEE),
					new OptionChoice(Neko.EEVEE_GIF),
					new OptionChoice(Neko.NEKO_SFW),
					new OptionChoice(Neko.NEKO_SFW_GIF)
				)
		);
	}

	@Override
	public void run(Options options, CommandContext ctx){
		var neko = Neko.valueOf(options.getString("type"));
		if(neko.isNsfw()){
			if(ctx.isFromGuild()){
				var guildCtx = (GuildCommandContext) ctx;
				if(!ctx.get(SettingsModule.class).isNsfwEnabled(guildCtx.getGuildId())){
					ctx.error("NSFW commands are disabled in this guild");
					return;
				}
				if(!guildCtx.getChannel().isNSFW()){
					ctx.error("This command is nsfw channel only");
					return;
				}
			}
		}
		ctx.reply(builder -> builder
			.setImage(ctx.get(RequestModule.class).getNeko(neko))
		);
	}

}
