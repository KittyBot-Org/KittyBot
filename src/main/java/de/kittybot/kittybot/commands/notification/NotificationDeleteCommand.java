package de.kittybot.kittybot.commands.notification;

import de.kittybot.kittybot.command.Category;
import de.kittybot.kittybot.command.Command;
import de.kittybot.kittybot.command.ctx.CommandContext;

import java.util.List;

public class NotificationDeleteCommand extends Command{

	public NotificationDeleteCommand(Command parent){
		super(parent, "delete", "Deletes a Notifications", Category.NOTIFICATION);
		addAliases("del", "remove", "destroy");
		setUsage("<id>");
	}

	@Override
	public void run(List<String> args, CommandContext ctx){
		if(args.isEmpty()){
			ctx.sendUsage(this);
			return;
		}
		if(ctx.getNotificationManager().delete(Long.parseLong(args.get(0)), ctx.getUser().getIdLong())){
			ctx.sendSuccess("Deleted your notification with id `" + args.get(0) + "`");
			return;
		}
		ctx.sendError("Could not delete notification");
	}

}
