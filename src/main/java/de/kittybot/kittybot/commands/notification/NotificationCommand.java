package de.kittybot.kittybot.commands.notification;

import de.kittybot.kittybot.commands.notification.notification.CreateCommand;
import de.kittybot.kittybot.commands.notification.notification.DeleteCommand;
import de.kittybot.kittybot.commands.notification.notification.ListCommand;
import de.kittybot.kittybot.slashcommands.application.Category;
import de.kittybot.kittybot.slashcommands.application.Command;

@SuppressWarnings("unused")
public class NotificationCommand extends Command{

	public NotificationCommand(){
		super("notification", "Creates/deletes/lists notifications", Category.NOTIFICATION);
		addOptions(
			new CreateCommand(),
			new DeleteCommand(),
			new ListCommand()
		);
	}

}
