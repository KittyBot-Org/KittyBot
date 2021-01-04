package de.kittybot.kittybot.commands.snipe;

import de.kittybot.kittybot.command.Args;
import de.kittybot.kittybot.command.Category;
import de.kittybot.kittybot.command.Command;
import de.kittybot.kittybot.command.CommandContext;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;

public class SnipeCommand extends Command{

	public SnipeCommand(){
		super("snipe", "Snipes a deleted message", Category.SNIPE);
		addAliases("s");
	}

	@Override
	public void run(Args args, CommandContext ctx){
		if(!ctx.getChannel().canTalk()){
			return;
		}
		if(!ctx.getGuildSettingsManager().areSnipesEnabled(ctx.getGuildId())){
			ctx.error("Snipes are disabled for this guild");
		}
		var lastDeletedMessage = ctx.getMessageManager().getLastDeletedMessage(ctx.getMessage().getTextChannel().getIdLong());
		if(lastDeletedMessage == null){
			ctx.sendError("There's no deleted message to snipe");
			return;
		}
		ctx.getJDA().retrieveUserById(lastDeletedMessage.getAuthorId()).queue(user -> {
			ctx.getChannel().sendMessage(new EmbedBuilder()
					.setDescription(lastDeletedMessage.getContent())
					.setTimestamp(lastDeletedMessage.getTimeCreated())
					.setColor(Color.GREEN)
					.setFooter(ctx.getMember().getEffectiveName(), ctx.getUser().getEffectiveAvatarUrl())
					.setAuthor(user.getName(), lastDeletedMessage.getJumpUrl(), user.getEffectiveAvatarUrl())
					.build()
			).queue();
		});
	}

}
