package de.kittybot.kittybot.commands.snipe;

import de.kittybot.kittybot.modules.MessageModule;
import de.kittybot.kittybot.modules.SettingsModule;
import de.kittybot.kittybot.slashcommands.application.Category;
import de.kittybot.kittybot.slashcommands.application.RunGuildCommand;
import de.kittybot.kittybot.slashcommands.interaction.GuildInteraction;
import de.kittybot.kittybot.slashcommands.interaction.Options;
import de.kittybot.kittybot.utils.Colors;
import de.kittybot.kittybot.utils.MessageUtils;
import de.kittybot.kittybot.utils.TimeUtils;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.Color;

@SuppressWarnings("unused")
public class EditSnipeCommand extends RunGuildCommand{

	public EditSnipeCommand(){
		super("editsnipe", "Snipes the last edited message", Category.SNIPE);
	}

	@Override
	public void run(Options options, GuildInteraction ia){
		var settings = ia.get(SettingsModule.class).getSettings(ia.getGuildId());
		if(!settings.areSnipesEnabled()){
			ia.error("Snipes are disabled for this guild");
		}
		if(settings.areSnipesDisabledInChannel(ia.getChannelId())){
			ia.error("Snipes are disabled for this channel");
		}
		var lastEditedMessage = ia.get(MessageModule.class).getLastEditedMessage(ia.getChannelId());
		if(lastEditedMessage == null){
			ia.reply(builder -> builder.setColor(Color.RED).setDescription("There are no edited messages to snipe"));
			return;
		}
		ia.getJDA().retrieveUserById(lastEditedMessage.getAuthorId()).queue(user ->
			ia.reply(builder -> builder
				.setAuthor("Edit Sniped " + user.getName(), lastEditedMessage.getJumpUrl())
				.setDescription(lastEditedMessage.getContent())
				.setFooter("from " + user.getName(), user.getEffectiveAvatarUrl())
				.setTimestamp(lastEditedMessage.getTimeCreated())
			)
		);
	}

}
