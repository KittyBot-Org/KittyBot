package de.kittybot.kittybot.commands.snipe;

import de.kittybot.kittybot.command.Category;
import de.kittybot.kittybot.command.Command;
import de.kittybot.kittybot.command.ctx.CommandContext;
import de.kittybot.kittybot.main.KittyBot;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;
import java.util.List;

public class EditSnipeCommand extends Command{

	public EditSnipeCommand(){
		super("editsnipe", "Snipes a edited message", Category.SNIPE);
		addAliases("es");
	}

	@Override
	public void run(List<String> args, CommandContext ctx){
		if(!ctx.getChannel().canTalk()){
			return;
		}
		if(!ctx.getGuildSettingsManager().areSnipesEnabled(ctx.getGuild().getIdLong())){
			ctx.error("Edit Snipes are disabled for this guild");
		}
		if(ctx.getGuildSettingsManager().areSnipesDisabledInChannel(ctx.getGuild().getIdLong(), ctx.getChannel().getIdLong())){
			ctx.error("Snipes are disabled for this guild");
		}
		var lastEditedMessage = ctx.getMessageManager().getLastEditedMessage(ctx.getMessage().getTextChannel().getIdLong());
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
