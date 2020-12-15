package de.kittybot.kittybot.commands.admin;

import de.kittybot.kittybot.command.Category;
import de.kittybot.kittybot.command.Command;
import de.kittybot.kittybot.command.ctx.CommandContext;
import de.kittybot.kittybot.main.KittyBot;
import de.kittybot.kittybot.utils.Utils;

import java.util.List;

public class LeaveMessagesCommand extends Command{

	public LeaveMessagesCommand(KittyBot main){
		super("leavemessages", "Enables or sets leave messages for the current channel", Category.ADMIN);
		addAliases("leave", "leavem", "lmessages");
		setUsage(".../<false>/<message>");
	}

	@Override
	protected void run(List<String> args, CommandContext ctx){
		var guild = ctx.getGuild().getIdLong();
		var settingsManager = ctx.getCommandManager().getGuildSettingsManager();
		if(!args.isEmpty() && Utils.isDisable(args.get(0))){
			settingsManager.setLeaveMessagesEnabled(guild, false);
			ctx.sendSuccess("Disabled leave messages here");
			return;
		}
		else if(!args.isEmpty()){
			settingsManager.setLeaveMessage(guild, ctx.getRawMessage());
			settingsManager.setLeaveMessagesEnabled(guild, true);
			ctx.sendSuccess("Leave message enabled & set to:\n" + ctx.getRawMessage());
			return;
		}
		settingsManager.setAnnouncementChannelId(guild, ctx.getChannel().getIdLong());
		settingsManager.setLeaveMessagesEnabled(guild, true);
		ctx.sendSuccess("Enabled leave messages here");
	}

}
