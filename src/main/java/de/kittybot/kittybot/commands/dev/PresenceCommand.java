package de.kittybot.kittybot.commands.dev;

import de.kittybot.kittybot.slashcommands.application.CommandOptionChoice;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionInteger;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionString;
import de.kittybot.kittybot.slashcommands.application.options.SubCommand;
import de.kittybot.kittybot.slashcommands.context.CommandContext;
import de.kittybot.kittybot.slashcommands.context.Options;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;

public class PresenceCommand extends SubCommand{

	public PresenceCommand(){
		super("presence", "Set the presence & activity");
		addOptions(
			new CommandOptionString("status", "The online status").addChoices(
				new CommandOptionChoice<>("online", OnlineStatus.ONLINE.getKey()),
				new CommandOptionChoice<>("do not disturb", OnlineStatus.DO_NOT_DISTURB.getKey()),
				new CommandOptionChoice<>("idle", OnlineStatus.IDLE.getKey()),
				new CommandOptionChoice<>("offline", OnlineStatus.OFFLINE.getKey()),
				new CommandOptionChoice<>("invisible", OnlineStatus.INVISIBLE.getKey())
			),
			new CommandOptionInteger("activity", "The online status").addChoices(
				new CommandOptionChoice<>("competing", Activity.ActivityType.COMPETING.getKey()),
				new CommandOptionChoice<>("listening", Activity.ActivityType.LISTENING.getKey()),
				new CommandOptionChoice<>("watching", Activity.ActivityType.WATCHING.getKey()),
				new CommandOptionChoice<>("streaming", Activity.ActivityType.STREAMING.getKey()),
				new CommandOptionChoice<>("playing", Activity.ActivityType.DEFAULT.getKey())
			),
			new CommandOptionString("message", "The activity message"),
			new CommandOptionString("streaming-url", "The streaming url if you chose streaming as activity")
		);
		devOnly();
	}


	@Override
	public void run(Options options, CommandContext ctx){
		var status = options.has("status") ? OnlineStatus.fromKey(options.getString("status")) : null;
		var activity = options.has("activity") ? Activity.ActivityType.fromKey(options.getInt("activity")) : null;
		var message = options.has("message") ? options.getString("message") : null;
		var streamingURL = options.has("streaming-url") ? options.getString("streaming-url") : null;

		if(activity == Activity.ActivityType.STREAMING && message == null){
			ctx.error("The message may not be empty for streaming");
			return;
		}
		ctx.getJDA().getPresence().setPresence(status, activity == null ? null : Activity.of(activity, message == null ? "" : message, streamingURL));
		ctx.reply("Successfully set presence");
	}

}