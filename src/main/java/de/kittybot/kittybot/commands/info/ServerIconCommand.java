package de.kittybot.kittybot.commands.info;

import de.kittybot.kittybot.slashcommands.CommandContext;
import de.kittybot.kittybot.slashcommands.GuildCommandContext;
import de.kittybot.kittybot.slashcommands.Options;
import de.kittybot.kittybot.slashcommands.application.Category;
import de.kittybot.kittybot.slashcommands.application.OptionChoice;
import de.kittybot.kittybot.slashcommands.application.RunCommand;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionInteger;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionLong;
import de.kittybot.kittybot.utils.MessageUtils;

@SuppressWarnings("unused")
public class ServerIconCommand extends RunCommand{

	public ServerIconCommand(){
		super("servericon", "Gets the server icon", Category.INFORMATION);
		addOptions(
			new CommandOptionLong("server-id", "The server id to get the icon from").required(),
			new CommandOptionInteger("size", "The image size")
				.addChoices(
					new OptionChoice("16", 16),
					new OptionChoice("32", 32),
					new OptionChoice("64", 64),
					new OptionChoice("128", 128),
					new OptionChoice("256", 256),
					new OptionChoice("512", 512),
					new OptionChoice("1024", 1024),
					new OptionChoice("2048", 2048)
				)
		);
	}

	@Override
	public void run(Options options, CommandContext ctx){
		var guildId = options.getLong("guild-id", ctx instanceof GuildCommandContext ? ((GuildCommandContext) ctx).getGuildId() : -1L);
		if(guildId == -1L){
			ctx.error("Please provide a server id");
			return;
		}
		var size = options.has("size") ? options.getInt("size") : 1024;

		var guild = ctx.getJDA().getGuildById(guildId);
		if(guild == null){
			ctx.error("Server not found");
			return;
		}
		var icon = guild.getIconUrl();
		if(icon == null){
			ctx.error("Server has no icon set");
			return;
		}
		ctx.reply(builder -> builder
			.setTitle(guild.getName() + " Icon")
			.setThumbnail(icon)
			.setDescription(MessageUtils.maskLink(size + "px", icon + "?size=" + size)));
	}

}
