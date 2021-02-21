package de.kittybot.kittybot.commands.snipe;

import de.kittybot.kittybot.modules.MessageModule;
import de.kittybot.kittybot.modules.SettingsModule;
import de.kittybot.kittybot.slashcommands.application.Category;
import de.kittybot.kittybot.slashcommands.application.RunGuildCommand;
import de.kittybot.kittybot.slashcommands.interaction.GuildInteraction;
import de.kittybot.kittybot.slashcommands.interaction.Options;
import de.kittybot.kittybot.utils.Colors;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.Color;

@SuppressWarnings("unused")
public class SnipeCommand extends RunGuildCommand{

	public SnipeCommand(){
		super("snipe", "Snipes the last deleted message", Category.SNIPE);
	}

	@Override
	public void run(Options options, GuildInteraction ia){
		if(!ia.get(SettingsModule.class).areSnipesEnabled(ia.getGuildId())){
			ia.error("Snipes are disabled for this guild");
		}
		if(ia.get(SettingsModule.class).areSnipesDisabledInChannel(ia.getGuildId(), ia.getChannelId())){
			ia.error("Snipes are disabled for this channel");
		}
		var lastDeletedMessage = ia.get(MessageModule.class).getLastDeletedMessage(ia.getChannelId());
		if(lastDeletedMessage == null){
			ia.reply(new EmbedBuilder().setColor(Color.RED).setDescription("There are no deleted messages to snipe"));
			return;
		}
		ia.getJDA().retrieveUserById(lastDeletedMessage.getAuthorId()).queue(user ->
			ia.reply(new EmbedBuilder()
				.setColor(Colors.KITTYBOT_BLUE)
				.setAuthor(user.getName(), lastDeletedMessage.getJumpUrl(), user.getEffectiveAvatarUrl())
				.setDescription(lastDeletedMessage.getContent())
				.setFooter(ia.getMember().getEffectiveName(), ia.getUser().getEffectiveAvatarUrl())
				.setTimestamp(lastDeletedMessage.getTimeCreated())
				.build()
			)
		);
	}

}
