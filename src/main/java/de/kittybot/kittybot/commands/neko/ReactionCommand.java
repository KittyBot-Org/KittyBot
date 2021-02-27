package de.kittybot.kittybot.commands.neko;

import de.kittybot.kittybot.modules.RequestModule;
import de.kittybot.kittybot.objects.enums.Neko;
import de.kittybot.kittybot.slashcommands.application.Category;
import de.kittybot.kittybot.slashcommands.application.RunCommand;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionUser;
import de.kittybot.kittybot.slashcommands.interaction.Interaction;
import de.kittybot.kittybot.slashcommands.interaction.Options;
import de.kittybot.kittybot.utils.Colors;
import de.kittybot.kittybot.utils.annotations.Ignore;
import net.dv8tion.jda.api.EmbedBuilder;

@Ignore
public abstract class ReactionCommand extends RunCommand{

	private final Neko neko;
	private final String text;

	protected ReactionCommand(Neko neko, String description, String text){
		super(neko.getName(), description, Category.NEKO);
		this.neko = neko;
		this.text = text;
		addOptions(
			new CommandOptionUser("user", "The user to interact with").required()
		);
	}

	@Override
	public void run(Options options, Interaction ia){
		var user = options.getUser("user");
		var message = new StringBuilder();
		if(user.getIdLong() == ia.getUserId()){
			message
				.append("You are not allowed to ")
				.append(getName())
				.append(" yourself so I ")
				.append(getName())
				.append(" you ")
				.append(ia.getUser().getAsMention());
		}
		else{
			message
				.append(ia.getUser().getAsMention())
				.append(" ")
				.append(this.text)
				.append(" ")
				.append(user.getAsMention());
		}
		ia.reply(builder -> builder
			.setDescription(message)
			.setImage(ia.get(RequestModule.class).getNeko(this.neko))
		);
	}

}
