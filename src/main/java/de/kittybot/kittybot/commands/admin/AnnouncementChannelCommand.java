package de.kittybot.kittybot.commands.admin;

import de.kittybot.kittybot.command.Args;
import de.kittybot.kittybot.command.Category;
import de.kittybot.kittybot.command.Command;
import de.kittybot.kittybot.command.ctx.CommandContext;
import de.kittybot.kittybot.utils.Utils;
import net.dv8tion.jda.api.Permission;

import java.util.List;

public class AnnouncementChannelCommand extends Command{

	public AnnouncementChannelCommand(){
		super("announcementchannel", "Set's the current channel as announcement channel", Category.ADMIN);
		addAliases("announce", "achannel");
		setUsage(".../<false>");
		addPermissions(Permission.ADMINISTRATOR);
	}

	@Override
	protected void run(Args args, CommandContext ctx){
		var guild = ctx.getGuildId();
		var settingsManager = ctx.getGuildSettingsManager();
		if(!args.isEmpty() && args.isDisable(0)){
			settingsManager.setAnnouncementChannelId(guild, -1L);
			ctx.sendSuccess("Disabled announcements here");
			return;
		}
		settingsManager.setAnnouncementChannelId(guild, ctx.getChannelId());
		ctx.sendSuccess("Enabled announcements here\nDon't forgot to enable join/leave announcements");
	}

}
