package de.kittybot.kittybot.commands.snipe;

import de.kittybot.kittybot.modules.MessageModule;
import de.kittybot.kittybot.modules.SettingsModule;
import de.kittybot.kittybot.slashcommands.GuildCommandContext;
import de.kittybot.kittybot.slashcommands.Options;
import de.kittybot.kittybot.slashcommands.application.Category;
import de.kittybot.kittybot.slashcommands.application.RunGuildCommand;

import java.awt.Color;

@SuppressWarnings("unused")
public class SnipeCommand extends RunGuildCommand{

	public SnipeCommand(){
		super("snipe", "Snipes the last deleted message", Category.SNIPE);
	}

	@Override
	public void run(Options options, GuildCommandContext ctx){
		if(!ctx.get(SettingsModule.class).areSnipesEnabled(ctx.getGuildId())){
			ctx.error("Snipes are disabled for this guild");
		}
		if(ctx.get(SettingsModule.class).areSnipesDisabledInChannel(ctx.getGuildId(), ctx.getChannelId())){
			ctx.error("Snipes are disabled for this channel");
		}
		var lastDeletedMessage = ctx.get(MessageModule.class).getLastDeletedMessage(ctx.getChannelId());
		if(lastDeletedMessage == null){
			ctx.reply(builder -> builder.setColor(Color.RED).setDescription("There are no deleted messages to snipe"));
			return;
		}
		ctx.getJDA().retrieveUserById(lastDeletedMessage.getAuthorId()).queue(user ->
			ctx.reply(builder -> {
				builder
					.setAuthor("Sniped " + user.getName(), lastDeletedMessage.getJumpUrl())
					.setDescription(lastDeletedMessage.getContent())
					.setFooter("from " + user.getName(), user.getEffectiveAvatarUrl())
					.setTimestamp(lastDeletedMessage.getTimeCreated());
				if(!lastDeletedMessage.getAttachments().isEmpty()){
					lastDeletedMessage.getAttachments().forEach(attachment -> builder.addField("Attachment", attachment, true));
				}
			})
		);
	}

}
