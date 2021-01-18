package de.kittybot.kittybot.commands.utility;

import de.kittybot.kittybot.slashcommands.application.Category;
import de.kittybot.kittybot.slashcommands.application.Command;
import de.kittybot.kittybot.slashcommands.application.CommandOptionChoice;
import de.kittybot.kittybot.slashcommands.application.RunnableCommand;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionInteger;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionString;
import de.kittybot.kittybot.slashcommands.context.CommandContext;
import de.kittybot.kittybot.slashcommands.context.Options;
import de.kittybot.kittybot.utils.Colors;
import de.kittybot.kittybot.utils.MessageUtils;
import net.dv8tion.jda.api.EmbedBuilder;

@SuppressWarnings("unused")
public class GuildIconCommand extends Command implements RunnableCommand{

	public GuildIconCommand(){
		super("guildicon", "Gets the guild icon", Category.UTILITIES);
		addOptions(
			new CommandOptionString("guild-id", "The guild id to get the icon from"),
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
	public void run(Options options, CommandContext ctx){
		var guildId = options.has("guild-id") ? options.getLong("guild-id") : ctx.getGuildId();
		var size = options.has("size") ? options.getInt("size") : 1024;

		if(guildId == -1){
			ctx.error("Please provide a valid guild id");
			return;
		}

		var guild = ctx.getJDA().getGuildById(guildId);
		if(guild == null){
			ctx.error("Guild not found");
			return;
		}
		var icon = guild.getIconUrl();
		if(icon == null){
			ctx.error("Guild has no icon set");
			return;
		}
		ctx.reply(new EmbedBuilder()
			.setColor(Colors.KITTYBOT_BLUE)
			.setTitle(guild.getName() + " Icon")
			.setThumbnail(icon)
			.setDescription(MessageUtils.maskLink(size + "px", icon + "?size=" + size)));
	}

}
