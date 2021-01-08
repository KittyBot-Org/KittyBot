package de.kittybot.kittybot.commands.admin;

import de.kittybot.kittybot.command.Args;
import de.kittybot.kittybot.command.Category;
import de.kittybot.kittybot.command.Command;
import de.kittybot.kittybot.command.CommandContext;
import de.kittybot.kittybot.modules.SettingsModule;
import net.dv8tion.jda.api.Permission;

public class LeaveMessagesCommand extends Command{

	public LeaveMessagesCommand(){
		super("leavemessages", "Enables or sets leave messages for the current channel", Category.ADMIN);
		addAliases("leave", "leavem", "lmessages");
		setUsage(".../<false>/<message>");
		addPermissions(Permission.ADMINISTRATOR);
	}

	@Override
	protected void run(Args args, CommandContext ctx){
		var guild = ctx.getGuildId();
		var settingsManager = ctx.get(SettingsModule.class);
		if(!args.isEmpty() && args.isDisable(0)){
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
		settingsManager.setAnnouncementChannelId(guild, ctx.getChannelId());
		settingsManager.setLeaveMessagesEnabled(guild, true);
		ctx.sendSuccess("Enabled leave messages here");
	}

}
