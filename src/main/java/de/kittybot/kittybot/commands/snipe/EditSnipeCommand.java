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
public class EditSnipeCommand extends Command implements RunnableCommand{

	public EditSnipeCommand(){
		super("editsnipe", "Snipes the last edited message", Category.SNIPE);
	}

	@Override
	public void run(Options options, CommandContext ctx){
		var settings = ctx.get(SettingsModule.class).getSettings(ctx.getGuildId());
		if(!settings.areSnipesEnabled()){
			ctx.error("Snipes are disabled for this guild");
		}
		if(settings.areSnipesDisabledInChannel(ctx.getChannelId())){
			ctx.error("Snipes are disabled for this channel");
		}
		var lastEditedMessage = ctx.get(MessageModule.class).getLastEditedMessage(ctx.getChannelId());
		if(lastEditedMessage == null){
			ctx.reply(new EmbedBuilder().setColor(Color.RED).setDescription("There are no edited messages to snipe"));
			return;
		}
		ctx.getJDA().retrieveUserById(lastEditedMessage.getAuthorId()).queue(user ->
				ctx.reply(new EmbedBuilder()
						.setColor(Colors.KITTYBOT_BLUE)
						.setAuthor(user.getName(), lastEditedMessage.getJumpUrl(), user.getEffectiveAvatarUrl())
						.setDescription(lastEditedMessage.getContent())
						.setFooter(ctx.getMember().getEffectiveName(), ctx.getUser().getEffectiveAvatarUrl())
						.setTimestamp(lastEditedMessage.getTimeCreated())
						.build()
				)
		);
	}

}
