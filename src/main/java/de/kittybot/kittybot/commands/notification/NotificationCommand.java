package de.kittybot.kittybot.commands.notification;

import de.kittybot.kittybot.modules.NotificationModule;
import de.kittybot.kittybot.modules.PaginatorModule;
import de.kittybot.kittybot.slashcommands.application.Category;
import de.kittybot.kittybot.slashcommands.application.Command;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionInteger;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionString;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionTime;
import de.kittybot.kittybot.slashcommands.application.options.GuildSubCommand;
import de.kittybot.kittybot.slashcommands.interaction.GuildInteraction;
import de.kittybot.kittybot.slashcommands.interaction.Options;
import de.kittybot.kittybot.utils.Colors;
import de.kittybot.kittybot.utils.Config;
import de.kittybot.kittybot.utils.TimeUtils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

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

	private static class CreateCommand extends GuildSubCommand{

		public CreateCommand(){
			super("create", "Creates a notification");
			addOptions(
				new CommandOptionTime("time", "When to notif you. Format: `HH:mm dd.MM.yyyy` or 1y2w3d4h5m6s").required(),
				new CommandOptionString("message", "The notif message").required()/*,
				new CommandOptionBoolean("notif-in-dms", "If I should notif you in dms")*/
			);
		}

		@Override
		public void run(Options options, GuildInteraction ia){
			var time = options.getTime("time");
			if(time.isBefore(LocalDateTime.now())){
				ia.error("Please provide a valid time or duration in this format: ");
				return;
			}
			var message = options.getString("message");
			var notif = ia.get(NotificationModule.class).create(
				ia.getGuildId(),
				ia.getChannelId(),
				-1,
				ia.getUser().getIdLong(),
				message,
				time
			);
			if(notif == null){
				ia.error("There was an unexpected error while creating your notification");
				return;
			}
			ia.reply("Notification at `" + TimeUtils.formatDuration(notif.getCreatedAt().until(notif.getNotificationTime(), ChronoUnit.MILLIS)) + "` created with id: `" + notif.getId() + "`");

		}

	}

	private static class DeleteCommand extends GuildSubCommand{

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

	private static class ListCommand extends GuildSubCommand{

		public ListCommand(){
			super("list", "Lists your notifications");
		}

		@Override
		public void run(Options options, GuildInteraction ia){
			var notifs = ia.get(NotificationModule.class).get(ia.getUserId());

			if(notifs.isEmpty()){
				ia.reply("You have no notifications");
				return;
			}

			var notifMessage = new StringBuilder();
			var pages = new ArrayList<String>();

			for(var notif : notifs){
				var formattedNotif = "**ID:** `" + notif.getId() + "` is scheduled for `" + TimeUtils.format(notif.getNotificationTime()) + "`\n";
				if(notifMessage.length() + formattedNotif.length() >= 2048){
					pages.add(notifMessage.toString());
					notifMessage = new StringBuilder();
				}
				notifMessage.append(formattedNotif);
			}
			pages.add(notifMessage.toString());

			ia.acknowledge(true).queue(success -> ia.get(PaginatorModule.class).create(
				ia.getChannel(),
				ia.getUserId(),
				pages.size(),
				(page, embedBuilder) -> embedBuilder
					.setColor(Colors.KITTYBOT_BLUE)
					.setAuthor("Your Notifications", Config.ORIGIN_URL, Category.NOTIFICATION.getEmoteUrl())
					.setDescription(pages.get(page))
					.setTimestamp(Instant.now())
			));
		}

	}

}
