package de.kittybot.kittybot.commands.admin;

import de.kittybot.kittybot.command.Category;
import de.kittybot.kittybot.command.Command;
import de.kittybot.kittybot.command.ctx.CommandContext;
import de.kittybot.kittybot.main.KittyBot;
import de.kittybot.kittybot.utils.Utils;

import java.util.List;

public class AnnouncementChannelCommand extends Command{

	public AnnouncementChannelCommand(){
		super("announcementchannel", "Set's the current channel as announcement channel", Category.ADMIN);
		addAliases("announce", "achannel");
		setUsage(".../<false>");
	}

	@Override
	protected void run(List<String> args, CommandContext ctx){
		var guild = ctx.getGuild().getIdLong();
		var settingsManager = ctx.getGuildSettingsManager();
		if(!args.isEmpty() && Utils.isDisable(args.get(0))){
			settingsManager.setAnnouncementChannelId(guild, -1L);
			ctx.sendSuccess("Disabled announcements here");
			return;
		}
		settingsManager.setAnnouncementChannelId(guild, ctx.getChannel().getIdLong());
		ctx.sendSuccess("Enabled announcements here\nDon't forgot to enable join/leave announcements");
	}

}
