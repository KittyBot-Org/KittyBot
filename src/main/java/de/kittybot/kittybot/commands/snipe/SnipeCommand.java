package de.kittybot.kittybot.commands.snipe;

import de.kittybot.kittybot.command.Category;
import de.kittybot.kittybot.command.Command;
import de.kittybot.kittybot.command.ctx.CommandContext;
import de.kittybot.kittybot.main.KittyBot;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;
import java.util.List;

public class SnipeCommand extends Command{

	private final KittyBot main;

	public SnipeCommand(KittyBot main){
		super("snipe", "Snipes a deleted message", Category.SNIPE);
		this.main = main;
		addAliases("s");
	}

	@Override
	public void run(List<String> args, CommandContext ctx){
		if(!ctx.getChannel().canTalk()){
			return;
		}
		var lastDeletedMessage = this.main.getMessageManager().getLastDeletedMessage(ctx.getMessage().getTextChannel().getIdLong());
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
