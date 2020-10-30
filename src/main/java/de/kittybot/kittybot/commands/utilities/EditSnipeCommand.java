package de.kittybot.kittybot.commands.utilities;

import de.kittybot.kittybot.KittyBot;
import de.kittybot.kittybot.cache.MessageCache;
import de.kittybot.kittybot.objects.command.ACommand;
import de.kittybot.kittybot.objects.command.Category;
import de.kittybot.kittybot.objects.command.CommandContext;
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
		if(!ctx.getChannel().canTalk()){
			return;
		}
		final var lastEditedMessage = MessageCache.getLastEditedMessage(ctx.getMessage().getTextChannel().getId());
		if(lastEditedMessage == null){
			sendError(ctx, "There's no edited message to snipe");
			return;
		}

		ctx.getJDA().retrieveUserById(lastEditedMessage.getAuthorId()).queue(user -> {
			ctx.getChannel().sendMessage(new EmbedBuilder()
					.setAuthor(user.getName(), lastEditedMessage.getJumpUrl(), user.getEffectiveAvatarUrl())
					.setDescription(lastEditedMessage.getContent())
					.setTimestamp(lastEditedMessage.getTimeEdited())
					.setColor(Color.GREEN)
					.setFooter(ctx.getMember().getEffectiveName(), ctx.getUser().getEffectiveAvatarUrl())
					.build()
			).queue();
			KittyBot.getScheduler().schedule(() -> MessageCache.uncacheEditedMessage(lastEditedMessage.getChannelId(), lastEditedMessage.getId()), 2, TimeUnit.MINUTES);
		});
	}

}