package de.kittybot.kittybot.commands.admin.settings;

import de.kittybot.kittybot.modules.SettingsModule;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionBoolean;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionGuildChannel;
import de.kittybot.kittybot.slashcommands.application.options.GuildSubCommand;
import de.kittybot.kittybot.slashcommands.GuildCommandContext;
import de.kittybot.kittybot.slashcommands.Options;
import de.kittybot.kittybot.utils.MessageUtils;

public class LogMessagesCommand extends GuildSubCommand{

	public LogMessagesCommand(){
		super("logmessages", "Sets the logging channel or enable/disables log messages");
		addOptions(
			new CommandOptionBoolean("enabled", "Whether log messages are enabled"),
			new CommandOptionGuildChannel("channel", "The log message channel")
		);
	}

	@Override
	public void run(Options options, GuildCommandContext ctx){
		var settings = ctx.get(SettingsModule.class);
		var returnMessage = "";
		if(options.has("enabled")){
			var enabled = options.getBoolean("enabled");
			settings.setLogMessagesEnabled(ctx.getGuildId(), enabled);
			returnMessage += "Log messages `" + (enabled ? "enabled" : "disabled") + "`\n";
		}

		if(options.has("channel")){
			var channel = options.getTextChannel("channel");
			settings.setLogChannelId(ctx.getGuildId(), channel.getIdLong());
			returnMessage += "Log channel to:\n" + channel.getAsMention() + "\n";
		}

		if(returnMessage.isBlank()){
			ctx.reply("Log message `" + (settings.areLogMessagesEnabled(ctx.getGuildId()) ? "enabled" : "disabled") + "` and send to channel " +
				MessageUtils.getChannelMention(settings.getLogChannelId(ctx.getGuildId())));
			return;
		}
		ctx.reply(returnMessage);
	}

}
