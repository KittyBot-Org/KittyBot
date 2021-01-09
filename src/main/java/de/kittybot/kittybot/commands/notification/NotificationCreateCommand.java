package de.kittybot.kittybot.commands.notification;

import de.kittybot.kittybot.command.Args;
import de.kittybot.kittybot.command.Category;
import de.kittybot.kittybot.command.Command;
import de.kittybot.kittybot.command.CommandContext;
import de.kittybot.kittybot.modules.NotificationModule;
import de.kittybot.kittybot.utils.TimeUtils;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class NotificationCreateCommand extends Command{

	public NotificationCreateCommand(Command parent){
		super(parent, "create", "Creates a notification", Category.NOTIFICATION);
		addAliases("add", "new");
		setUsage("<HH:mm-dd.MM.yyyy/2y2w2d2h2m2s> <text>");
	}

	@Override
	public void run(Args args, CommandContext ctx){
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
		var notif = ctx.get(NotificationModule.class).create(
				ctx.getGuildId(),
				ctx.getChannelId(),
				ctx.getMessageId(),
				ctx.getUser().getIdLong(),
				message,
				time
		);
		if(notif == null){
			ctx.sendError("There was an unexpected error while creating your notification");
			return;
		}
		ctx.sendSuccess("Created Notification in `" + TimeUtils.formatDuration(notif.getCreatedAt().until(notif.getNotificationTime(), ChronoUnit.MILLIS)) + "` with id: `" + notif.getId() + "`");
	}

}
