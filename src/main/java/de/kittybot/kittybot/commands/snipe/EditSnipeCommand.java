package de.kittybot.kittybot.commands.snipe;

import de.kittybot.kittybot.command.Args;
import de.kittybot.kittybot.command.Category;
import de.kittybot.kittybot.command.Command;
import de.kittybot.kittybot.command.CommandContext;
import de.kittybot.kittybot.modules.MessageModule;
import de.kittybot.kittybot.modules.SettingsModule;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;

public class EditSnipeCommand extends Command{

	public EditSnipeCommand(){
		super("editsnipe", "Snipes a edited message", Category.SNIPE);
		addAliases("es");
	}

	@Override
	public void run(Args args, CommandContext ctx){
		if(!ctx.getChannel().canTalk()){
			return;
		}
		if(!ctx.get(SettingsModule.class).areSnipesEnabled(ctx.getGuildId())){
			ctx.error("Edit Snipes are disabled for this guild");
		}
		if(ctx.get(SettingsModule.class).areSnipesDisabledInChannel(ctx.getGuildId(), ctx.getChannelId())){
			ctx.error("Snipes are disabled for this guild");
		}
		var lastEditedMessage = ctx.get(MessageModule.class).getLastEditedMessage(ctx.getMessage().getTextChannel().getIdLong());
		if(lastEditedMessage == null){
			ctx.sendError("There's no edited message to snipe");
			return;
		}
		ctx.getJDA().retrieveUserById(lastEditedMessage.getAuthorId()).queue(user -> {
			ctx.getChannel().sendMessage(new EmbedBuilder()
					.setDescription(lastEditedMessage.getContent())
					.setTimestamp(lastEditedMessage.getTimeCreated())
					.setColor(Color.GREEN)
					.setFooter(ctx.getMember().getEffectiveName(), ctx.getUser().getEffectiveAvatarUrl())
					.setAuthor(user.getName(), lastEditedMessage.getJumpUrl(), user.getEffectiveAvatarUrl())
					.build()
			).queue();
		});
	}

}
