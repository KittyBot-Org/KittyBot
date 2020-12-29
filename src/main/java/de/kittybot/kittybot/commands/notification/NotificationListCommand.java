package de.kittybot.kittybot.commands.notification;

import de.kittybot.kittybot.command.Category;
import de.kittybot.kittybot.command.Command;
import de.kittybot.kittybot.command.ctx.CommandContext;
import de.kittybot.kittybot.objects.Notification;
import de.kittybot.kittybot.utils.TimeUtils;
import net.dv8tion.jda.api.EmbedBuilder;

import java.util.List;
import java.util.Set;

public class NotificationListCommand extends Command{

	public NotificationListCommand(Command parent){
		super(parent, "list", "Lists a Notification", Category.NOTIFICATION);
		addAliases("ls");
		setUsage("<@User/id/all>");
	}

	@Override
	public void run(List<String> args, CommandContext ctx){
		Set<Notification> notifs;
		if(args.isEmpty()){
			notifs = ctx.getNotificationManager().get(ctx.getUser().getIdLong());
		}
		else if(ctx.getCommandManager().getOwnerIds().contains(ctx.getUser().getIdLong())){
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
		ctx.sendSuccess(new EmbedBuilder().setAuthor("Your Notifications", ctx.getConfig().getString("origin_url"), ctx.getSelfUser().getEffectiveAvatarUrl()).setDescription(message.toString()));
	}

}