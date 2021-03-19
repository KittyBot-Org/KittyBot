package de.kittybot.kittybot.commands.notification.notification;

import de.kittybot.kittybot.modules.NotificationModule;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionString;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionTime;
import de.kittybot.kittybot.slashcommands.application.options.GuildSubCommand;
import de.kittybot.kittybot.slashcommands.GuildCommandContext;
import de.kittybot.kittybot.slashcommands.Options;
import de.kittybot.kittybot.utils.TimeUtils;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class CreateCommand extends GuildSubCommand{

	public CreateCommand(){
		super("create", "Creates a notification");
		addOptions(
			new CommandOptionTime("time", "When to notif you. Format: `HH:mm dd.MM.yyyy` or 1y2w3d4h5m6s").required(),
			new CommandOptionString("message", "The notif message").required()/*,
            new CommandOptionBoolean("notif-in-dms", "If I should notif you in dms")*/
		);
	}

	@Override
	public void run(Options options, GuildCommandContext ctx){
		var time = options.getTime("time");
		if(time.isBefore(LocalDateTime.now())){
			ctx.error("Please provide a valid time or duration in this format: ");
			return;
		}
		var message = options.getString("message");
		var notif = ctx.get(NotificationModule.class).create(
			ctx.getGuildId(),
			ctx.getChannelId(),
			-1,
			ctx.getUser().getIdLong(),
			message,
			time
		);
		if(notif == null){
			ctx.error("There was an unexpected error while creating your notification");
			return;
		}
		ctx.reply("Notification at `" + TimeUtils.formatDuration(notif.getCreatedAt().until(notif.getNotificationTime(), ChronoUnit.MILLIS)) + "` created with id: `" + notif.getId() + "`");

	}

}
