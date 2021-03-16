package de.kittybot.kittybot.commands.notification.notification;

import de.kittybot.kittybot.modules.NotificationModule;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionInteger;
import de.kittybot.kittybot.slashcommands.application.options.GuildSubCommand;
import de.kittybot.kittybot.slashcommands.interaction.GuildInteraction;
import de.kittybot.kittybot.slashcommands.interaction.Options;

public class DeleteCommand extends GuildSubCommand{

	public DeleteCommand(){
		super("delete", "Deletes a notification");
		addOptions(
			new CommandOptionInteger("notification-id", "The notification id").required()
		);
	}

	@Override
	public void run(Options options, GuildInteraction ia){
		var notificationId = options.getLong("notification-id");
		if(ia.get(NotificationModule.class).delete(notificationId, ia.getUserId())){
			ia.reply("Deleted your notification with id `" + notificationId + "`");
			return;
		}
		ia.error("Notification either does not exist or does not belong to you");
	}

}
