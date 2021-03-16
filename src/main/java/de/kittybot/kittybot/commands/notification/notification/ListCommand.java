package de.kittybot.kittybot.commands.notification.notification;

import de.kittybot.kittybot.modules.NotificationModule;
import de.kittybot.kittybot.modules.PaginatorModule;
import de.kittybot.kittybot.slashcommands.application.Category;
import de.kittybot.kittybot.slashcommands.application.options.GuildSubCommand;
import de.kittybot.kittybot.slashcommands.interaction.GuildInteraction;
import de.kittybot.kittybot.slashcommands.interaction.Options;
import de.kittybot.kittybot.utils.Colors;
import de.kittybot.kittybot.utils.Config;
import de.kittybot.kittybot.utils.TimeUtils;

import java.time.Instant;
import java.util.ArrayList;

public class ListCommand extends GuildSubCommand{

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
