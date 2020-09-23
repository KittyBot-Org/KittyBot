package de.anteiku.kittybot.commands.utilities;

import de.anteiku.kittybot.KittyBot;
import de.anteiku.kittybot.objects.cache.MessageCache;
import de.anteiku.kittybot.objects.command.ACommand;
import de.anteiku.kittybot.objects.command.Category;
import de.anteiku.kittybot.objects.command.CommandContext;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;
import java.util.concurrent.TimeUnit;

public class EditSnipeCommand extends ACommand{

	public static final String COMMAND = "editsnipe";
	public static final String USAGE = "editsnipe";
	public static final String DESCRIPTION = "Snipes an edited message";
	protected static final String[] ALIASES = {"es", "esnipe"};
	protected static final Category CATEGORY = Category.UTILITIES;

	public EditSnipeCommand(){
		super(COMMAND, USAGE, DESCRIPTION, ALIASES, CATEGORY);
	}

	@Override
	public void run(final CommandContext ctx){
		final var lastEditedMessage = MessageCache.getLastEditedMessage(ctx.getMessage().getTextChannel().getId());
		if(lastEditedMessage == null){
			sendError(ctx, "There's no edited message to snipe");
			return;
		}
		final var eb = new EmbedBuilder();
		eb.setTimestamp(lastEditedMessage.getTimeEdited());
		eb.setDescription(lastEditedMessage.getContent());
		eb.setColor(Color.GREEN);
		ctx.getJDA().retrieveUserById(lastEditedMessage.getAuthorId()).queue(user -> eb.setAuthor(user.getName(), lastEditedMessage.getJumpUrl(), user.getEffectiveAvatarUrl()));

		sendAnswer(ctx, eb);
		KittyBot.getScheduler().schedule(() -> MessageCache.uncacheEditedMessage(lastEditedMessage.getChannelId(), lastEditedMessage.getId()), 2, TimeUnit.MINUTES);
	}

}