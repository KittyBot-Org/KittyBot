package de.kittybot.kittybot.commands.dev.dev;

import de.kittybot.kittybot.slashcommands.CommandContext;
import de.kittybot.kittybot.slashcommands.Options;
import de.kittybot.kittybot.slashcommands.application.CommandOptionChoice;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionString;
import de.kittybot.kittybot.slashcommands.application.options.SubCommand;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;

public class PresenceCommand extends SubCommand{

	public PresenceCommand(){
		super("presence", "Set the presence & activity");
		addOptions(
			new CommandOptionString("status", "The online status").addChoices(
				new CommandOptionChoice<>(OnlineStatus.ONLINE),
				new CommandOptionChoice<>(OnlineStatus.DO_NOT_DISTURB),
				new CommandOptionChoice<>(OnlineStatus.IDLE),
				new CommandOptionChoice<>(OnlineStatus.OFFLINE),
				new CommandOptionChoice<>(OnlineStatus.INVISIBLE)
			),
			new CommandOptionString("activity", "The online status").addChoices(
				new CommandOptionChoice<>(Activity.ActivityType.COMPETING),
				new CommandOptionChoice<>(Activity.ActivityType.LISTENING),
				new CommandOptionChoice<>(Activity.ActivityType.WATCHING),
				new CommandOptionChoice<>(Activity.ActivityType.STREAMING),
				new CommandOptionChoice<>(Activity.ActivityType.DEFAULT)
			),
			new CommandOptionString("message", "The activity message"),
			new CommandOptionString("streaming-url", "The streaming url if you chose streaming as activity")
		);
		devOnly();
	}


	@Override
	public void run(Options options, CommandContext ctx){
		var status = options.has("status") ? OnlineStatus.valueOf(options.getString("status")) : null;
		var activity = options.has("activity") ? Activity.ActivityType.valueOf(options.getString("activity")) : null;
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