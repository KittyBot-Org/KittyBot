package de.kittybot.kittybot.main.commands.notification;

import de.kittybot.kittybot.command.old.Args;
import de.kittybot.kittybot.command.Category;
import de.kittybot.kittybot.command.old.Command;
import de.kittybot.kittybot.command.old.CommandContext;

@SuppressWarnings("unused")
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
	public void run(Args args, CommandContext ctx){
		ctx.sendUsage(this.getUsage() + " " + this.getRawUsage());
	}

}
