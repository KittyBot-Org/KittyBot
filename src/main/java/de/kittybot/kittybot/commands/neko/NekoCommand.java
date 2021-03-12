package de.kittybot.kittybot.commands.neko;

import de.kittybot.kittybot.modules.RequestModule;
import de.kittybot.kittybot.modules.SettingsModule;
import de.kittybot.kittybot.objects.enums.Neko;
import de.kittybot.kittybot.slashcommands.CommandContext;
import de.kittybot.kittybot.slashcommands.GuildCommandContext;
import de.kittybot.kittybot.slashcommands.Options;
import de.kittybot.kittybot.slashcommands.application.Category;
import de.kittybot.kittybot.slashcommands.application.CommandOptionChoice;
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
					new CommandOptionChoice<>(Neko.ANAL),
					new CommandOptionChoice<>(Neko.BLOWJOB),
					new CommandOptionChoice<>(Neko.CUM),
					new CommandOptionChoice<>(Neko.FUCK),
					new CommandOptionChoice<>(Neko.NEKO_NSFW),
					new CommandOptionChoice<>(Neko.NEKO_NSFW_GIF),
					new CommandOptionChoice<>(Neko.PUSSY_LICK),
					new CommandOptionChoice<>(Neko.SOLO),
					new CommandOptionChoice<>(Neko.THREESOME_FFF),
					new CommandOptionChoice<>(Neko.THREESOME_FFM),
					new CommandOptionChoice<>(Neko.THREESOME_MMF),
					new CommandOptionChoice<>(Neko.YAOI),
					new CommandOptionChoice<>(Neko.YURI),
					new CommandOptionChoice<>(Neko.KITSUNE),
					new CommandOptionChoice<>(Neko.SENKO),
					new CommandOptionChoice<>(Neko.TAIL),
					new CommandOptionChoice<>(Neko.FLUFF),
					new CommandOptionChoice<>(Neko.EEVEE),
					new CommandOptionChoice<>(Neko.EEVEE_GIF),
					new CommandOptionChoice<>(Neko.NEKO_SFW),
					new CommandOptionChoice<>(Neko.NEKO_SFW_GIF)
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
