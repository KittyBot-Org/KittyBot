package de.kittybot.kittybot.commands.admin.settings;

import de.kittybot.kittybot.modules.SettingsModule;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionBoolean;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionString;
import de.kittybot.kittybot.slashcommands.application.options.GuildSubCommand;
import de.kittybot.kittybot.slashcommands.GuildCommandContext;
import de.kittybot.kittybot.slashcommands.Options;

public class LeaveMessageCommand extends GuildSubCommand{

	public LeaveMessageCommand(){
		super("leavemessage", "Sets or enables/disables leave messages");
		addOptions(
			new CommandOptionBoolean("enabled", "Whether leave messages are enabled"),
			new CommandOptionString("message", "The leave message template")
		);
	}

	@Override
	public void run(Options options, GuildCommandContext ctx){
		var settings = ctx.get(SettingsModule.class);
		var returnMessage = "";
		if(options.has("enabled")){
			var enabled = options.getBoolean("enabled");
			settings.setLeaveMessagesEnabled(ctx.getGuildId(), enabled);
			returnMessage += "Leave messages `" + (enabled ? "enabled" : "disabled") + "`\n";
		}

		if(options.has("message")){
			var message = options.getString("message");
			settings.setLeaveMessage(ctx.getGuildId(), message);
			returnMessage += "Leave message to:\n" + message + "\n";
		}

		if(returnMessage.isBlank()){
			ctx.reply("Leave message `" + (settings.areLeaveMessagesEnabled(ctx.getGuildId()) ? "enabled" : "disabled") + "` and set to:\n" + settings.getLeaveMessage(ctx.getGuildId()));
			return;
		}
		ctx.reply(returnMessage);
	}

}
