package de.kittybot.kittybot.commands.snipe;

import de.kittybot.kittybot.modules.MessageModule;
import de.kittybot.kittybot.modules.SettingsModule;
import de.kittybot.kittybot.slashcommands.GuildCommandContext;
import de.kittybot.kittybot.slashcommands.Options;
import de.kittybot.kittybot.slashcommands.application.Category;
import de.kittybot.kittybot.slashcommands.application.RunGuildCommand;

import java.awt.Color;

@SuppressWarnings("unused")
public class EditSnipeCommand extends RunGuildCommand{

	public EditSnipeCommand(){
		super("editsnipe", "Snipes the last edited message", Category.SNIPE);
	}

	@Override
	public void run(Options options, GuildCommandContext ctx){
		var settings = ctx.get(SettingsModule.class).getSettings(ctx.getGuildId());
		if(!settings.areSnipesEnabled()){
			ctx.error("Snipes are disabled for this guild");
		}
		if(settings.areSnipesDisabledInChannel(ctx.getChannelId())){
			ctx.error("Snipes are disabled for this channel");
		}
		var lastEditedMessage = ctx.get(MessageModule.class).getLastEditedMessage(ctx.getChannelId());
		if(lastEditedMessage == null){
			ctx.reply(builder -> builder.setColor(Color.RED).setDescription("There are no edited messages to snipe"));
			return;
		}
		ctx.getJDA().retrieveUserById(lastEditedMessage.getAuthorId()).queue(user ->
			ctx.reply(builder -> {
				builder
					.setAuthor("Edit Sniped " + user.getName(), lastEditedMessage.getJumpUrl())
					.setDescription(lastEditedMessage.getContent())
					.setFooter("from " + user.getName(), user.getEffectiveAvatarUrl())
					.setTimestamp(lastEditedMessage.getTimeCreated());
				if(!lastEditedMessage.getAttachments().isEmpty()){
					lastEditedMessage.getAttachments().forEach(attachment -> builder.addField("Attachment", attachment, true));
				}
			})
		);
	}

}
