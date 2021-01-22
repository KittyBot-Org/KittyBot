package de.kittybot.kittybot.commands.info.info;

import de.kittybot.kittybot.slashcommands.application.CommandOptionChoice;
import de.kittybot.kittybot.slashcommands.application.options.*;
import de.kittybot.kittybot.slashcommands.context.CommandContext;
import de.kittybot.kittybot.slashcommands.context.Options;
import de.kittybot.kittybot.utils.Colors;
import de.kittybot.kittybot.utils.MessageUtils;
import net.dv8tion.jda.api.EmbedBuilder;

@SuppressWarnings("unused")
public class AvatarCommand extends SubCommand{

	public AvatarCommand(){
		super("avatar", "Gets the avatar of a user");
		addOptions(
			new CommandOptionUser("user", "The user to get the avatar from"),
			new CommandOptionLong("user-id", "The user id to get the avatar from"),
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
		var userId = options.has("user") ? options.getLong("user") : options.has("user-id") ? options.getLong("user-id") : ctx.getUserId();
		var size = options.has("size") ? options.getInt("size") : 1024;

		ctx.getJDA().retrieveUserById(userId).queue(user ->
				ctx.reply(new EmbedBuilder()
					.setColor(Colors.KITTYBOT_BLUE)
					.setTitle(user.getAsTag() + " Avatar")
					.setThumbnail(user.getEffectiveAvatarUrl())
					.setDescription(MessageUtils.maskLink(size + "px", user.getEffectiveAvatarUrl() + "?size=" + size)))
			, error -> ctx.error("User not found"));
	}

}
