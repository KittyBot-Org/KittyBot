package de.kittybot.kittybot.commands.neko;

import de.kittybot.kittybot.modules.RequestModule;
import de.kittybot.kittybot.modules.SettingsModule;
import de.kittybot.kittybot.objects.enums.Neko;
import de.kittybot.kittybot.slashcommands.application.Category;
import de.kittybot.kittybot.slashcommands.application.RunCommand;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionString;
import de.kittybot.kittybot.slashcommands.interaction.GuildInteraction;
import de.kittybot.kittybot.slashcommands.interaction.Interaction;
import de.kittybot.kittybot.slashcommands.interaction.Options;
import de.kittybot.kittybot.utils.Colors;
import net.dv8tion.jda.api.EmbedBuilder;

@SuppressWarnings("unused")
public class NekoCommand extends RunCommand{

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
	public void run(Options options, Interaction ia){
		var nekoType = options.getString("type");
		var neko = Neko.valueOf(nekoType);
		if(neko.isNsfw()){
			if(ia.isFromGuild()){
				var guildIa = (GuildInteraction) ia;
				if(!ia.get(SettingsModule.class).isNsfwEnabled(guildIa.getGuildId())){
					ia.error("NSFW commands are disabled in this guild");
					return;
				}
				if(!guildIa.getChannel().isNSFW()){
					ia.error("This command is nsfw channel only");
					return;
				}
			}
		}
		ia.reply(new EmbedBuilder()
			.setColor(Colors.KITTYBOT_BLUE)
			.setImage(ia.get(RequestModule.class).getNeko(neko))
		);
	}

}
