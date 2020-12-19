package de.kittybot.kittybot.commands.admin;

import de.kittybot.kittybot.command.Category;
import de.kittybot.kittybot.command.Command;
import de.kittybot.kittybot.command.ctx.CommandContext;
import de.kittybot.kittybot.utils.Utils;

import java.util.List;

public class LogMessagesCommand extends Command{

	public LogMessagesCommand(){
		super("logmessages", "Set's the current channel for log messages", Category.ADMIN);
		addAliases("logs", "logmsgs");
		setUsage(".../<false>");
	}

	@Override
	protected void run(List<String> args, CommandContext ctx){
		var guild = ctx.getGuild().getIdLong();
		var settingsManager = ctx.getGuildSettingsManager();
		if(!args.isEmpty() && Utils.isDisable(args.get(0))){
			settingsManager.setLogMessagesEnabled(guild, false);
			ctx.sendSuccess("Disabled log messages here");
			return;
		}
		settingsManager.setLogChannelId(guild, ctx.getChannel().getIdLong());
		settingsManager.setLogMessagesEnabled(guild, true);
		ctx.sendSuccess("Enabled log messages here");
	}

}
