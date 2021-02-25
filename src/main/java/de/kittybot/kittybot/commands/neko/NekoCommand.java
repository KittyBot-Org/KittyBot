package de.kittybot.kittybot.commands.neko;

import de.kittybot.kittybot.modules.RequestModule;
import de.kittybot.kittybot.modules.GuildSettingsModule;
import de.kittybot.kittybot.objects.enums.Neko;
import de.kittybot.kittybot.slashcommands.application.Category;
import de.kittybot.kittybot.slashcommands.application.CommandOptionChoice;
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
	public void run(Options options, Interaction ia){
		var neko = Neko.valueOf(options.getString("type"));
		if(neko.isNsfw()){
			if(ia.isFromGuild()){
				var guildIa = (GuildInteraction) ia;
				if(!ia.get(GuildSettingsModule.class).isNsfwEnabled(guildIa.getGuildId())){
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
