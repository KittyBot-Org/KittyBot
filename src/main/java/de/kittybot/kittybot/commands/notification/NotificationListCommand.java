package de.kittybot.kittybot.commands.notification;

import de.kittybot.kittybot.command.Args;
import de.kittybot.kittybot.command.Category;
import de.kittybot.kittybot.command.Command;
import de.kittybot.kittybot.command.CommandContext;
import de.kittybot.kittybot.objects.Notification;
import de.kittybot.kittybot.utils.Config;
import de.kittybot.kittybot.utils.TimeUtils;
import net.dv8tion.jda.api.EmbedBuilder;

import java.util.Set;

public class NotificationListCommand extends Command{

	public NotificationListCommand(Command parent){
		super(parent, "list", "Lists a Notification", Category.NOTIFICATION);
		addAliases("ls");
		setUsage("<@User/id/all>");
	}

	@Override
	public void run(Args args, CommandContext ctx){
		Set<Notification> notifs;
		if(args.isEmpty()){
			notifs = ctx.getNotificationManager().get(ctx.getUser().getIdLong());
		}
		else if(Config.OWNER_IDS.contains(ctx.getUser().getIdLong())){
			notifs = ctx.getNotificationManager().get(Long.parseLong(args.get(0)));
		}
		else{
			ctx.sendError("Error while retrieving notifications");
			return;
		}
		if(notifs.isEmpty()){
			ctx.sendError("No notifications found");
			return;
		}
		var message = new StringBuilder();
		for(var notif : notifs){
			message.append("**").append(notif.getId()).append("**").append(" scheduled for `").append(TimeUtils.format(notif.getNotificationTime())).append("`").append("\n");
		}
		ctx.sendSuccess(new EmbedBuilder().setAuthor("Your Notifications", Config.ORIGIN_URL, ctx.getSelfUser().getEffectiveAvatarUrl()).setDescription(message.toString()));
	}

}
