package de.kittybot.kittybot.commands.info;

import de.kittybot.kittybot.objects.enums.Emoji;
import de.kittybot.kittybot.slashcommands.CommandContext;
import de.kittybot.kittybot.slashcommands.Options;
import de.kittybot.kittybot.slashcommands.application.Category;
import de.kittybot.kittybot.slashcommands.application.RunCommand;
import de.kittybot.kittybot.utils.Config;
import de.kittybot.kittybot.utils.MessageUtils;

@SuppressWarnings("unused")
public class HelpCommand extends RunCommand{

	public HelpCommand(){
		super("help", "Shows help", Category.INFORMATION);
	}

	@Override
	public void run(Options options, CommandContext ctx){
		ctx.reply(builder -> builder
			.setAuthor("Help", Config.ORIGIN_URL, ctx.getSelfUser().getEffectiveAvatarUrl())
			.setThumbnail(ctx.getSelfUser().getEffectiveAvatarUrl())
			.setDescription(
				"Hello " + ctx.getUser().getAsMention() + "\n" +
					"KittyBot uses the new " + Emoji.SLASH.get() + " Slash Commands by Discord!\n" +
					"To see all available commands just type `/` or use `/commands`\n\n" +
					MessageUtils.maskLink("Website", Config.ORIGIN_URL) + " | " +
					MessageUtils.maskLink("Invite Me", Config.BOT_INVITE_URL) + " | " +
					MessageUtils.maskLink("Support Server", Config.SUPPORT_GUILD_INVITE_URL) + " | " +
					MessageUtils.maskLink("GitHub", "https://github.com/KittyBot-Org/KittyBot")
			)
		);
	}

}
