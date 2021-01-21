package de.kittybot.kittybot.commands.neko;

import de.kittybot.kittybot.modules.RequestModule;
import de.kittybot.kittybot.slashcommands.application.Category;
import de.kittybot.kittybot.slashcommands.application.Command;
import de.kittybot.kittybot.slashcommands.application.RunnableCommand;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionUser;
import de.kittybot.kittybot.slashcommands.context.CommandContext;
import de.kittybot.kittybot.slashcommands.context.Options;
import de.kittybot.kittybot.utils.Colors;
import de.kittybot.kittybot.utils.MessageUtils;
import de.kittybot.kittybot.utils.annotations.Ignore;
import net.dv8tion.jda.api.EmbedBuilder;

@Ignore
public abstract class ReactionCommand extends Command implements RunnableCommand{

	private final String text;

	protected ReactionCommand(String name, String description, String text){
		super(name, description, Category.NEKO);
		this.text = text;
		addOptions(
			new CommandOptionUser("user", "The user to interact with").required()
		);
	}

	@Override
	public void run(Options options, CommandContext ctx){
		var userId = options.getLong("user");
		var message = new StringBuilder();
		if(userId == ctx.getUserId()){
			message
				.append("You are not allowed to ")
				.append(getName())
				.append(" yourself so I ")
				.append(getName())
				.append(" you ")
				.append(ctx.getUser().getAsMention());
		}
		else{
			message
				.append(ctx.getUser().getAsMention())
				.append(" ")
				.append(text)
				.append(" ")
				.append(MessageUtils.getUserMention(userId));
		}
		ctx.reply(new EmbedBuilder()
			.setColor(Colors.KITTYBOT_BLUE)
			.setDescription(message)
			.setImage(ctx.get(RequestModule.class).getNeko(false, getName(), "gif"))
		);
	}

}
