package de.kittybot.kittybot.commands.notification;

import de.kittybot.kittybot.command.Category;
import de.kittybot.kittybot.command.Command;
import de.kittybot.kittybot.command.ctx.CommandContext;

import java.util.List;

public class NotificationCommand extends Command{

	public NotificationCommand(){
		super("notification", "Creates/Lists/Deletes a Notifications", Category.NOTIFICATION);
		addAliases("notif");
		setUsage("<create/delete/list>");
		addChildren(new NotificationCreateCommand(this));
		addChildren(new NotificationDeleteCommand(this));
		addChildren(new NotificationListCommand(this));
	}

	@Override
	public void run(List<String> args, CommandContext ctx){
		ctx.sendUsage(this.getUsage() + " " + this.getRawUsage());
	}

}
