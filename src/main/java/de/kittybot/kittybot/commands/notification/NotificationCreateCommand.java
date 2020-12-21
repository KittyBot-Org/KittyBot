package de.kittybot.kittybot.commands.notification;

import de.kittybot.kittybot.command.Category;
import de.kittybot.kittybot.command.Command;
import de.kittybot.kittybot.command.ctx.CommandContext;
import de.kittybot.kittybot.utils.TimeUtils;
import org.eclipse.jetty.util.log.Log;

import java.time.LocalDateTime;
import java.util.List;

public class NotificationCreateCommand extends Command{

	public NotificationCreateCommand(Command parent){
		super(parent, "create", "Creates a notification", Category.NOTIFICATION);
		addAliases("add", "new");
		setUsage("<HH:mm-dd.MM.yyyy/2y2w2d2h2m2s> <text>");
	}

	@Override
	protected void run(List<String> args, CommandContext ctx){
		if(args.size() < 2){
			ctx.sendUsage(this);
			return;
		}
		var message = ctx.getRawMessage(1);
		if(message.isBlank()){
			ctx.sendError("Please provide a text");
			return;
		}
		var time = TimeUtils.parse(args.get(0));
		if(time == null || time.isBefore(LocalDateTime.now())){
			ctx.sendError("Please provide a valid time or duration");
			return;
		}
		var notif = ctx.getNotificationManager().create(
				ctx.getGuild().getIdLong(),
				ctx.getChannel().getIdLong(),
				ctx.getMessage().getIdLong(),
				ctx.getUser().getIdLong(),
				message,
				time
		);
		if(notif == null){
			ctx.sendError("There was an unexpected error while creating your notification");
			return;
		}
		ctx.sendSuccess("Created Notification with id: `" + notif.getId() + "`");
	}

}
