package de.kittybot.kittybot.commands.notification.notification;

import de.kittybot.kittybot.modules.NotificationModule;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionInteger;
import de.kittybot.kittybot.slashcommands.application.options.GuildSubCommand;
import de.kittybot.kittybot.slashcommands.GuildCommandContext;
import de.kittybot.kittybot.slashcommands.Options;

public class DeleteCommand extends GuildSubCommand{

	public DeleteCommand(){
		super("delete", "Deletes a notification");
		addOptions(
			new CommandOptionInteger("notification-id", "The notification id").required()
		);
	}

	@Override
	public void run(Options options, GuildCommandContext ctx){
		var notificationId = options.getLong("notification-id");
		if(ctx.get(NotificationModule.class).delete(notificationId, ctx.getUserId())){
			ctx.reply("Deleted your notification with id `" + notificationId + "`");
			return;
		}
		ctx.error("Notification either does not exist or does not belong to you");
	}

}
