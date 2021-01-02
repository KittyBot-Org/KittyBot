package de.kittybot.kittybot.commands.admin;

import de.kittybot.kittybot.command.Args;
import de.kittybot.kittybot.command.Category;
import de.kittybot.kittybot.command.Command;
import de.kittybot.kittybot.command.ctx.CommandContext;
import de.kittybot.kittybot.utils.Utils;
import net.dv8tion.jda.api.Permission;

import java.util.List;

public class LogMessagesCommand extends Command{

	public LogMessagesCommand(){
		super("logmessages", "Set's the current channel for log messages", Category.ADMIN);
		addAliases("logs", "logmsgs");
		setUsage(".../<false>");
		addPermissions(Permission.ADMINISTRATOR);
	}

	@Override
	protected void run(Args args, CommandContext ctx){
		var guild = ctx.getGuildId();
		var settingsManager = ctx.getGuildSettingsManager();
		if(!args.isEmpty() && args.isDisable(0)){
			settingsManager.setLogMessagesEnabled(guild, false);
			ctx.sendSuccess("Disabled log messages here");
			return;
		}
		settingsManager.setLogChannelId(guild, ctx.getChannelId());
		settingsManager.setLogMessagesEnabled(guild, true);
		ctx.sendSuccess("Enabled log messages here");
	}

}
