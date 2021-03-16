package de.kittybot.kittybot.commands.admin.settings;

import de.kittybot.kittybot.modules.SettingsModule;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionBoolean;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionChannel;
import de.kittybot.kittybot.slashcommands.application.options.GuildSubCommand;
import de.kittybot.kittybot.slashcommands.interaction.GuildInteraction;
import de.kittybot.kittybot.slashcommands.interaction.Options;
import de.kittybot.kittybot.slashcommands.interaction.response.InteractionResponse;
import de.kittybot.kittybot.utils.MessageUtils;

public class LogMessagesCommand extends GuildSubCommand{

	public LogMessagesCommand(){
		super("logmessages", "Sets the logging channel or enable/disables log messages");
		addOptions(
			new CommandOptionBoolean("enabled", "Whether log messages are enabled"),
			new CommandOptionChannel("channel", "The log message channel")
		);
	}

	@Override
	public void run(Options options, GuildInteraction ia){
		var settings = ia.get(SettingsModule.class);
		var returnMessage = "";
		if(options.has("enabled")){
			var enabled = options.getBoolean("enabled");
			settings.setLogMessagesEnabled(ia.getGuildId(), enabled);
			returnMessage += "Log messages `" + (enabled ? "enabled" : "disabled") + "`\n";
		}

		if(options.has("channel")){
			var channel = options.getTextChannel("channel");
			settings.setLogChannelId(ia.getGuildId(), channel.getIdLong());
			returnMessage += "Log channel to:\n" + channel.getAsMention() + "\n";
		}

		if(returnMessage.isBlank()){
			ia.reply(new InteractionResponse.Builder().setContent("Log message `" + (settings.areLogMessagesEnabled(ia.getGuildId()) ? "enabled" : "disabled") + "` and send to channel " +
				MessageUtils.getChannelMention(settings.getLogChannelId(ia.getGuildId()))).build());
			return;
		}
		ia.reply(new InteractionResponse.Builder().setContent(returnMessage).build());
	}

}
