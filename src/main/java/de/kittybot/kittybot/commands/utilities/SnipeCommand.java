package de.kittybot.kittybot.commands.utilities;

import de.kittybot.kittybot.KittyBot;
import de.kittybot.kittybot.cache.MessageCache;
import de.kittybot.kittybot.objects.command.ACommand;
import de.kittybot.kittybot.objects.command.Category;
import de.kittybot.kittybot.objects.command.CommandContext;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;
import java.util.concurrent.TimeUnit;

public class SnipeCommand extends ACommand{

	public static final String COMMAND = "snipe";
	public static final String USAGE = "snipe";
	public static final String DESCRIPTION = "Snipes a deleted message";
	protected static final String[] ALIASES = {"s", "dsnipe"};
	protected static final Category CATEGORY = Category.UTILITIES;

	public SnipeCommand(){
		super(COMMAND, USAGE, DESCRIPTION, ALIASES, CATEGORY);
	}

	@Override
	public void run(final CommandContext ctx){
		if(!ctx.getChannel().canTalk()){
			return;
		}
		final var lastDeletedMessage = MessageCache.getLastDeletedMessage(ctx.getMessage().getTextChannel().getId());
		if(lastDeletedMessage == null){
			sendError(ctx, "There's no deleted message to snipe");
			return;
		}
		ctx.getJDA().retrieveUserById(lastDeletedMessage.getAuthorId()).queue(user -> {
			ctx.getChannel().sendMessage(new EmbedBuilder()
					.setDescription(lastDeletedMessage.getContent())
					.setTimestamp(lastDeletedMessage.getTimeCreated())
					.setColor(Color.GREEN)
					.setFooter(ctx.getMember().getEffectiveName(), ctx.getUser().getEffectiveAvatarUrl())
					.setAuthor(user.getName(), null, user.getEffectiveAvatarUrl())
					.build()
			).queue();
			KittyBot.getScheduler().schedule(() -> MessageCache.uncacheMessage(lastDeletedMessage.getChannelId(), lastDeletedMessage.getId()), 2, TimeUnit.MINUTES);
		});
	}

}