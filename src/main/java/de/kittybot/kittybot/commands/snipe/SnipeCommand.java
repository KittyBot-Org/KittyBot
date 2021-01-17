package de.kittybot.kittybot.commands.snipe;

import de.kittybot.kittybot.command.Category;
import de.kittybot.kittybot.command.application.Command;
import de.kittybot.kittybot.command.application.RunnableCommand;
import de.kittybot.kittybot.command.context.CommandContext;
import de.kittybot.kittybot.command.interaction.Options;
import de.kittybot.kittybot.modules.MessageModule;
import de.kittybot.kittybot.modules.SettingsModule;
import de.kittybot.kittybot.utils.Colors;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.Color;

@SuppressWarnings("unused")
public class SnipeCommand extends Command implements RunnableCommand{

	public SnipeCommand(){
		super("snipe", "Snipes the last deleted message", Category.SNIPE);
	}

	@Override
	public void run(Options options, CommandContext ctx){
		if(!ctx.get(SettingsModule.class).areSnipesEnabled(ctx.getGuildId())){
			ctx.error("Snipes are disabled for this guild");
		}
		if(ctx.get(SettingsModule.class).areSnipesDisabledInChannel(ctx.getGuildId(), ctx.getChannelId())){
			ctx.error("Snipes are disabled for this channel");
		}
		var lastDeletedMessage = ctx.get(MessageModule.class).getLastEditedMessage(ctx.getChannelId());
		if(lastDeletedMessage == null){
			ctx.reply(new EmbedBuilder().setColor(Color.RED).setDescription("There are no deleted messages to snipe"));
			return;
		}
		ctx.getJDA().retrieveUserById(lastDeletedMessage.getAuthorId()).queue(user ->
				ctx.reply(new EmbedBuilder()
						.setColor(Colors.KITTYBOT_BLUE)
						.setAuthor(user.getName(), lastDeletedMessage.getJumpUrl(), user.getEffectiveAvatarUrl())
						.setDescription(lastDeletedMessage.getContent())
						.setFooter(ctx.getMember().getEffectiveName(), ctx.getUser().getEffectiveAvatarUrl())
						.setTimestamp(lastDeletedMessage.getTimeCreated())
						.build()
				)
		);
	}

}
