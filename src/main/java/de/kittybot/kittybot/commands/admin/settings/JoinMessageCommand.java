package de.kittybot.kittybot.commands.admin.settings;

import de.kittybot.kittybot.modules.SettingsModule;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionBoolean;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionString;
import de.kittybot.kittybot.slashcommands.application.options.GuildSubCommand;
import de.kittybot.kittybot.slashcommands.interaction.GuildInteraction;
import de.kittybot.kittybot.slashcommands.interaction.Options;

public class JoinMessageCommand extends GuildSubCommand{

	public JoinMessageCommand(){
		super("joinmessage", "Sets or enable/disables join messages");
		addOptions(
			new CommandOptionBoolean("enabled", "Whether join messages are enabled"),
			new CommandOptionString("message", "The join message template")
		);
	}

	@Override
	public void run(Options options, GuildInteraction ia){
		var settings = ia.get(SettingsModule.class);
		var returnMessage = "";
		if(options.has("enabled")){
			var enabled = options.getBoolean("enabled");
			settings.setJoinMessagesEnabled(ia.getGuildId(), enabled);
			returnMessage += "Join messages `" + (enabled ? "enabled" : "disabled") + "`\n";
		}

		if(options.has("message")){
			var message = options.getString("message");
			settings.setJoinMessage(ia.getGuildId(), message);
			returnMessage += "Join message to:\n" + message + "\n";
		}

		if(returnMessage.isBlank()){
			ia.reply("Join message `" + (settings.areJoinMessagesEnabled(ia.getGuildId()) ? "enabled" : "disabled") + "` and set to:\n" + settings.getJoinMessage(ia.getGuildId()));
			return;
		}
		ia.reply(returnMessage);
	}

}
