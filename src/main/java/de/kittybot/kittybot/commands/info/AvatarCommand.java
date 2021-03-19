package de.kittybot.kittybot.commands.info;

import de.kittybot.kittybot.slashcommands.application.Category;
import de.kittybot.kittybot.slashcommands.CommandContext;
import de.kittybot.kittybot.slashcommands.Options;
import de.kittybot.kittybot.slashcommands.application.OptionChoice;
import de.kittybot.kittybot.slashcommands.application.RunCommand;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionInteger;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionUser;
import de.kittybot.kittybot.utils.MessageUtils;

@SuppressWarnings("unused")
public class AvatarCommand extends RunCommand{

	public AvatarCommand(){
		super("avatar", "Gets the avatar of a user", Category.INFORMATION);
		addOptions(
			new CommandOptionUser("user", "The user to get the avatar from"),
			new CommandOptionInteger("size", "The image size")
				.addChoices(
					new OptionChoice("16px", 16),
					new OptionChoice("32px", 32),
					new OptionChoice("64px", 64),
					new OptionChoice("128px", 128),
					new OptionChoice("256px", 256),
					new OptionChoice("512px", 512),
					new OptionChoice("1024px", 1024),
					new OptionChoice("2048px", 2048)
				)
		);
	}

	@Override
	public void run(Options options, CommandContext ctx){
		var user = options.getUser("user", ctx.getUser());
		var size = options.getInt("size", 1024);

		ctx.reply(builder -> builder
			.setTitle(user.getAsTag() + " Avatar")
			.setThumbnail(user.getEffectiveAvatarUrl())
			.setDescription(MessageUtils.maskLink(size + "px", user.getEffectiveAvatarUrl() + "?size=" + size))
		);
	}

}