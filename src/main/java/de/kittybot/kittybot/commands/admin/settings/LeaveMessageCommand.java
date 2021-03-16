package de.kittybot.kittybot.commands.admin.settings;

import de.kittybot.kittybot.modules.SettingsModule;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionBoolean;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionString;
import de.kittybot.kittybot.slashcommands.application.options.GuildSubCommand;
import de.kittybot.kittybot.slashcommands.interaction.GuildInteraction;
import de.kittybot.kittybot.slashcommands.interaction.Options;

public class LeaveMessageCommand extends GuildSubCommand{

	public LeaveMessageCommand(){
		super("leavemessage", "Sets or enable/disables leave messages");
		addOptions(
			new CommandOptionBoolean("enabled", "Whether leave messages are enabled"),
			new CommandOptionString("message", "The leave message template")
		);
	}

	@Override
	public void run(Options options, GuildInteraction ia){
		var settings = ia.get(SettingsModule.class);
		var returnMessage = "";
		if(options.has("enabled")){
			var enabled = options.getBoolean("enabled");
			settings.setLeaveMessagesEnabled(ia.getGuildId(), enabled);
			returnMessage += "Leave messages `" + (enabled ? "enabled" : "disabled") + "`\n";
		}

		if(options.has("message")){
			var message = options.getString("message");
			settings.setLeaveMessage(ia.getGuildId(), message);
			returnMessage += "Leave message to:\n" + message + "\n";
		}

		if(returnMessage.isBlank()){
			ia.reply("Leave message `" + (settings.areLeaveMessagesEnabled(ia.getGuildId()) ? "enabled" : "disabled") + "` and set to:\n" + settings.getLeaveMessage(ia.getGuildId()));
			return;
		}
		ia.reply(returnMessage);
	}

}
