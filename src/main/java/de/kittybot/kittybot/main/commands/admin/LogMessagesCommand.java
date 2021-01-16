package de.kittybot.kittybot.main.commands.admin;

import de.kittybot.kittybot.command.old.Args;
import de.kittybot.kittybot.command.Category;
import de.kittybot.kittybot.command.old.Command;
import de.kittybot.kittybot.command.old.CommandContext;
import de.kittybot.kittybot.modules.SettingsModule;
import net.dv8tion.jda.api.Permission;

@SuppressWarnings("unused")
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
		var settingsManager = ctx.get(SettingsModule.class);
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