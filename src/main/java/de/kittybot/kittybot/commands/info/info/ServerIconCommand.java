package de.kittybot.kittybot.commands.info.info;

import de.kittybot.kittybot.slashcommands.application.CommandOptionChoice;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionInteger;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionLong;
import de.kittybot.kittybot.slashcommands.application.options.SubCommand;
import de.kittybot.kittybot.slashcommands.interaction.GuildInteraction;
import de.kittybot.kittybot.slashcommands.interaction.Interaction;
import de.kittybot.kittybot.slashcommands.interaction.Options;
import de.kittybot.kittybot.utils.Colors;
import de.kittybot.kittybot.utils.MessageUtils;
import net.dv8tion.jda.api.EmbedBuilder;

@SuppressWarnings("unused")
public class ServerIconCommand extends SubCommand{

	public ServerIconCommand(){
		super("servericon", "Gets the server icon");
		addOptions(
			new CommandOptionLong("server-id", "The server id to get the icon from").required(),
			new CommandOptionInteger("size", "The image size")
				.addChoices(
					new CommandOptionChoice<>("16", 16),
					new CommandOptionChoice<>("32", 32),
					new CommandOptionChoice<>("64", 64),
					new CommandOptionChoice<>("128", 128),
					new CommandOptionChoice<>("256", 256),
					new CommandOptionChoice<>("512", 512),
					new CommandOptionChoice<>("1024", 1024),
					new CommandOptionChoice<>("2048", 2048)
				)
		);
	}

	@Override
	public void run(Options options, Interaction ia){
		var guildId = options.getOrDefault("server-id", ia instanceof GuildInteraction ? ((GuildInteraction) ia).getGuildId() : -1L);
		if(guildId == -1L){
			ia.error("Please provide a server id");
			return;
		}
		var size = options.has("size") ? options.getInt("size") : 1024;

		var guild = ia.getJDA().getGuildById(guildId);
		if(guild == null){
			ia.error("I'm not in  not found");
			return;
		}
		var icon = guild.getIconUrl();
		if(icon == null){
			ia.error("Guild has no icon set");
			return;
		}
		ia.reply(builder -> builder
			.setTitle(guild.getName() + " Icon")
			.setThumbnail(icon)
			.setDescription(MessageUtils.maskLink(size + "px", icon + "?size=" + size)));
	}

}
